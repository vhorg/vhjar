package iskallia.vault.util;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class SideOnlyFixer {
   public static int getSlotFor(Inventory inventory, ItemStack stack) {
      for (int i = 0; i < inventory.items.size(); i++) {
         if (!((ItemStack)inventory.items.get(i)).isEmpty() && stackEqualExact(stack, (ItemStack)inventory.items.get(i))) {
            return i;
         }
      }

      return -1;
   }

   private static boolean stackEqualExact(ItemStack stack1, ItemStack stack2) {
      return stack1.getItem() == stack2.getItem() && ItemStack.tagMatches(stack1, stack2);
   }
}
