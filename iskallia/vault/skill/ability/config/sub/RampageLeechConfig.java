package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.RampageConfig;

public class RampageLeechConfig extends RampageConfig {
   @Expose
   private final float leechPercent;

   public RampageLeechConfig(int learningCost, int regretCost, int cooldownTicks, int levelRequirement, float manaCostPerSecond, float leechPercent) {
      super(learningCost, regretCost, cooldownTicks, levelRequirement, manaCostPerSecond, 0.0F);
      this.leechPercent = leechPercent;
   }

   public float getLeechPercent() {
      return this.leechPercent;
   }
}
