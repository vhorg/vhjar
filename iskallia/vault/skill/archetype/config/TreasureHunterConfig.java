package iskallia.vault.skill.archetype.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.archetype.AbstractArchetypeConfig;

public class TreasureHunterConfig extends AbstractArchetypeConfig {
   @Expose
   private final float additionalItemQuantity;
   @Expose
   private final float additionalItemRarity;
   @Expose
   private final float playerDamageTakenMultiplier;

   public TreasureHunterConfig(
      int learningCost, int levelRequirement, float additionalItemQuantity, float additionalItemRarity, float playerDamageTakenMultiplier
   ) {
      super(learningCost, levelRequirement);
      this.additionalItemQuantity = additionalItemQuantity;
      this.additionalItemRarity = additionalItemRarity;
      this.playerDamageTakenMultiplier = playerDamageTakenMultiplier;
   }

   public float getAdditionalItemQuantity() {
      return this.additionalItemQuantity;
   }

   public float getAdditionalItemRarity() {
      return this.additionalItemRarity;
   }

   public float getPlayerDamageTakenMultiplier() {
      return this.playerDamageTakenMultiplier;
   }
}
