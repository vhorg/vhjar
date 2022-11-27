package iskallia.vault.skill.archetype.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.archetype.AbstractArchetypeConfig;

public class WardConfig extends AbstractArchetypeConfig {
   @Expose
   private final float additionalBlockChanceWithShieldEquipped;
   @Expose
   private final float playerDamageDealtMultiplier;

   public WardConfig(int learningCost, int levelRequirement, float additionalBlockChanceWithShieldEquipped, float playerDamageDealtMultiplier) {
      super(learningCost, levelRequirement);
      this.additionalBlockChanceWithShieldEquipped = additionalBlockChanceWithShieldEquipped;
      this.playerDamageDealtMultiplier = playerDamageDealtMultiplier;
   }

   public float getAdditionalBlockChanceWithShieldEquipped() {
      return this.additionalBlockChanceWithShieldEquipped;
   }

   public float getPlayerDamageDealtMultiplier() {
      return this.playerDamageDealtMultiplier;
   }
}
