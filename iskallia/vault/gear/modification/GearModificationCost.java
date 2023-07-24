package iskallia.vault.gear.modification;

import iskallia.vault.config.gear.VaultGearModificationConfig;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.init.ModConfigs;
import net.minecraft.util.Mth;

public record GearModificationCost(int costPlating, int costBronze) {
   private static final float BASE_PLATING_COST = 4.0F;
   private static final float BASE_BRONZE_COST = 35.0F;
   private static final float BASE_POTENTIAL_SCALING_COST = 120.0F;

   public static GearModificationCost getCost(VaultGearRarity gearRarity, int gearLevel, int gearPotential, GearModification modification) {
      VaultGearModificationConfig.OperationConfig cfg = ModConfigs.VAULT_GEAR_MODIFICATION_CONFIG.getOperationConfig(modification);
      if (cfg == null) {
         return new GearModificationCost(Integer.MAX_VALUE, Integer.MAX_VALUE);
      } else {
         float incPotential = ModConfigs.VAULT_GEAR_CRAFTING_CONFIG.getPotentialIncreasePerLevel() * gearLevel;
         float maxPotential = ModConfigs.VAULT_GEAR_CRAFTING_CONFIG.getMaxCraftingPotential(gearRarity) * (1.0F + incPotential);
         float maxPotentialMultiplier = Math.max(maxPotential / 120.0F, 1.0F);
         float multiplier = cfg.getBaseCostMultiplier() * maxPotentialMultiplier;
         float basePlating = Math.max(4.0F * multiplier, 1.0F);
         float baseBronze = 35.0F * multiplier;
         if (gearPotential >= 0) {
            float potentialPercent = Mth.clamp(1.0F - gearPotential / maxPotential, 0.0F, 1.0F);
            int offset = 3;
            return new GearModificationCost(Math.max(Math.round(basePlating), 1), Math.max(offset + Math.round((baseBronze - offset) * potentialPercent), 1));
         } else {
            int absPotential = Math.abs(gearPotential);
            float negativeMultiplier = cfg.getNegativePotentialCostMultiplier();
            float scale = 1.0F + absPotential * negativeMultiplier / 20.0F;
            return new GearModificationCost(Math.max(Math.round(basePlating * scale), 1), Math.max(Math.round(baseBronze * scale), 1));
         }
      }
   }
}
