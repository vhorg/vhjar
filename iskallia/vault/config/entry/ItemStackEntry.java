package iskallia.vault.config.entry;

import com.google.gson.annotations.Expose;
import java.util.Random;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class ItemStackEntry {
   @Expose
   private final ItemStack stack;
   @Expose
   private final int minCount;
   @Expose
   private final int maxCount;

   public ItemStackEntry(ItemStack stack, int minCount, int maxCount) {
      this.stack = stack;
      this.minCount = minCount;
      this.maxCount = maxCount;
   }

   public ItemStack getMatchingStack() {
      ItemStack created = this.stack.copy();
      created.setCount(this.maxCount);
      return created;
   }

   public ItemStack createItemStack(Random rand) {
      ItemStack created = this.stack.copy();
      created.setCount(this.getRandomAmount(rand));
      return created;
   }

   private int getRandomAmount(Random rand) {
      return Mth.randomBetweenInclusive(rand, this.minCount, this.maxCount);
   }
}
