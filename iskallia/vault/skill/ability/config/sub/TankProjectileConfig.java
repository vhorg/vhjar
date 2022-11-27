package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.TankConfig;

public class TankProjectileConfig extends TankConfig {
   @Expose
   private final float percentageReducedProjectileDamage;
   @Expose
   private final float knockbackResistance;

   public TankProjectileConfig(
      int learningCost,
      int regretCost,
      int cooldownTicks,
      int levelRequirement,
      float manaCostPerSecond,
      float percentageReducedProjectileDamage,
      float knockbackResistance
   ) {
      super(learningCost, regretCost, cooldownTicks, levelRequirement, manaCostPerSecond, 0, 0.0F, 0.0F);
      this.percentageReducedProjectileDamage = percentageReducedProjectileDamage;
      this.knockbackResistance = knockbackResistance;
   }

   public float getPercentageReducedProjectileDamage() {
      return this.percentageReducedProjectileDamage;
   }

   public float getKnockbackResistance() {
      return this.knockbackResistance;
   }
}
