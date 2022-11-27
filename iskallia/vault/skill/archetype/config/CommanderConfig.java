package iskallia.vault.skill.archetype.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.archetype.AbstractArchetypeConfig;

public class CommanderConfig extends AbstractArchetypeConfig {
   @Expose
   private final float eternalDamageDealtMultiplier;
   @Expose
   private final float playerDamageDealtMultiplier;

   public CommanderConfig(int learningCost, int levelRequirement, float eternalDamageDealtMultiplier, float playerDamageDealtMultiplier) {
      super(learningCost, levelRequirement);
      this.eternalDamageDealtMultiplier = eternalDamageDealtMultiplier;
      this.playerDamageDealtMultiplier = playerDamageDealtMultiplier;
   }

   public float getEternalDamageDealtMultiplier() {
      return this.eternalDamageDealtMultiplier;
   }

   public float getPlayerDamageDealtMultiplier() {
      return this.playerDamageDealtMultiplier;
   }
}
