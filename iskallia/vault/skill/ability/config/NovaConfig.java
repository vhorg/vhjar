package iskallia.vault.skill.ability.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.spi.AbstractInstantManaConfig;

public class NovaConfig extends AbstractInstantManaConfig {
   @Expose
   private final float radius;
   @Expose
   private final float percentAttackDamageDealt;
   @Expose
   private final float knockbackStrengthMultiplier;

   public NovaConfig(
      int learningCost,
      int regretCost,
      int cooldownTicks,
      int levelRequirement,
      float manaCost,
      float radius,
      float percentAttackDamageDealt,
      float knockbackStrengthMultiplier
   ) {
      super(learningCost, regretCost, cooldownTicks, levelRequirement, manaCost);
      this.radius = radius;
      this.percentAttackDamageDealt = percentAttackDamageDealt;
      this.knockbackStrengthMultiplier = knockbackStrengthMultiplier;
   }

   public float getRadius() {
      return this.radius;
   }

   public float getPercentAttackDamageDealt() {
      return this.percentAttackDamageDealt;
   }

   public float getKnockbackStrengthMultiplier() {
      return this.knockbackStrengthMultiplier;
   }
}
