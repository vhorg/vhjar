package iskallia.vault.aura.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.EternalAuraConfig;

public class ParryAuraConfig extends EternalAuraConfig.AuraConfig {
   @Expose
   private final float additionalParryChance;

   public ParryAuraConfig(float additionalParryChance) {
      super("Parry", "Parry", "Players in aura have +" + ROUNDING_FORMAT.format(additionalParryChance * 100.0F) + "% Parry", "parry", 5.0F);
      this.additionalParryChance = additionalParryChance;
   }

   public float getAdditionalParryChance() {
      return this.additionalParryChance;
   }
}
