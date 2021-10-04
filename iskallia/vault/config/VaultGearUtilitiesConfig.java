package iskallia.vault.config;

import com.google.gson.annotations.Expose;

public class VaultGearUtilitiesConfig extends Config {
   @Expose
   private float voidOrbRepairCostChance;
   @Expose
   private float fabricationJewelKeepModifierChance;

   @Override
   public String getName() {
      return "vault_gear_utilities";
   }

   public float getVoidOrbRepairCostChance() {
      return this.voidOrbRepairCostChance;
   }

   public float getFabricationJewelKeepModifierChance() {
      return this.fabricationJewelKeepModifierChance;
   }

   @Override
   protected void reset() {
      this.voidOrbRepairCostChance = 0.2F;
      this.fabricationJewelKeepModifierChance = 0.8F;
   }
}
