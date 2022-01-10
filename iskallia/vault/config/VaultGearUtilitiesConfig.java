package iskallia.vault.config;

import com.google.gson.annotations.Expose;

public class VaultGearUtilitiesConfig extends Config {
   @Expose
   private float voidOrbRepairCostChance;
   @Expose
   private float voidOrbPredefinedRepairCostChance;
   @Expose
   private float fabricationJewelKeepModifierChance;

   @Override
   public String getName() {
      return "vault_gear_utilities";
   }

   public float getVoidOrbRepairCostChance() {
      return this.voidOrbRepairCostChance;
   }

   public float getVoidOrbPredefinedRepairCostChance() {
      return this.voidOrbPredefinedRepairCostChance;
   }

   public float getFabricationJewelKeepModifierChance() {
      return this.fabricationJewelKeepModifierChance;
   }

   @Override
   protected void reset() {
      this.voidOrbRepairCostChance = 0.2F;
      this.voidOrbPredefinedRepairCostChance = 0.4F;
      this.fabricationJewelKeepModifierChance = 0.8F;
   }
}
