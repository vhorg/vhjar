package iskallia.vault.aura.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.EternalAuraConfig;

public class ResistanceAuraConfig extends EternalAuraConfig.AuraConfig {
   @Expose
   private final float additionalResistance;

   public ResistanceAuraConfig(float additionalResistance) {
      super("Resistance", "Resistance", "Players in aura have +" + ROUNDING_FORMAT.format(additionalResistance * 100.0F) + "% Resistance", "resistance", 5.0F);
      this.additionalResistance = additionalResistance;
   }

   public float getAdditionalResistance() {
      return this.additionalResistance;
   }
}
