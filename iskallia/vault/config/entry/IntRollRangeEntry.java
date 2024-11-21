package iskallia.vault.config.entry;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.random.RandomSource;

public class IntRollRangeEntry extends IntRangeEntry {
   @Expose
   private final int step;

   public IntRollRangeEntry(int min, int max, int step) {
      super(min, max);
      this.step = step;
   }

   public int getStep() {
      return this.step;
   }

   @Override
   public int getRandom(RandomSource src) {
      int steps = Math.max(this.getMax() - this.getMin(), 0) / this.getStep() + 1;
      return this.getMin() + src.nextInt(steps) * this.getStep();
   }

   public int getRolledMaximum() {
      int steps = Math.max(this.getMax() - this.getMin(), 0) / this.getStep();
      return this.getMin() + steps * this.getStep();
   }
}
