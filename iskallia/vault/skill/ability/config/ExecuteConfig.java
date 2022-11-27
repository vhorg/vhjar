package iskallia.vault.skill.ability.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.spi.AbstractAbilityConfig;

public class ExecuteConfig extends AbstractAbilityConfig {
   @Expose
   private final float damageHealthPercentage;
   @Expose
   private final int effectDurationTicks;

   public ExecuteConfig(int learningCost, int regretCost, int cooldownTicks, int levelRequirement, float damageHealthPercentage, int effectDurationTicks) {
      super(learningCost, regretCost, cooldownTicks, levelRequirement);
      this.damageHealthPercentage = damageHealthPercentage;
      this.effectDurationTicks = effectDurationTicks;
   }

   public float getDamageHealthPercentage() {
      return this.damageHealthPercentage;
   }

   public int getEffectDurationTicks() {
      return this.effectDurationTicks;
   }
}
