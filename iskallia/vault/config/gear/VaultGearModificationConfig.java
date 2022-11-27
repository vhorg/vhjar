package iskallia.vault.config.gear;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.Config;
import iskallia.vault.gear.modification.GearModification;
import iskallia.vault.init.ModGearModifications;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class VaultGearModificationConfig extends Config {
   @Expose
   private final Map<ResourceLocation, VaultGearModificationConfig.OperationConfig> operationConfiguration = new LinkedHashMap<>();

   @Override
   public String getName() {
      return "gear%sgear_modification".formatted(File.separator);
   }

   @Nullable
   public VaultGearModificationConfig.OperationConfig getOperationConfig(GearModification modification) {
      return this.operationConfiguration.get(modification.getRegistryName());
   }

   public int getPotentialUsed(GearModification modification) {
      VaultGearModificationConfig.OperationConfig range = this.getOperationConfig(modification);
      return range == null ? 0 : range.getPotentialUse(rand);
   }

   @Override
   protected void reset() {
      this.operationConfiguration.clear();
      this.set(ModGearModifications.REFORGE_ALL_MODIFIERS, 2, 4, 1.0F, 1.0F);
      this.set(ModGearModifications.ADD_MODIFIER, 4, 7, 1.3F, 1.2F);
      this.set(ModGearModifications.REMOVE_MODIFIER, 4, 7, 1.3F, 1.2F);
      this.set(ModGearModifications.RESET_POTENTIAL, 0, 0, 2.0F, 4.0F);
      this.set(ModGearModifications.REFORGE_REPAIR_SLOTS, 10, 20, 1.3F, 1.6F);
      this.set(ModGearModifications.REFORGE_ALL_IMPLICITS, 3, 7, 1.0F, 1.0F);
      this.set(ModGearModifications.REFORGE_ALL_ADD_TAG, 5, 8, 1.2F, 1.2F);
      this.set(ModGearModifications.REFORGE_RANDOM_TIER, 3, 9, 1.1F, 1.1F);
   }

   private void set(GearModification modification, int pMin, int pMax, float base, float negative) {
      this.operationConfiguration.put(modification.getRegistryName(), new VaultGearModificationConfig.OperationConfig(pMin, pMax, base, negative));
   }

   public static class OperationConfig {
      @Expose
      private int potentialUseMin;
      @Expose
      private int potentialUseMax;
      @Expose
      private float baseCostMultiplier;
      @Expose
      private float negativePotentialCostMultiplier;

      public OperationConfig(int potentialUseMin, int potentialUseMax, float baseCostMultiplier, float negativePotentialCostMultiplier) {
         this.potentialUseMin = potentialUseMin;
         this.potentialUseMax = potentialUseMax;
         this.baseCostMultiplier = baseCostMultiplier;
         this.negativePotentialCostMultiplier = negativePotentialCostMultiplier;
      }

      public int getPotentialUse(Random rand) {
         return Mth.randomBetweenInclusive(rand, this.potentialUseMin, this.potentialUseMax);
      }

      public float getBaseCostMultiplier() {
         return this.baseCostMultiplier;
      }

      public float getNegativePotentialCostMultiplier() {
         return this.negativePotentialCostMultiplier;
      }
   }
}
