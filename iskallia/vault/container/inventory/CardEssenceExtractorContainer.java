package iskallia.vault.container.inventory;

import iskallia.vault.block.entity.CardEssenceExtractorTileEntity;
import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.container.spi.AbstractElementContainer;
import iskallia.vault.init.ModContainers;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.CardItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CardEssenceExtractorContainer extends AbstractElementContainer {
   private final CardEssenceExtractorTileEntity tileEntity;
   private final BlockPos tilePos;

   public CardEssenceExtractorContainer(int windowId, Level world, BlockPos pos, Inventory playerInventory) {
      super(ModContainers.CARD_ESSENCE_EXTRACTOR_CONTAINER, windowId, playerInventory.player);
      this.tilePos = pos;
      if (world.getBlockEntity(this.tilePos) instanceof CardEssenceExtractorTileEntity craftingStationTileEntity) {
         this.tileEntity = craftingStationTileEntity;
         this.initSlots(playerInventory);
      } else {
         this.tileEntity = null;
      }
   }

   private void initSlots(Inventory playerInventory) {
      for (int row = 0; row < 3; row++) {
         for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
         }
      }

      for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
         this.addSlot(new Slot(playerInventory, hotbarSlot, 8 + hotbarSlot * 18, 142));
      }

      SimpleContainer ct = this.tileEntity.getInventory();
      this.addSlot(new Slot(ct, 0, 16, 33) {
         public boolean mayPlace(ItemStack stack) {
            return stack.isEmpty() ? true : stack.is(ModItems.CARD) || stack.is(ModItems.CARD_DECK);
         }
      });
      this.addSlot(new Slot(ct, 1, 106, 28) {
         public boolean mayPlace(ItemStack stack) {
            return stack.isEmpty() ? true : stack.getItem() instanceof CardItem;
         }
      });
      this.addSlot(new Slot(ct, 2, 142, 28) {
         public boolean mayPlace(ItemStack stack) {
            return stack.isEmpty();
         }
      });
   }

   public BlockPos getTilePos() {
      return this.tilePos;
   }

   public CardEssenceExtractorTileEntity getTileEntity() {
      return this.tileEntity;
   }

   public ItemStack quickMoveStack(Player playerIn, int index) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(index);
      if (slot != null && slot.hasItem()) {
         ItemStack slotStack = slot.getItem();
         itemstack = slotStack.copy();
         if (index >= 0 && index < 36 && this.moveItemStackTo(slotStack, 36, this.slots.size(), false)) {
            return itemstack;
         }

         if (index >= 0 && index < 27) {
            if (!this.moveItemStackTo(slotStack, 27, 36, false)) {
               return ItemStack.EMPTY;
            }
         } else if (index >= 27 && index < 36) {
            if (!this.moveItemStackTo(slotStack, 0, 27, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(slotStack, 0, 36, false)) {
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

         slot.onTake(playerIn, slotStack);
      }

      return itemstack;
   }

   public boolean stillValid(Player player) {
      return this.tileEntity == null ? false : OverSizedInventory.stillValidTile().test(this.tileEntity, this.player);
   }
}
