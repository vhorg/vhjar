package iskallia.vault.skill.ability.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.spi.AbstractToggleManaConfig;

public class RampageConfig extends AbstractToggleManaConfig {
   @Expose
   private final float damageIncrease;

   public RampageConfig(int learningCost, int regretCost, int cooldownTicks, int levelRequirement, float manaCostPerSecond, float damageIncrease) {
      super(learningCost, regretCost, cooldownTicks, levelRequirement, manaCostPerSecond);
      this.damageIncrease = damageIncrease;
   }

   public float getDamageIncrease() {
      return this.damageIncrease;
   }
}
