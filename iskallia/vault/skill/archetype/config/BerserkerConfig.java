package iskallia.vault.skill.archetype.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.archetype.AbstractArchetypeConfig;

public class BerserkerConfig extends AbstractArchetypeConfig {
   @Expose
   private final float playerMaxLifeMultiplier;
   @Expose
   private final float playerDamageDealtMultiplier;

   public BerserkerConfig(int learningCost, int levelRequirement, float playerMaxLifeMultiplier, float playerDamageDealtMultiplier) {
      super(learningCost, levelRequirement);
      this.playerMaxLifeMultiplier = playerMaxLifeMultiplier;
      this.playerDamageDealtMultiplier = playerDamageDealtMultiplier;
   }

   public float getPlayerMaxLifeMultiplier() {
      return this.playerMaxLifeMultiplier;
   }

   public float getPlayerDamageDealtMultiplier() {
      return this.playerDamageDealtMultiplier;
   }
}
