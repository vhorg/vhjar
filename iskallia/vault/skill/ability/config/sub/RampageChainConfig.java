package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.RampageConfig;

public class RampageChainConfig extends RampageConfig {
   @Expose
   private final int additionalChainCount;

   public RampageChainConfig(int learningCost, int regretCost, int cooldownTicks, int levelRequirement, float manaCostPerSecond, int additionalChainCount) {
      super(learningCost, regretCost, cooldownTicks, levelRequirement, manaCostPerSecond, 0.0F);
      this.additionalChainCount = additionalChainCount;
   }

   public int getAdditionalChainCount() {
      return this.additionalChainCount;
   }
}
