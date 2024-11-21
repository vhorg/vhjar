package iskallia.vault.container;

import iskallia.vault.block.entity.TransmogTableTileEntity;
import iskallia.vault.container.inventory.TransmogTableInventory;
import iskallia.vault.container.oversized.OverSizedSlotContainer;
import iskallia.vault.container.oversized.OverSizedTabSlot;
import iskallia.vault.container.slot.RecipeOutputSlot;
import iskallia.vault.container.slot.TabSlot;
import iskallia.vault.container.slot.VaultGearSlot;
import iskallia.vault.container.spi.AbstractElementContainer;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModContainers;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModSlotIcons;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TransmogTableContainer extends OverSizedSlotContainer {
   protected TransmogTableInventory internalInventory;
   protected AbstractElementContainer.SlotIndexRange playerInventoryIndexRange;
   protected AbstractElementContainer.SlotIndexRange hotbarIndexRange;
   protected AbstractElementContainer.SlotIndexRange internalInventoryIndexRange;
   protected ResourceLocation selectedModelId;
   private final TransmogTableTileEntity tileEntity;
   private final BlockPos tilePos;

   public TransmogTableContainer(int id, Level level, BlockPos pos, Inventory playerInventory) {
      super(ModContainers.TRANSMOG_TABLE_CONTAINER, id, playerInventory.player);
      this.tilePos = pos;
      if (level.getBlockEntity(this.tilePos) instanceof TransmogTableTileEntity transmogTableTileEntity) {
         this.tileEntity = transmogTableTileEntity;
         this.initInventory();
      } else {
         this.tileEntity = null;
      }
   }

   private void initInventory() {
      this.internalInventory = this.tileEntity.getInternalInventory();
      this.internalInventory.stopOpen(this.player);
      int offsetX = 0;
      int offsetY = 0;
      int containerSlotIndex = 0;

      for (int row = 0; row < 3; row++) {
         for (int column = 0; column < 9; column++) {
            int index = column + (row + 1) * 9;
            int x = 8 + column * 18 + offsetX;
            int y = 84 + row * 18 + offsetY;
            this.addSlot(new TabSlot(this.player.getInventory(), index, x, y));
            containerSlotIndex++;
         }
      }

      this.playerInventoryIndexRange = new AbstractElementContainer.SlotIndexRange(0, containerSlotIndex);

      for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
         int x = 8 + hotbarSlot * 18 + offsetX;
         int y = 142 + offsetY;
         this.addSlot(new TabSlot(this.player.getInventory(), hotbarSlot, x, y));
         containerSlotIndex++;
      }

      this.hotbarIndexRange = new AbstractElementContainer.SlotIndexRange(this.playerInventoryIndexRange.end(), containerSlotIndex);
      this.addSlot(new VaultGearSlot(this.internalInventory, 0, 63, 61) {
         public void setChanged() {
            super.setChanged();
            if (this.getItem().isEmpty()) {
               TransmogTableContainer.this.selectModelId(null);
            }
         }
      });
      containerSlotIndex++;
      this.addSlot((new OverSizedTabSlot(this.internalInventory, 1, 92, 61) {
         @Override
         public void setChanged() {
            super.setChanged();
            TransmogTableContainer.this.slotsChanged(this.container);
         }
      }).setFilter(itemStack -> itemStack.getItem() == ModBlocks.VAULT_BRONZE).setBackground(InventoryMenu.BLOCK_ATLAS, ModSlotIcons.COINS_NO_ITEM));
      containerSlotIndex++;
      this.addSlot(new RecipeOutputSlot(this.internalInventory, this.internalInventory.outputSlotIndex(), 152, 61) {
         public void setChanged() {
            super.setChanged();
            TransmogTableContainer.this.slotsChanged(this.container);
         }
      });
      this.internalInventoryIndexRange = new AbstractElementContainer.SlotIndexRange(this.hotbarIndexRange.end(), ++containerSlotIndex);
   }

   public TransmogTableInventory getInternalInventory() {
      return this.internalInventory;
   }

   public AbstractElementContainer.SlotIndexRange getInternalInventoryIndexRange() {
      return this.internalInventoryIndexRange;
   }

   public ResourceLocation getSelectedModelId() {
      return this.selectedModelId;
   }

   public void selectModelId(ResourceLocation modelId) {
      this.selectedModelId = modelId;
   }

   public ItemStack getPreviewItemStack() {
      ResourceLocation selectedModelId = this.getSelectedModelId();
      if (selectedModelId == null) {
         return ItemStack.EMPTY;
      } else {
         int gearSlotIndex = this.internalInventoryIndexRange.getContainerIndex(0);
         ItemStack gearStack = this.getSlot(gearSlotIndex).getItem();
         if (gearStack.isEmpty()) {
            return ItemStack.EMPTY;
         } else {
            ItemStack displayStack = new ItemStack(gearStack.getItem());
            VaultGearData gearData = VaultGearData.read(displayStack);
            gearData.setState(VaultGearState.IDENTIFIED);
            gearData.createOrReplaceAttributeValue(ModGearAttributes.GEAR_MODEL, selectedModelId);
            gearData.write(displayStack);
            return displayStack;
         }
      }
   }

   public boolean priceFulfilled() {
      Slot bronzeSlot = this.getSlot(this.internalInventoryIndexRange.getContainerIndex(1));
      return bronzeSlot.hasItem() && bronzeSlot.getItem().is(ModBlocks.VAULT_BRONZE) && bronzeSlot.getItem().getCount() >= this.copperCost();
   }

   public int copperCost() {
      Slot gearSlot = this.getSlot(this.internalInventoryIndexRange.getContainerIndex(0));
      ItemStack gearItem = gearSlot.getItem();
      if (gearItem.isEmpty()) {
         return -1;
      } else {
         VaultGearData gearData = VaultGearData.read(gearItem);
         return Mth.clamp(gearData.getItemLevel(), 1, 64);
      }
   }

   public void slotsChanged(Container pInventory) {
      this.tileEntity.setChanged();
      super.slotsChanged(pInventory);
   }

   public boolean stillValid(@Nonnull Player player) {
      return this.tileEntity == null ? false : this.tileEntity.stillValid(player);
   }

   public void removed(@Nonnull Player player) {
      super.removed(player);
      this.internalInventory.stopOpen(player);
   }

   @Nonnull
   public ItemStack quickMoveStack(@Nonnull Player player, int index) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(index);
      if (slot.hasItem()) {
         ItemStack slotStack = slot.getItem();
         itemstack = slotStack.copy();
         if (index >= 0 && index < 36 && this.moveOverSizedItemStackTo(slotStack, slot, 36, this.slots.size(), false)) {
            return itemstack;
         }

         if (index >= 0 && index < 27) {
            if (!this.moveOverSizedItemStackTo(slotStack, slot, 27, 36, false)) {
               return ItemStack.EMPTY;
            }
         } else if (index >= 27 && index < 36) {
            if (!this.moveOverSizedItemStackTo(slotStack, slot, 0, 27, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveOverSizedItemStackTo(slotStack, slot, 0, 36, false)) {
            return ItemStack.EMPTY;
         }

         if (slotStack.getCount() == 0) {
            slot.set(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }

         if (slotStack.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(player, slotStack);
      }

      return itemstack;
   }
}
