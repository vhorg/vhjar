package iskallia.vault.block.entity;

import com.google.common.collect.Lists;
import iskallia.vault.VaultMod;
import iskallia.vault.container.VaultDiffuserContainer;
import iskallia.vault.container.base.SimpleSidedContainer;
import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModParticles;
import iskallia.vault.network.message.DiffuserParticleMessage;
import iskallia.vault.util.nbt.NBTHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

public class VaultDiffuserTileEntity extends BlockEntity implements MenuProvider {
   private static final Random rand = new Random();
   private static final BlockPos[] list = new BlockPos[]{
      BlockPos.ZERO.north().east(), BlockPos.ZERO.north().west(), BlockPos.ZERO.south().east(), BlockPos.ZERO.south().west()
   };
   private final VaultDiffuserTileEntity.DiffuserInput inputInv = new VaultDiffuserTileEntity.DiffuserInput();
   private final VaultDiffuserTileEntity.DiffuserOutput outputInv = new VaultDiffuserTileEntity.DiffuserOutput(1, this);
   private ItemStack prevInput = ItemStack.EMPTY;
   private int processTick = 0;
   private int processTickLast = 0;
   private LazyOptional<? extends IItemHandler>[] handlers = SidedInvWrapper.create(
      this.inputInv, new Direction[]{Direction.UP, Direction.EAST, Direction.NORTH, Direction.WEST, Direction.SOUTH}
   );
   private LazyOptional<? extends IItemHandler>[] outputHandlers = SidedInvWrapper.create(this.outputInv, new Direction[]{Direction.DOWN});

   public VaultDiffuserTileEntity(BlockPos pWorldPosition, BlockState pBlockState) {
      super(ModBlocks.VAULT_DIFFUSER_ENTITY, pWorldPosition, pBlockState);
   }

   public static void tick(Level world, BlockPos pos, BlockState state, VaultDiffuserTileEntity tile) {
      tile.processTickLast = tile.processTick;
      if (!tile.prevInput.sameItem(tile.inputInv.getItem(0))) {
         tile.triggerItemChange();
      }

      tile.prevInput = tile.inputInv.getItem(0);
      if (world.isClientSide()) {
         if (tile.getProgressPercent() > 0.0F && tile.getProgressPercent() < 0.3F) {
            Vec3 vec3 = new Vec3(pos.getX() + 0.5, pos.getY() + 0.65, pos.getZ() + 0.5);

            for (BlockPos blockpos : list) {
               float f = blockpos.getX() / 3.5F;
               float f1 = 0.5F;
               float f2 = blockpos.getZ() / 3.5F;
               world.addParticle((ParticleOptions)ModParticles.REVERSE_DIFFUSER.get(), vec3.x, vec3.y, vec3.z, f, f1, f2);
            }
         }

         if (tile.getProgressPercent() > 0.6F && tile.getProgressPercent() < 0.9F) {
            Vec3 vec3 = new Vec3(pos.getX() + 0.5, pos.getY() + 0.75, pos.getZ() + 0.5);

            for (BlockPos blockpos : list) {
               float f = blockpos.getX() / 2.5F;
               float f1 = 1.0F;
               float f2 = blockpos.getZ() / 2.5F;
               world.addParticle((ParticleOptions)ModParticles.DIFFUSER.get(), vec3.x, vec3.y, vec3.z, f, f1, f2);
            }
         }
      }

      if (!world.isClientSide()) {
         if (!tile.canCraft()) {
            tile.resetProcess(world);
         } else {
            if (tile.processTick == 0 && tile.level != null) {
               tile.level
                  .playSound(
                     null,
                     tile.getBlockPos(),
                     SoundEvents.CONDUIT_ACTIVATE,
                     SoundSource.BLOCKS,
                     0.15F + new Random().nextFloat() * 0.05F,
                     0.85F + new Random().nextFloat() * 0.05F
                  );
            }

            tile.processTick++;
            if (tile.processTick >= ModConfigs.VAULT_DIFFUSER.getProcessingTickTime()) {
               tile.finishCraft();
            }

            tile.setChanged();
            if (world instanceof ServerLevel serverWorld) {
               serverWorld.sendBlockUpdated(pos, state, state, 3);
            }
         }
      }
   }

   private void finishCraft() {
      int output = this.getRecipeOutput();
      if (output != 0) {
         if (this.level != null) {
            this.level
               .playSound(
                  null,
                  this.getBlockPos(),
                  SoundEvents.BEACON_POWER_SELECT,
                  SoundSource.BLOCKS,
                  0.15F + new Random().nextFloat() * 0.05F,
                  0.85F + new Random().nextFloat() * 0.05F
               );
         }

         this.processTick = 0;
         ItemStack input = this.inputInv.getItem(0).copy();
         this.inputInv.removeItem(0, 1);
         List<ItemStack> stacksToAdd = this.getUseRelatedOutput(ModConfigs.VAULT_DIFFUSER.generateOutput(output));
         if (this.outputInv.getItem(0).isEmpty()) {
            this.outputInv.setOverSizedStack(0, new OverSizedItemStack(stacksToAdd.get(0), output));
         } else {
            this.outputInv.setOverSizedStack(0, ((OverSizedItemStack)this.outputInv.getOverSizedContents().get(0)).addCopy(output));
         }

         this.outputInv.setChanged();
         ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new DiffuserParticleMessage(this.getBlockPos()));
      }
   }

   private List<ItemStack> getUseRelatedOutput(List<ItemStack> output) {
      List<ItemStack> list = new ArrayList<>();
      output.forEach(stack -> {
         float out = stack.getCount();
         int resultCount = Mth.floor(out);
         if (resultCount < 1 && out > 0.0F && rand.nextFloat() < out) {
            resultCount++;
         }

         ItemStack copyOut = stack.copy();
         copyOut.setCount(resultCount);
         list.add(copyOut);
      });
      return list;
   }

   private boolean canCraft() {
      int output = this.getRecipeOutput();
      return output == 0 ? false : this.canAddItem();
   }

   private boolean canAddItem() {
      ItemStack stack = new ItemStack((ItemLike)ForgeRegistries.ITEMS.getValue(ModConfigs.VAULT_DIFFUSER.getOutputItem()));
      ItemStack slotItem = this.outputInv.getItem(0);
      return slotItem.isEmpty() || ItemStack.isSameItemSameTags(slotItem, stack);
   }

   @Nullable
   private int getRecipeOutput() {
      ItemStack input = this.inputInv.getItem(0);
      if (!this.isValidInput(input)) {
         return 0;
      } else {
         int output;
         if (ModConfigs.VAULT_DIFFUSER.getDiffuserOutputMap().containsKey(input.getItem().getRegistryName())) {
            output = ModConfigs.VAULT_DIFFUSER.getDiffuserOutputMap().get(input.getItem().getRegistryName());
         } else {
            output = ModConfigs.VAULT_DIFFUSER.getDiffuserOutputMap().get(VaultMod.id("default"));
         }

         return output;
      }
   }

   private void triggerItemChange() {
      ItemStack input = this.inputInv.getItem(0).copy();
      if (!this.isValidInput(input) && !input.sameItem(this.prevInput)) {
         this.resetProcess(null);
      } else {
         if (this.canCraft()) {
            this.startProcess(null);
         }
      }
   }

   public boolean isValidInput(ItemStack input) {
      return input.isEmpty() ? false : ModConfigs.VAULT_DIFFUSER.getDiffuserOutputMap().containsKey(input.getItem().getRegistryName());
   }

   private void resetProcess(@Nullable Level world) {
      this.startProcess(world);
   }

   private void startProcess(@Nullable Level world) {
      int prevTick = this.processTick;
      this.processTick = 0;
      this.setChanged();
      if (prevTick != this.processTick && world instanceof ServerLevel serverWorld) {
         serverWorld.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
      }
   }

   public float getProgressPercent() {
      return (float)this.processTick / ModConfigs.VAULT_DIFFUSER.getProcessingTickTime();
   }

   public float getProgressLastPercent() {
      return (float)this.processTickLast / ModConfigs.VAULT_DIFFUSER.getProcessingTickTime();
   }

   public VaultDiffuserTileEntity.DiffuserInput getInputInv() {
      return this.inputInv;
   }

   public VaultDiffuserTileEntity.DiffuserOutput getOutputInv() {
      return this.outputInv;
   }

   public void load(CompoundTag tag) {
      super.load(tag);
      this.outputInv.load(tag.getCompound("output"));
      NBTHelper.deserializeSimpleContainer(this.inputInv, tag.getList("inventory", 10));
      this.processTick = tag.getInt("processTick");
   }

   protected void saveAdditional(CompoundTag tag) {
      super.saveAdditional(tag);
      CompoundTag output = new CompoundTag();
      this.outputInv.save(output);
      tag.put("output", output);
      tag.put("inventory", NBTHelper.serializeSimpleContainer(this.inputInv));
      tag.putInt("processTick", this.processTick);
   }

   public Component getDisplayName() {
      return this.getBlockState().getBlock().getName();
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory inv, Player player) {
      return this.getLevel() == null ? null : new VaultDiffuserContainer(containerId, this.getLevel(), this.getBlockPos(), inv);
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
      if (!this.remove && facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
         if (facing == Direction.UP) {
            return this.handlers[0].cast();
         }

         if (facing == Direction.EAST) {
            return this.handlers[1].cast();
         }

         if (facing == Direction.NORTH) {
            return this.handlers[2].cast();
         }

         if (facing == Direction.WEST) {
            return this.handlers[3].cast();
         }

         if (facing == Direction.SOUTH) {
            return this.handlers[4].cast();
         }

         if (facing == Direction.DOWN) {
            return this.outputHandlers[0].cast();
         }
      }

      return super.getCapability(capability, facing);
   }

   public void invalidateCaps() {
      super.invalidateCaps();
      Arrays.stream(this.handlers).forEach(LazyOptional::invalidate);
      Arrays.stream(this.outputHandlers).forEach(LazyOptional::invalidate);
   }

   public void reviveCaps() {
      super.reviveCaps();
      this.handlers = SidedInvWrapper.create(this.inputInv, new Direction[]{Direction.UP, Direction.EAST, Direction.NORTH, Direction.WEST, Direction.SOUTH});
      this.outputHandlers = SidedInvWrapper.create(this.outputInv, new Direction[]{Direction.DOWN});
   }

   @OnlyIn(Dist.CLIENT)
   public static void spawnDiffuserParticles(BlockPos pos) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
         for (int i = 0; i < 40; i++) {
            Random random = level.getRandom();
            float rotation = random.nextFloat() * 360.0F;
            float length = random.nextFloat() / 5.0F + 0.1F;
            Vec3 offset = new Vec3(Math.cos(rotation) * length, 0.0, Math.sin(rotation) * length);
            level.addParticle(
               (ParticleOptions)ModParticles.DIFFUSER_COMPLETE.get(),
               true,
               pos.getX() + 0.5 + offset.x / 20.0,
               pos.getY() + random.nextDouble() * 0.15F + 1.25,
               pos.getZ() + 0.5 + offset.z / 20.0,
               offset.x * 2.0,
               0.01,
               offset.z * 2.0
            );
         }

         for (int i = 0; i < 20; i++) {
            Random random = level.getRandom();
            float rotation = random.nextFloat() * 360.0F;
            float length = random.nextFloat() / 5.0F + 0.1F;
            Vec3 offset = new Vec3(Math.cos(rotation) * length, 0.0, Math.sin(rotation) * length);
            level.addParticle(
               (ParticleOptions)ModParticles.DIFFUSER_COMPLETE.get(),
               true,
               pos.getX() + 0.5 + offset.x / 20.0,
               pos.getY() + random.nextDouble() * 0.15F + 1.25,
               pos.getZ() + 0.5 + offset.z / 20.0,
               offset.x * 3.0,
               0.01,
               offset.z * 3.0
            );
         }

         for (int i = 0; i < 20; i++) {
            Random random = level.getRandom();
            float rotation = random.nextFloat() * 360.0F;
            float length = (random.nextFloat() + 0.1F) / 25.0F;
            Vec3 offset = new Vec3(Math.cos(rotation) * length, 0.0, Math.sin(rotation) * length);
            level.addParticle(
               (ParticleOptions)ModParticles.DIFFUSER_COMPLETE.get(),
               true,
               pos.getX() + 0.5 + offset.x / 20.0,
               pos.getY() + random.nextDouble() * 0.15F + 1.25,
               pos.getZ() + 0.5 + offset.z / 20.0,
               offset.x * 3.0,
               (random.nextDouble() * 0.3 + 0.25) * (random.nextBoolean() ? -1 : 1),
               offset.z * 3.0
            );
         }
      }
   }

   public class DiffuserInput extends SimpleSidedContainer {
      public DiffuserInput() {
         super(6);
      }

      @Override
      public List<Direction> getAccessibleSlots(int slot) {
         return slot == 0
            ? Lists.newArrayList(new Direction[]{Direction.UP, Direction.EAST, Direction.NORTH, Direction.WEST, Direction.SOUTH})
            : Lists.newArrayList(new Direction[]{Direction.DOWN});
      }

      public boolean canPlaceItem(int slot, ItemStack stack) {
         return ModConfigs.VAULT_DIFFUSER.getDiffuserOutputMap().containsKey(stack.getItem().getRegistryName());
      }

      public void setChanged() {
         super.setChanged();
         VaultDiffuserTileEntity.this.setChanged();
      }

      public ItemStack addItem(ItemStack pStack) {
         ItemStack $$1 = pStack.copy();
         this.moveItemToOccupiedSlotsWithSameType($$1);
         if ($$1.isEmpty()) {
            return ItemStack.EMPTY;
         } else {
            this.moveItemToEmptySlots($$1);
            return $$1.isEmpty() ? ItemStack.EMPTY : $$1;
         }
      }

      private void moveItemToEmptySlots(ItemStack pStack) {
         for (int $$1 = 1; $$1 < this.getContainerSize(); $$1++) {
            ItemStack $$2 = this.getItem($$1);
            if ($$2.isEmpty()) {
               this.setItem($$1, pStack.copy());
               pStack.setCount(0);
               return;
            }
         }
      }

      private void moveItemToOccupiedSlotsWithSameType(ItemStack pStack) {
         for (int $$1 = 1; $$1 < this.getContainerSize(); $$1++) {
            ItemStack $$2 = this.getItem($$1);
            if (ItemStack.isSameItemSameTags($$2, pStack)) {
               this.moveItemsBetweenStacks(pStack, $$2);
               if (pStack.isEmpty()) {
                  return;
               }
            }
         }
      }

      private void moveItemsBetweenStacks(ItemStack p_19186_, ItemStack p_19187_) {
         int $$2 = Math.min(this.getMaxStackSize(), p_19187_.getMaxStackSize());
         int $$3 = Math.min(p_19186_.getCount(), $$2 - p_19187_.getCount());
         if ($$3 > 0) {
            p_19187_.grow($$3);
            p_19186_.shrink($$3);
            this.setChanged();
         }
      }
   }

   public class DiffuserOutput extends OverSizedInventory implements WorldlyContainer {
      private final Map<Direction, Set<Integer>> cachedSidedSlots = new HashMap<>();

      public DiffuserOutput(int size, BlockEntity tile) {
         super(size, tile);
         this.cacheSlots();
      }

      private void cacheSlots() {
         IntStream.range(0, this.getContainerSize())
            .forEach(slot -> this.getAccessibleSlots(slot).forEach(dir -> this.cachedSidedSlots.computeIfAbsent(dir, side -> new HashSet<>()).add(slot)));
      }

      public List<Direction> getAccessibleSlots(int slot) {
         return Lists.newArrayList(new Direction[]{Direction.UP, Direction.EAST, Direction.NORTH, Direction.WEST, Direction.SOUTH, Direction.DOWN});
      }

      public int[] getSlotsForFace(Direction side) {
         return Optional.ofNullable(this.cachedSidedSlots.get(side)).map(Collection::stream).orElse(Stream.empty()).mapToInt(Integer::intValue).toArray();
      }

      public boolean canPlaceItem(int pIndex, ItemStack pStack) {
         return false;
      }

      public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
         return false;
      }

      public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
         return this.cachedSidedSlots.getOrDefault(side, Collections.emptySet()).contains(slot);
      }
   }
}
