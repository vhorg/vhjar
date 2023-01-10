package iskallia.vault.world.vault.modifier.reputation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import iskallia.vault.world.vault.modifier.spi.ModifierContext;

public class ScalarReputationProperty {
   @Expose
   @SerializedName("addendPerLevel")
   private final double addendPerLevel;

   public ScalarReputationProperty(double addendPerLevel) {
      this.addendPerLevel = addendPerLevel;
   }

   public int apply(int value, ModifierContext context) {
      return context.getReputation().map(x -> (int)(x.intValue() * this.addendPerLevel + value)).orElse(value);
   }

   public float apply(float value, ModifierContext context) {
      return context.getReputation().map(x -> (float)(x.intValue() * this.addendPerLevel + value)).orElse(value);
   }

   public double apply(double value, ModifierContext context) {
      return context.getReputation().map(x -> x.intValue() * this.addendPerLevel + value).orElse(value);
   }
}
