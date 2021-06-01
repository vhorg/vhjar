package iskallia.vault.util;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

public class SideOnlyFixer {
   public static int getSlotFor(PlayerInventory inventory, ItemStack stack) {
      for (int i = 0; i < inventory.field_70462_a.size(); i++) {
         if (!((ItemStack)inventory.field_70462_a.get(i)).func_190926_b() && stackEqualExact(stack, (ItemStack)inventory.field_70462_a.get(i))) {
            return i;
         }
      }

      return -1;
   }

   private static boolean stackEqualExact(ItemStack stack1, ItemStack stack2) {
      return stack1.func_77973_b() == stack2.func_77973_b() && ItemStack.func_77970_a(stack1, stack2);
   }
}
