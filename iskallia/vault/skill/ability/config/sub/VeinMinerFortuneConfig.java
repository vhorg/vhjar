package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.VeinMinerConfig;

public class VeinMinerFortuneConfig extends VeinMinerConfig {
   @Expose
   private final int additionalFortuneLevel;

   public VeinMinerFortuneConfig(int learningCost, int regretCost, int cooldownTicks, int levelRequirement, int blockLimit, int additionalFortuneLevel) {
      super(learningCost, regretCost, cooldownTicks, levelRequirement, blockLimit);
      this.additionalFortuneLevel = additionalFortuneLevel;
   }

   public int getAdditionalFortuneLevel() {
      return this.additionalFortuneLevel;
   }
}
