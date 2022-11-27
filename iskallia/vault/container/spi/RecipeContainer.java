package iskallia.vault.container.spi;

import iskallia.vault.util.EntityHelper;
import javax.annotation.Nullable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public abstract class RecipeContainer extends AbstractContainerMenu {
   protected RecipeInventory internalInventory;
   protected Inventory playerInventory;

   protected RecipeContainer(@Nullable MenuType<?> containerType, int windowId, RecipeInventory internalInventory, Player player) {
      super(containerType, windowId);
      this.internalInventory = internalInventory;
      this.playerInventory = player.getInventory();
      this.addInternalInventorySlots();
      this.addPlayerInventorySlots();
   }

   protected abstract void addInternalInventorySlots();

   protected void addPlayerInventorySlots() {
      for (int row = 0; row < 3; row++) {
         for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(this.playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
         }
      }

      for (int col = 0; col < 9; col++) {
         this.addSlot(new Slot(this.playerInventory, col, 8 + col * 18, 142));
      }
   }

   public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
      super.clicked(slotId, dragType, clickTypeIn, player);
      this.internalInventory.updateResult();
   }

   public ItemStack quickMoveStack(Player player, int index) {
      Slot slot = (Slot)this.slots.get(index);
      if (slot != null && slot.hasItem()) {
         ItemStack stackOnSlot = slot.getItem();
         ItemStack copiedStack = stackOnSlot.copy();
         int inventoryFirstIndex = this.internalInventory.getContainerSize();
         int inventoryLastIndex = 36 + inventoryFirstIndex;
         if (index == this.internalInventory.outputSlotIndex()) {
            if (this.moveItemStackTo(stackOnSlot, inventoryFirstIndex, inventoryLastIndex, false)) {
               this.internalInventory.consumeIngredients();
               this.onResultPicked(player, index);
               return copiedStack;
            } else {
               return ItemStack.EMPTY;
            }
         } else if (!this.internalInventory.isIngredientIndex(index)) {
            if (!this.moveItemStackTo(stackOnSlot, 0, this.internalInventory.getContainerSize() - 1, false)) {
               return ItemStack.EMPTY;
            } else {
               if (stackOnSlot.isEmpty()) {
                  slot.set(ItemStack.EMPTY);
               } else {
                  slot.setChanged();
               }

               return stackOnSlot.getCount() == copiedStack.getCount() ? ItemStack.EMPTY : copiedStack;
            }
         } else if (this.moveItemStackTo(stackOnSlot, inventoryFirstIndex, inventoryLastIndex, false)) {
            this.internalInventory.updateResult();
            return copiedStack;
         } else {
            return ItemStack.EMPTY;
         }
      } else {
         return ItemStack.EMPTY;
      }
   }

   public void removed(Player player) {
      super.removed(player);
      this.internalInventory.forEachInput(index -> {
         ItemStack ingredientStack = this.internalInventory.getItem(index);
         if (!ingredientStack.isEmpty()) {
            EntityHelper.giveItem(player, ingredientStack);
         }
      });
   }

   public void onResultPicked(Player player, int index) {
   }
}
