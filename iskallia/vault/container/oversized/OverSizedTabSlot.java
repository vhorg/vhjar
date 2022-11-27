package iskallia.vault.container.oversized;

import iskallia.vault.container.slot.TabSlot;
import java.util.function.Predicate;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class OverSizedTabSlot extends TabSlot {
   private Predicate<ItemStack> filter = stack -> true;

   public OverSizedTabSlot(Container container, int index, int x, int y) {
      super(container, index, x, y);
   }

   public OverSizedTabSlot setFilter(Predicate<ItemStack> filter) {
      this.filter = filter;
      return this;
   }

   public boolean mayPlace(ItemStack stack) {
      return !this.filter.test(stack) ? false : super.mayPlace(stack);
   }

   public int getMaxStackSize(ItemStack stack) {
      return this.container.getMaxStackSize();
   }
}
