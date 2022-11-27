package iskallia.vault.skill.archetype.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.archetype.AbstractArchetypeConfig;

public class BarbarianConfig extends AbstractArchetypeConfig {
   @Expose
   private final int ragePerHit;
   @Expose
   private final float playerDamageDealtMultiplierPerRagePoint;
   @Expose
   private final float playerHealingEfficiencyMultiplierPerRagePoint;

   public BarbarianConfig(
      int learningCost,
      int levelRequirement,
      int ragePerHit,
      float playerDamageDealtMultiplierPerRagePoint,
      float playerHealingEfficiencyMultiplierPerRagePoint
   ) {
      super(learningCost, levelRequirement);
      this.ragePerHit = ragePerHit;
      this.playerDamageDealtMultiplierPerRagePoint = playerDamageDealtMultiplierPerRagePoint;
      this.playerHealingEfficiencyMultiplierPerRagePoint = playerHealingEfficiencyMultiplierPerRagePoint;
   }

   public int getRagePerHit() {
      return this.ragePerHit;
   }

   public float getPlayerDamageDealtMultiplierPerRagePoint() {
      return this.playerDamageDealtMultiplierPerRagePoint;
   }

   public float getPlayerHealingEfficiencyMultiplierPerRagePoint() {
      return this.playerHealingEfficiencyMultiplierPerRagePoint;
   }
}
