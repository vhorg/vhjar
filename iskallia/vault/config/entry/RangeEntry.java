package iskallia.vault.config.entry;

import com.google.gson.annotations.Expose;
import iskallia.vault.util.MathUtilities;

public class RangeEntry {
   public static final RangeEntry EMPTY = new RangeEntry(0, 0);
   @Expose
   public final int min;
   @Expose
   public final int max;

   public RangeEntry(int min, int max) {
      this.min = min;
      this.max = max;
   }

   public int getRandom() {
      return MathUtilities.getRandomInt(this.min, this.max);
   }
}
