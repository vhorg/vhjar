package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.init.ModBlocks;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.world.item.Item;

public class SpiritConfig extends Config {
   @Expose
   public float perRecoveryMultiplierIncrease;
   @Expose
   public float perCompletionMultiplierDecrease;
   @Expose
   public float heroDiscountMin;
   @Expose
   public float heroDiscountMax;
   @Expose
   public float rescuedBonusMin;
   @Expose
   public float rescuedBonusMax;
   @Expose
   public Set<SpiritConfig.LevelCost> levelCosts;

   @Override
   public String getName() {
      return "spirit";
   }

   @Override
   protected void reset() {
      this.perRecoveryMultiplierIncrease = 0.3F;
      this.perCompletionMultiplierDecrease = 0.05F;
      this.heroDiscountMin = 0.2F;
      this.heroDiscountMax = 0.8F;
      this.rescuedBonusMin = 0.2F;
      this.rescuedBonusMax = 0.8F;
      this.levelCosts = new HashSet<>();
      Map<VaultGearRarity, Integer> gearRarityCost = new LinkedHashMap<>();
      gearRarityCost.put(VaultGearRarity.SCRAPPY, 1);
      gearRarityCost.put(VaultGearRarity.COMMON, 2);
      gearRarityCost.put(VaultGearRarity.RARE, 3);
      gearRarityCost.put(VaultGearRarity.EPIC, 4);
      gearRarityCost.put(VaultGearRarity.OMEGA, 5);
      gearRarityCost.put(VaultGearRarity.UNIQUE, 6);
      this.levelCosts.add(new SpiritConfig.LevelCost(0, ModBlocks.VAULT_BRONZE, 20, gearRarityCost, 3));
      this.levelCosts.add(new SpiritConfig.LevelCost(5, ModBlocks.VAULT_SILVER, 3, gearRarityCost, 3));
   }

   public float getCompletionMultiplierDecrease() {
      return (this.perCompletionMultiplierDecrease < 0.5F ? 1 : 0) - this.perCompletionMultiplierDecrease;
   }

   public float getHeroDiscount(Random random) {
      return this.heroDiscountMax > 0.0F ? random.nextFloat(this.heroDiscountMin, this.heroDiscountMax) : 0.0F;
   }

   public float getRescuedBonus(Random random) {
      return random.nextFloat(this.rescuedBonusMin, this.rescuedBonusMax);
   }

   public static class LevelCost {
      @Expose
      public int minLevel;
      @Expose
      public Item item;
      @Expose
      public int count;
      @Expose
      public Map<VaultGearRarity, Integer> gearRarityCost;
      @Expose
      public int trinketCost;

      public LevelCost(int minLevel, Item item, int count, Map<VaultGearRarity, Integer> gearRarityCost, int trinketCost) {
         this.minLevel = minLevel;
         this.item = item;
         this.count = count;
         this.gearRarityCost = gearRarityCost;
         this.trinketCost = trinketCost;
      }
   }
}
