package iskallia.vault.block.entity;

import iskallia.vault.VaultMod;
import iskallia.vault.container.VaultJewelApplicationStationContainer;
import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModSounds;
import iskallia.vault.item.tool.JewelItem;
import iskallia.vault.item.tool.ToolItem;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class VaultJewelApplicationStationTileEntity extends BlockEntity implements MenuProvider {
   private int totalSizeInJewels = 0;
   private final OverSizedInventory inventory = new OverSizedInventory(61, this) {
      @Override
      public void setChanged() {
         super.setChanged();
         ItemStack result = VaultJewelApplicationStationTileEntity.this.getToolItem().copy();
         VaultJewelApplicationStationTileEntity.this.totalSizeInJewels = 0;

         for (int i = 0; i < 60; i++) {
            if (VaultJewelApplicationStationTileEntity.this.getJewelItem(i).getItem() instanceof JewelItem) {
               if (result.getItem() instanceof ToolItem) {
                  VaultJewelApplicationStationTileEntity.applyJewel(result, VaultJewelApplicationStationTileEntity.this.getJewelItem(i));
               }

               VaultGearData data = VaultGearData.read(VaultJewelApplicationStationTileEntity.this.getJewelItem(i));
               Iterator var4 = data.getModifiers(ModGearAttributes.JEWEL_SIZE, VaultGearData.Type.ALL_MODIFIERS).iterator();
               if (var4.hasNext()) {
                  VaultGearAttributeInstance<Integer> sizeAttribute = (VaultGearAttributeInstance<Integer>)var4.next();
                  VaultJewelApplicationStationTileEntity.this.totalSizeInJewels = VaultJewelApplicationStationTileEntity.this.totalSizeInJewels
                     + sizeAttribute.getValue();
               }
            }
         }

         VaultJewelApplicationStationTileEntity.this.renderedTool = result.copy();
      }
   };
   private ItemStack renderedTool = this.getToolItem();

   public int getTotalSizeInJewels() {
      return this.totalSizeInJewels;
   }

   public void applyJewels() {
      for (int i = 0; i < 60; i++) {
         ItemStack result = this.getToolItem();
         if (this.getJewelItem(i).getItem() instanceof JewelItem && result.getItem() instanceof ToolItem && ToolItem.applyJewel(result, this.getJewelItem(i))) {
            this.setJewelItem(i, ItemStack.EMPTY);
         }

         this.getInventory().setItem(0, result);
      }

      if (this.level != null) {
         this.level.playSound(null, this.getBlockPos(), ModSounds.ARTISAN_SMITHING, SoundSource.BLOCKS, 0.2F, this.level.random.nextFloat() * 0.1F + 0.9F);
      }
   }

   public static boolean applyJewel(ItemStack tool, ItemStack jewel) {
      VaultGearData toolData = VaultGearData.read(tool);
      VaultGearData jewelData = VaultGearData.read(jewel);
      int capacity = toolData.getFirstValue(ModGearAttributes.TOOL_CAPACITY).orElse(0);
      int size = jewelData.getFirstValue(ModGearAttributes.JEWEL_SIZE).orElse(0);
      toolData.updateAttribute(ModGearAttributes.TOOL_CAPACITY, Integer.valueOf(capacity - size));

      for (VaultGearModifier.AffixType affix : VaultGearModifier.AffixType.explicits()) {
         for (VaultGearModifier<?> jewelModifier : jewelData.getModifiers(affix)) {
            if (jewelModifier.getAttribute() != ModGearAttributes.HAMMER_SIZE
               || toolData.get(ModGearAttributes.HAMMERING, VaultGearAttributeTypeMerger.anyTrue())) {
               mergeModifier(affix, toolData, jewelModifier);
            }
         }
      }

      for (VaultGearAttributeInstance<Integer> hammerSizeModifier : toolData.getModifiers(ModGearAttributes.HAMMER_SIZE, VaultGearData.Type.ALL_MODIFIERS)) {
         hammerSizeModifier.setValue(Math.min(hammerSizeModifier.getValue(), 7));
      }

      toolData.setItemLevel(Math.max(toolData.getItemLevel(), jewelData.getItemLevel()));
      toolData.write(tool);
      return true;
   }

   private static <T> void mergeModifier(VaultGearModifier.AffixType affix, VaultGearData targetData, VaultGearModifier<T> toAdd) {
      List<VaultGearAttributeInstance<T>> matching = targetData.getModifiers(toAdd.getAttribute(), VaultGearData.Type.EXPLICIT_MODIFIERS);
      if (matching.isEmpty()) {
         targetData.addModifier(affix, new VaultGearModifier<>(toAdd.getAttribute(), toAdd.getValue()));
      } else {
         matching.stream().findFirst().ifPresent(current -> current.setValue(merge((VaultGearAttributeInstance<T>)current, toAdd.getValue())));
      }
   }

   private static <T> T merge(VaultGearAttributeInstance<T> attributeInstance, T toAdd) {
      VaultGearAttribute<T> attribute = attributeInstance.getAttribute();
      if (attribute.getAttributeComparator() != null) {
         return attribute.getAttributeComparator().merge(attributeInstance.getValue(), toAdd);
      } else {
         VaultMod.LOGGER.error("Unsupported merging on attribute " + attribute.getRegistryName(), new UnsupportedOperationException());
         return attributeInstance.getValue();
      }
   }

   public boolean stillValid(Player player) {
      return this.level != null && this.level.getBlockEntity(this.worldPosition) == this ? this.inventory.stillValid(player) : false;
   }

   public VaultJewelApplicationStationTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.VAULT_JEWEL_APPLICATION_STATION_ENTITY, pos, state);
   }

   public OverSizedInventory getInventory() {
      return this.inventory;
   }

   public ItemStack getToolItem() {
      return this.inventory.getItem(0);
   }

   public ItemStack getRenderedTool() {
      return this.renderedTool;
   }

   public ItemStack getJewelItem(int i) {
      i = Mth.clamp(i, 0, 60);
      return this.inventory.getItem(i + 1);
   }

   public void setJewelItem(int i, ItemStack stack) {
      i = Mth.clamp(i, 0, 60);
      this.inventory.setItem(i + 1, stack);
   }

   public List<ItemStack> getJewels() {
      List<ItemStack> stacks = new ArrayList<>();

      for (int i = 0; i < 60; i++) {
         stacks.add(this.inventory.getItem(i + 1));
      }

      return stacks;
   }

   public void load(CompoundTag tag) {
      super.load(tag);
      this.renderedTool = ItemStack.of(tag.getCompound("renderedItem"));
      this.totalSizeInJewels = tag.getInt("size");
      this.inventory.load(tag);
   }

   protected void saveAdditional(CompoundTag tag) {
      super.saveAdditional(tag);
      tag.put("renderedItem", this.renderedTool.save(new CompoundTag()));
      tag.putInt("size", this.totalSizeInJewels);
      this.inventory.save(tag);
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public Component getDisplayName() {
      return this.getBlockState().getBlock().getName();
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory inv, Player player) {
      return this.getLevel() == null ? null : new VaultJewelApplicationStationContainer(containerId, this.getLevel(), this.getBlockPos(), inv);
   }
}
