package iskallia.vault.container.oversized;

import javax.annotation.Nonnull;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;

public class OverSizedInvWrapper extends InvWrapper {
   public OverSizedInvWrapper(Container inv) {
      super(inv);
   }

   @Nonnull
   public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
      if (stack.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         ItemStack stackInSlot = this.getInv().getItem(slot);
         if (!stackInSlot.isEmpty()) {
            if (stackInSlot.getCount() >= this.getSlotLimit(slot)) {
               return stack;
            } else if (!ItemHandlerHelper.canItemStacksStack(stack, stackInSlot)) {
               return stack;
            } else if (!this.getInv().canPlaceItem(slot, stack)) {
               return stack;
            } else {
               int storeableCount = this.getSlotLimit(slot) - stackInSlot.getCount();
               if (stack.getCount() <= storeableCount) {
                  if (!simulate) {
                     ItemStack copy = stack.copy();
                     copy.grow(stackInSlot.getCount());
                     this.getInv().setItem(slot, copy);
                     this.getInv().setChanged();
                  }

                  return ItemStack.EMPTY;
               } else {
                  stack = stack.copy();
                  if (!simulate) {
                     ItemStack copy = stack.split(storeableCount);
                     copy.grow(stackInSlot.getCount());
                     this.getInv().setItem(slot, copy);
                     this.getInv().setChanged();
                  } else {
                     stack.shrink(Math.min(stack.getCount(), storeableCount));
                  }

                  return stack;
               }
            }
         } else if (!this.getInv().canPlaceItem(slot, stack)) {
            return stack;
         } else {
            int storeableCount = this.getSlotLimit(slot);
            if (storeableCount < stack.getCount()) {
               stack = stack.copy();
               if (!simulate) {
                  this.getInv().setItem(slot, stack.split(storeableCount));
                  this.getInv().setChanged();
               } else {
                  stack.shrink(Math.min(stack.getCount(), storeableCount));
               }

               return stack;
            } else {
               if (!simulate) {
                  this.getInv().setItem(slot, stack);
                  this.getInv().setChanged();
               }

               return ItemStack.EMPTY;
            }
         }
      }
   }
}
