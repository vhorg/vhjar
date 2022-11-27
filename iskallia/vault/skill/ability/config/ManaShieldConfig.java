package iskallia.vault.skill.ability.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.spi.AbstractToggleManaConfig;

public class ManaShieldConfig extends AbstractToggleManaConfig {
   @Expose
   private final float percentageDamageAbsorbed;

   public ManaShieldConfig(int learningCost, int regretCost, int cooldownTicks, int levelRequirement, float manaCostPerSecond, float percentageDamageAbsorbed) {
      super(learningCost, regretCost, cooldownTicks, levelRequirement, manaCostPerSecond);
      this.percentageDamageAbsorbed = percentageDamageAbsorbed;
   }

   public float getPercentageDamageAbsorbed() {
      return this.percentageDamageAbsorbed;
   }
}
