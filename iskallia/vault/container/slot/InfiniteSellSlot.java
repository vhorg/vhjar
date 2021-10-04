package iskallia.vault.container.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class InfiniteSellSlot extends SellSlot {
   public InfiniteSellSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
      super(inventoryIn, index, xPosition, yPosition);
   }

   public ItemStack func_75209_a(int amount) {
      return this.func_75211_c().func_77946_l();
   }
}
