package iskallia.vault.config.entry;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.random.RandomSource;

public class FloatRollRangeEntry extends FloatRangeEntry {
   @Expose
   private final float step;

   public FloatRollRangeEntry(float min, float max, float step) {
      super(min, max);
      this.step = step;
   }

   public float getStep() {
      return this.step;
   }

   @Override
   public float getRandom(RandomSource src) {
      int steps = Math.round(Math.max(this.getMax() - this.getMin(), 0.0F) / this.step) + 1;
      return this.getMin() + src.nextInt(steps) * this.step;
   }

   public float getRolledMaximum() {
      int steps = Math.round(Math.max(this.getMax() - this.getMin(), 0.0F) / this.getStep());
      return this.getMin() + steps * this.getStep();
   }
}
