package iskallia.vault.skill.ability.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.spi.AbstractToggleManaConfig;

public class ManaShieldConfig extends AbstractToggleManaConfig {
   @Expose
   private final float percentageDamageAbsorbed;
   @Expose
   private final float manaPerDamageScalar;

   public ManaShieldConfig(
      int learningCost,
      int regretCost,
      int cooldownTicks,
      int levelRequirement,
      float manaCostPerSecond,
      float percentageDamageAbsorbed,
      float manaPerDamageScalar
   ) {
      super(learningCost, regretCost, cooldownTicks, levelRequirement, manaCostPerSecond);
      this.percentageDamageAbsorbed = percentageDamageAbsorbed;
      this.manaPerDamageScalar = manaPerDamageScalar;
   }

   public float getPercentageDamageAbsorbed() {
      return this.percentageDamageAbsorbed;
   }

   public float getManaPerDamageScalar() {
      return this.manaPerDamageScalar;
   }
}
