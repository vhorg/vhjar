package iskallia.vault.config;

import com.google.gson.annotations.Expose;

public class AbyssConfig extends Config {
   @Expose
   private float fullAbyssDistance;
   @Expose
   private float mobDamageIncrease;
   @Expose
   private float mobHealthIncrease;
   @Expose
   private float mobKnockBackResistance;
   @Expose
   private float breakSpeedReductionMultiplier;
   @Expose
   private float itemQuantityBonus;
   @Expose
   private float itemRarityBonus;
   @Expose
   private float copiouslyBonus;
   @Expose
   private float abyssalModifierChance;
   @Expose
   private float abyssalAdditionalModifierChance;
   @Expose
   private float abyssalFocusChance;

   public float getFullAbyssDistance() {
      return this.fullAbyssDistance;
   }

   public float getMobDamageIncrease() {
      return this.mobDamageIncrease;
   }

   public float getMobHealthIncrease() {
      return this.mobHealthIncrease;
   }

   public float getMobKnockBackResistance() {
      return this.mobKnockBackResistance;
   }

   public float getBreakSpeedReductionMultiplier() {
      return this.breakSpeedReductionMultiplier;
   }

   public float getItemQuantityBonus() {
      return this.itemQuantityBonus;
   }

   public float getItemRarityBonus() {
      return this.itemRarityBonus;
   }

   public float getCopiouslyBonus() {
      return this.copiouslyBonus;
   }

   public float getAbyssalModifierChance() {
      return this.abyssalModifierChance;
   }

   public float getAbyssalAdditionalModifierChance() {
      return this.abyssalAdditionalModifierChance;
   }

   public float getAbyssalFocusChance() {
      return this.abyssalFocusChance;
   }

   @Override
   public String getName() {
      return "abyss";
   }

   @Override
   protected void reset() {
      this.fullAbyssDistance = 300.0F;
      this.mobDamageIncrease = 1.2F;
      this.mobHealthIncrease = 3.5F;
      this.mobKnockBackResistance = 1.5F;
      this.breakSpeedReductionMultiplier = 2.5F;
      this.itemQuantityBonus = 0.75F;
      this.itemRarityBonus = 0.75F;
      this.copiouslyBonus = 0.3F;
      this.abyssalModifierChance = 0.04F;
      this.abyssalAdditionalModifierChance = 0.04F;
      this.abyssalFocusChance = 0.01F;
   }
}
