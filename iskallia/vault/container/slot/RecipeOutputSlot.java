package iskallia.vault.container.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class RecipeOutputSlot extends Slot {
   public RecipeOutputSlot(IInventory inventory, int index, int x, int y) {
      super(inventory, index, x, y);
   }

   public boolean func_75214_a(ItemStack itemStack) {
      return false;
   }
}
