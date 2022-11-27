package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.VeinMinerConfig;

public class VeinMinerDurabilityConfig extends VeinMinerConfig {
   @Expose
   private final int additionalUnbreakingLevel;

   public VeinMinerDurabilityConfig(int learningCost, int regretCost, int cooldownTicks, int levelRequirement, int blockLimit, int additionalUnbreakingLevel) {
      super(learningCost, regretCost, cooldownTicks, levelRequirement, blockLimit);
      this.additionalUnbreakingLevel = additionalUnbreakingLevel;
   }

   public int getAdditionalUnbreakingLevel() {
      return this.additionalUnbreakingLevel;
   }
}
