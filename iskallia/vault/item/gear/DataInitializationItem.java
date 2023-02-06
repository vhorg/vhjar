package iskallia.vault.item.gear;

import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import net.minecraft.world.item.ItemStack;

public interface DataInitializationItem {
   static void doInitialize(ItemStack stack) {
      doInitialize(stack, JavaRandom.ofNanoTime());
   }

   static void doInitialize(ItemStack stack, RandomSource rand) {
      if (!stack.isEmpty() && stack.getItem() instanceof DataInitializationItem dataInitializationItem) {
         dataInitializationItem.initialize(stack, rand);
      }
   }

   void initialize(ItemStack var1, RandomSource var2);
}
