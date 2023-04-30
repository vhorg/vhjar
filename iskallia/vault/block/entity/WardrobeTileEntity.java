package iskallia.vault.block.entity;

import com.mojang.authlib.GameProfile;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.integration.IntegrationCurios;
import iskallia.vault.util.nbt.NBTHelper;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

public class WardrobeTileEntity extends BlockEntity {
   private Map<EquipmentSlot, ItemStack> equipmentSlots = new EnumMap<>(EquipmentSlot.class);
   private Map<String, Map<Integer, ItemStack>> curiosItems = new HashMap<>();
   private final ItemStackHandler hotbarItems = new ItemStackHandler(9) {
      protected void onContentsChanged(int slot) {
         super.onContentsChanged(slot);
         WardrobeTileEntity.this.setChanged();
         WardrobeTileEntity.this.level
            .sendBlockUpdated(WardrobeTileEntity.this.getBlockPos(), WardrobeTileEntity.this.getBlockState(), WardrobeTileEntity.this.getBlockState(), 3);
      }
   };
   @Nullable
   private UUID owner = null;
   @Nullable
   private Player dummyRenderPlayer;
   private boolean renderSolid = false;

   public WardrobeTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.WARDROBE_TILE_ENTITY, pos, state);
   }

   public void setOwner(UUID owner) {
      this.owner = owner;
      this.setChanged();
      this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
   }

   public boolean isOwner(Player player) {
      return player.getUUID().equals(this.owner);
   }

   protected void saveAdditional(CompoundTag tag) {
      super.saveAdditional(tag);
      NBTHelper.writeMap(tag, "equipmentSlots", this.equipmentSlots, EquipmentSlot::getName, stack -> stack.save(new CompoundTag()));
      NBTHelper.writeMap(
         tag, "curiosItems", this.curiosItems, name -> name, stacks -> NBTHelper.serializeMap(stacks, String::valueOf, stack -> stack.save(new CompoundTag()))
      );
      tag.put("hotbarItems", this.hotbarItems.serializeNBT());
      if (this.owner != null) {
         tag.putUUID("owner", this.owner);
      }

      tag.putBoolean("renderSolid", this.renderSolid);
   }

   public void load(CompoundTag tag) {
      super.load(tag);
      this.equipmentSlots = NBTHelper.<EquipmentSlot, ItemStack>readMap(
            tag, "equipmentSlots", EquipmentSlot::byName, (key, t) -> Optional.of(ItemStack.of((CompoundTag)t))
         )
         .orElse(Map.of());
      this.curiosItems = NBTHelper.<String, Map<Integer, ItemStack>>readMap(
            tag,
            "curiosItems",
            name -> name,
            (name, t) -> Optional.of(
               NBTHelper.deserializeMap((CompoundTag)t, Integer::valueOf, (k, stackTag) -> Optional.of(ItemStack.of((CompoundTag)stackTag)))
            )
         )
         .orElse(Map.of());
      this.hotbarItems.deserializeNBT(tag.getCompound("hotbarItems"));
      this.updateRenderPlayerItems();
      if (tag.contains("owner")) {
         this.owner = tag.getUUID("owner");
      }

      this.renderSolid = tag.getBoolean("renderSolid");
   }

   public void setEquipmentSlot(EquipmentSlot equipmentSlot, ItemStack stack) {
      this.equipmentSlots.put(equipmentSlot, stack);
      this.setChanged();
      this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
   }

   public Player getDummyRenderPlayer() {
      if (this.dummyRenderPlayer == null) {
         this.dummyRenderPlayer = new Player(this.level, this.getBlockPos(), 0.0F, new GameProfile(null, "dummyWardrobeRender")) {
            public boolean isSpectator() {
               return false;
            }

            public boolean isCreative() {
               return false;
            }
         };
         this.updateRenderPlayerItems();
      }

      return this.dummyRenderPlayer;
   }

   public AABB getRenderBoundingBox() {
      return super.getRenderBoundingBox().expandTowards(0.0, 3.0, 0.0);
   }

   private void updateRenderPlayerItems() {
      if (this.dummyRenderPlayer != null) {
         this.dummyRenderPlayer.getInventory().clearContent();
         this.equipmentSlots.forEach((equipmentSlot, stack) -> this.dummyRenderPlayer.setItemSlot(equipmentSlot, stack));
         this.curiosItems
            .forEach((slotKey, stacks) -> stacks.forEach((slot, stack) -> IntegrationCurios.setCurioItemStack(this.dummyRenderPlayer, stack, slotKey, slot)));
         this.dummyRenderPlayer.setItemSlot(EquipmentSlot.MAINHAND, this.hotbarItems.getStackInSlot(0));
      }
   }

   public boolean shouldRenderSolid() {
      return this.renderSolid;
   }

   public ItemStackHandler getHotbarItems() {
      return this.hotbarItems;
   }

   public Map<EquipmentSlot, ItemStack> getEquipmentSlots() {
      return this.equipmentSlots;
   }

   public ItemStack getEquipment(EquipmentSlot equipmentSlot) {
      return this.equipmentSlots.getOrDefault(equipmentSlot, ItemStack.EMPTY);
   }

   public Map<String, Map<Integer, ItemStack>> getCuriosItems() {
      return this.curiosItems;
   }

   public ItemStack getCurio(String slotKey, int index) {
      return this.curiosItems.getOrDefault(slotKey, Collections.emptyMap()).getOrDefault(index, ItemStack.EMPTY);
   }

   public void setCurio(String slotKey, int index, ItemStack stack) {
      this.curiosItems.computeIfAbsent(slotKey, sk -> new HashMap<>()).put(index, stack);
      this.setChanged();
      this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
   }

   public void swap(Player player, boolean swapEverything) {
      this.swapEquipmentSlots(player, swapEverything);
      this.swapCuriosSlots(player, swapEverything);
      this.swapHotbar(player, swapEverything);
      this.level.playSound(null, this.getBlockPos(), SoundEvents.ARMOR_EQUIP_DIAMOND, SoundSource.BLOCKS, 1.0F, 1.0F);
   }

   private void swapHotbar(Player player, boolean swapEverything) {
      for (int slot = 0; slot < this.hotbarItems.getSlots(); slot++) {
         ItemStack slotStack = this.hotbarItems.getStackInSlot(slot);
         if (swapEverything || !slotStack.isEmpty()) {
            this.hotbarItems.setStackInSlot(slot, player.getInventory().getItem(slot));
            player.getInventory().items.set(slot, slotStack);
            this.setChanged();
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
         }
      }
   }

   private void swapEquipmentSlots(Player player, boolean swapEverything) {
      for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
         if (equipmentSlot != EquipmentSlot.MAINHAND
            && (swapEverything || this.equipmentSlots.containsKey(equipmentSlot) && !this.equipmentSlots.get(equipmentSlot).isEmpty())) {
            ItemStack storedCopy = this.equipmentSlots.getOrDefault(equipmentSlot, ItemStack.EMPTY).copy();
            this.equipmentSlots.put(equipmentSlot, player.getItemBySlot(equipmentSlot));
            player.setItemSlot(equipmentSlot, storedCopy);
            this.setChanged();
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
         }
      }
   }

   private void swapCuriosSlots(Player player, boolean swapEverything) {
      IntegrationCurios.getCuriosItemStacks(player).forEach((slotKey, slotStacks) -> {
         Map<Integer, ItemStack> storedStacks = this.curiosItems.getOrDefault(slotKey, Collections.emptyMap());
         slotStacks.forEach(t -> {
            ItemStack stack = (ItemStack)t.getA();
            int slot = (Integer)t.getB();
            if (swapEverything || storedStacks.containsKey(slot) && !storedStacks.get(slot).isEmpty()) {
               ItemStack storedCopy = storedStacks.getOrDefault(slot, ItemStack.EMPTY);
               this.setCurio(slotKey, slot, stack);
               IntegrationCurios.setCurioItemStack(player, storedCopy, slotKey, slot);
               this.setChanged();
               this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
            }
         });
      });
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this, blockEntity -> this.saveWithoutMetadata());
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   public void toggleSolidRender() {
      this.renderSolid = !this.renderSolid;
      this.setChanged();
      this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
   }

   public static class CuriosDynamicStackHandler implements IDynamicStackHandler {
      private NonNullList<ItemStack> previousStacks;
      private final WardrobeTileEntity wardrobeTile;
      private final String slotKey;
      private final int slots;

      public CuriosDynamicStackHandler(WardrobeTileEntity wardrobeTile, String slotKey, int slots) {
         this.wardrobeTile = wardrobeTile;
         this.slotKey = slotKey;
         this.slots = slots;
         this.previousStacks = NonNullList.withSize(slots, ItemStack.EMPTY);
      }

      public void setStackInSlot(int slot, @NotNull ItemStack stack) {
         this.wardrobeTile.setCurio(this.slotKey, slot, stack);
      }

      @NotNull
      public ItemStack getStackInSlot(int slot) {
         return this.wardrobeTile.getCurio(this.slotKey, slot);
      }

      @NotNull
      public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
         if (stack.isEmpty()) {
            return ItemStack.EMPTY;
         } else if (!this.wardrobeTile.getCurio(this.slotKey, slot).isEmpty()) {
            return stack;
         } else if (simulate) {
            return ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - 1);
         } else {
            this.wardrobeTile.setCurio(this.slotKey, slot, stack.split(1));
            return stack;
         }
      }

      public ItemStack extractItem(int slot, int amount, boolean simulate) {
         ItemStack curio = this.wardrobeTile.getCurio(this.slotKey, slot);
         if (amount < 1 || curio.isEmpty()) {
            return ItemStack.EMPTY;
         } else if (simulate) {
            return curio;
         } else {
            this.wardrobeTile.setCurio(this.slotKey, slot, ItemStack.EMPTY);
            return curio;
         }
      }

      public int getSlotLimit(int slot) {
         return 1;
      }

      public boolean isItemValid(int slot, @NotNull ItemStack stack) {
         return true;
      }

      public void setPreviousStackInSlot(int slot, @NotNull ItemStack stack) {
         this.previousStacks.set(slot, stack);
      }

      public ItemStack getPreviousStackInSlot(int slot) {
         this.validateSlotIndex(slot);
         return (ItemStack)this.previousStacks.get(slot);
      }

      private void validateSlotIndex(int slot) {
         if (slot < 0 || slot >= this.slots) {
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + this.slots + ")");
         }
      }

      public int getSlots() {
         return this.slots;
      }

      public void grow(int amount) {
         this.previousStacks = getResizedList(this.previousStacks.size() + amount, this.previousStacks);
      }

      public void shrink(int amount) {
         this.previousStacks = getResizedList(this.previousStacks.size() - amount, this.previousStacks);
      }

      private static NonNullList<ItemStack> getResizedList(int size, NonNullList<ItemStack> stacks) {
         NonNullList<ItemStack> newList = NonNullList.withSize(Math.max(0, size), ItemStack.EMPTY);

         for (int i = 0; i < newList.size() && i < stacks.size(); i++) {
            newList.set(i, (ItemStack)stacks.get(i));
         }

         return newList;
      }

      public CompoundTag serializeNBT() {
         return new CompoundTag();
      }

      public void deserializeNBT(CompoundTag nbt) {
      }
   }
}
