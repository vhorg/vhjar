package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.FarmerConfig;

public class FarmerAnimalConfig extends FarmerConfig {
   @Expose
   private final float adultChance;

   public FarmerAnimalConfig(
      int learningCost,
      int regretCost,
      int cooldownTicks,
      int levelRequirement,
      float manaCostPerSecond,
      int tickDelay,
      int horizontalRange,
      int verticalRange,
      float adultChance
   ) {
      super(learningCost, regretCost, cooldownTicks, levelRequirement, manaCostPerSecond, tickDelay, horizontalRange, verticalRange);
      this.adultChance = adultChance;
   }

   public float getAdultChance() {
      return this.adultChance;
   }
}
