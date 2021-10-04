package iskallia.vault.container.slot;

import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class FilteredSlot extends SlotItemHandler {
   private final Predicate<ItemStack> stackFilter;

   public FilteredSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, Predicate<ItemStack> stackFilter) {
      super(itemHandler, index, xPosition, yPosition);
      this.stackFilter = stackFilter;
   }

   public boolean func_75214_a(ItemStack stack) {
      return !this.stackFilter.test(stack) ? false : super.func_75214_a(stack);
   }
}
