package iskallia.vault.container.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class RecipeOutputSlot extends Slot {
   public RecipeOutputSlot(Container inventory, int index, int x, int y) {
      super(inventory, index, x, y);
   }

   public boolean mayPlace(ItemStack itemStack) {
      return false;
   }
}
