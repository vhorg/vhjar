package iskallia.vault.container.oversized;

import iskallia.vault.container.slot.TabSlot;
import java.util.function.Predicate;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class OverSizedTabSlot extends TabSlot {
   private Predicate<ItemStack> filter = stack -> true;
   private Runnable slotChangeListener = () -> {};

   public OverSizedTabSlot(Container container, int index, int x, int y) {
      super(container, index, x, y);
      this.setFilter(stack -> container.canPlaceItem(this.getSlotIndex(), stack));
   }

   public OverSizedTabSlot setFilter(Predicate<ItemStack> filter) {
      this.filter = this.filter.and(filter);
      return this;
   }

   public void addListener(Runnable run) {
      Runnable existing = this.slotChangeListener;
      this.slotChangeListener = () -> {
         existing.run();
         run.run();
      };
   }

   public boolean mayPlace(ItemStack stack) {
      return !this.filter.test(stack) ? false : super.mayPlace(stack);
   }

   public int getMaxStackSize(ItemStack stack) {
      return this.container.getMaxStackSize();
   }

   public void setChanged() {
      super.setChanged();
      this.slotChangeListener.run();
   }
}
