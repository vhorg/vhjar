package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.TankConfig;

public class TankReflectConfig extends TankConfig {
   @Expose
   private final float additionalThornsChance;
   @Expose
   private final float thornsDamageMultiplier;

   public TankReflectConfig(
      int learningCost,
      int regretCost,
      int cooldownTicks,
      int levelRequirement,
      float manaCostPerSecond,
      float additionalThornsChance,
      float thornsDamageMultiplier
   ) {
      super(learningCost, regretCost, cooldownTicks, levelRequirement, manaCostPerSecond, 0, 0.0F, 0.0F);
      this.additionalThornsChance = additionalThornsChance;
      this.thornsDamageMultiplier = thornsDamageMultiplier;
   }

   public float getAdditionalThornsChance() {
      return this.additionalThornsChance;
   }

   public float getThornsDamageMultiplier() {
      return this.thornsDamageMultiplier;
   }
}
