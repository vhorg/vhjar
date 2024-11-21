package iskallia.vault.config.entry;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;

public class FloatRangeEntry {
   private static final RandomSource rand = JavaRandom.ofNanoTime();
   public static final FloatRangeEntry EMPTY = new FloatRangeEntry(0.0F, 0.0F);
   @Expose
   private final float min;
   @Expose
   private final float max;

   public FloatRangeEntry(float min, float max) {
      this.min = min;
      this.max = max;
   }

   public float getMin() {
      return this.min;
   }

   public float getMax() {
      return this.max;
   }

   public float getRandom() {
      return this.getRandom(rand);
   }

   public float getRandom(RandomSource src) {
      return this.max <= this.min ? this.min : this.min + src.nextFloat() * (this.max - this.min);
   }
}
