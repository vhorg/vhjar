package iskallia.vault.skill.ability.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.spi.AbstractHoldManaConfig;

public class FarmerConfig extends AbstractHoldManaConfig {
   @Expose
   private final int tickDelay;
   @Expose
   private final int horizontalRange;
   @Expose
   private final int verticalRange;

   public FarmerConfig(
      int learningCost, int regretCost, int cooldownTicks, int levelRequirement, float manaCostPerSecond, int tickDelay, int horizontalRange, int verticalRange
   ) {
      super(learningCost, regretCost, cooldownTicks, levelRequirement, manaCostPerSecond);
      this.tickDelay = tickDelay;
      this.horizontalRange = horizontalRange;
      this.verticalRange = verticalRange;
   }

   public int getTickDelay() {
      return this.tickDelay;
   }

   public int getHorizontalRange() {
      return this.horizontalRange;
   }

   public int getVerticalRange() {
      return this.verticalRange;
   }
}
