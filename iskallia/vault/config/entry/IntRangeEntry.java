package iskallia.vault.config.entry;

import com.google.gson.annotations.Expose;
import iskallia.vault.util.MathUtilities;

public class IntRangeEntry {
   public static final IntRangeEntry EMPTY = new IntRangeEntry(0, 0);
   @Expose
   public final int min;
   @Expose
   public final int max;

   public IntRangeEntry(int min, int max) {
      this.min = min;
      this.max = max;
   }

   public int getRandom() {
      return MathUtilities.getRandomInt(this.min, this.max);
   }
}
