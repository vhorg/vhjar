package iskallia.vault.skill.ability.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.spi.AbstractToggleManaConfig;

public class TankConfig extends AbstractToggleManaConfig {
   @Expose
   private final int durationTicksPerHit;
   @Expose
   private final float resistancePercentAddedPerHit;
   @Expose
   private final float resistancePercentCap;

   public TankConfig(
      int learningCost,
      int regretCost,
      int cooldownTicks,
      int levelRequirement,
      float manaCostPerSecond,
      int durationTicksPerHit,
      float resistancePercentAddedPerHit,
      float resistancePercentCap
   ) {
      super(learningCost, regretCost, cooldownTicks, levelRequirement, manaCostPerSecond);
      this.durationTicksPerHit = durationTicksPerHit;
      this.resistancePercentAddedPerHit = resistancePercentAddedPerHit;
      this.resistancePercentCap = resistancePercentCap;
   }

   public int getDurationTicksPerHit() {
      return this.durationTicksPerHit;
   }

   public float getResistancePercentAddedPerHit() {
      return this.resistancePercentAddedPerHit;
   }

   public float getResistancePercentCap() {
      return this.resistancePercentCap;
   }
}
