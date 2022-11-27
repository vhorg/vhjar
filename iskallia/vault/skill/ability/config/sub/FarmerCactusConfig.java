package iskallia.vault.skill.ability.config.sub;

import iskallia.vault.skill.ability.config.FarmerConfig;

public class FarmerCactusConfig extends FarmerConfig {
   public FarmerCactusConfig(
      int learningCost, int regretCost, int cooldownTicks, int levelRequirement, float manaCostPerSecond, int tickDelay, int horizontalRange, int verticalRange
   ) {
      super(learningCost, regretCost, cooldownTicks, levelRequirement, manaCostPerSecond, tickDelay, horizontalRange, verticalRange);
   }
}
