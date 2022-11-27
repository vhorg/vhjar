package iskallia.vault.skill.ability.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.spi.AbstractAbilityConfig;

public class VeinMinerConfig extends AbstractAbilityConfig {
   @Expose
   private final int blockLimit;

   public VeinMinerConfig(int learningCost, int regretCost, int cooldownTicks, int levelRequirement, int blockLimit) {
      super(learningCost, regretCost, cooldownTicks, levelRequirement);
      this.blockLimit = blockLimit;
   }

   public int getBlockLimit() {
      return this.blockLimit;
   }
}
