package iskallia.vault.container.inventory;

import iskallia.vault.container.spi.AbstractElementContainer;
import iskallia.vault.init.ModContainers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ShardTradeContainer extends AbstractElementContainer {
   public ShardTradeContainer(int windowId, Inventory inventory) {
      super(ModContainers.SHARD_TRADE_CONTAINER, windowId, inventory.player);
      this.initSlots(inventory);
   }

   private void initSlots(Inventory playerInventory) {
      for (int row = 0; row < 3; row++) {
         for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 102 + row * 18));
         }
      }

      for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
         this.addSlot(new Slot(playerInventory, hotbarSlot, 8 + hotbarSlot * 18, 160));
      }
   }

   public ItemStack quickMoveStack(Player player, int index) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(index);
      if (slot != null && slot.hasItem()) {
         ItemStack slotStack = slot.getItem();
         itemstack = slotStack.copy();
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

         if (slotStack.getCount() > 0) {
            slot.setChanged();
         }

         if (slotStack.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(player, slotStack);
      }

      return itemstack;
   }

   public boolean stillValid(Player player) {
      return true;
   }
}
