package iskallia.vault.config.entry;

import com.google.gson.annotations.Expose;
import java.util.Random;
import net.minecraft.world.item.ItemStack;

public class ChanceItemStackEntry extends ItemStackEntry {
   @Expose
   private final float chance;

   public ChanceItemStackEntry(ItemStack stack, int min, int max, float chance) {
      super(stack, min, max);
      this.chance = chance;
   }

   public float getChance() {
      return this.chance;
   }

   public ChanceItemStackEntry adjustChance(float chance) {
      return new ChanceItemStackEntry(this.getMatchingStack(), this.getMinCount(), this.getMaxCount(), this.getChance() + chance);
   }

   @Override
   public ItemStack createItemStack(Random rand) {
      return rand.nextFloat() >= this.getChance() ? ItemStack.EMPTY : super.createItemStack(rand);
   }
}
