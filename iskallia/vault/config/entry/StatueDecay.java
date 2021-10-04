package iskallia.vault.config.entry;

import com.google.gson.annotations.Expose;
import iskallia.vault.util.MathUtilities;

public class StatueDecay {
   @Expose
   private int MIN;
   @Expose
   private int MAX;
   public static final StatueDecay NONE = new StatueDecay(-1, -1);

   public StatueDecay(int min, int max) {
      this.MIN = min;
      this.MAX = max;
   }

   public int getDecay() {
      return MathUtilities.getRandomInt(this.MIN, this.MAX);
   }
}
