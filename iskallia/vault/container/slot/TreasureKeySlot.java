package iskallia.vault.container.slot;

import iskallia.vault.container.oversized.OverSizedTabSlot;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class TreasureKeySlot extends OverSizedTabSlot {
   private final ItemStack keyStack;

   public TreasureKeySlot(Container container, int index, int x, int y, ItemStack keyStack) {
      super(container, index, x, y);
      this.keyStack = keyStack;
      this.setFilter(stack -> stack.sameItem(this.keyStack));
   }

   public ItemStack getKeyStack() {
      return this.keyStack;
   }
}
