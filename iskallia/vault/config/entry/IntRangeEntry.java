package iskallia.vault.config.entry;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;

public class IntRangeEntry {
   private static final RandomSource rand = JavaRandom.ofNanoTime();
   public static final IntRangeEntry EMPTY = new IntRangeEntry(0, 0);
   @Expose
   private final int min;
   @Expose
   private final int max;

   public IntRangeEntry(int min, int max) {
      this.min = min;
      this.max = max;
   }

   public int getMin() {
      return this.min;
   }

   public int getMax() {
      return this.max;
   }

   public int getRandom() {
      return this.getRandom(rand);
   }

   public int getRandom(RandomSource src) {
      return this.max <= this.min ? this.min : this.min + src.nextInt(this.max - this.min);
   }
}
