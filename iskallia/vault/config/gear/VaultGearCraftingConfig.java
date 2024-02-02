package iskallia.vault.config.gear;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.Config;
import iskallia.vault.config.entry.LevelEntryList;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.init.ModConfigs;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.util.Mth;

public class VaultGearCraftingConfig extends Config {
   @Expose
   private String defaultCraftedPool;
   @Expose
   private float legendaryModifierChance;
   @Expose
   private float potentialIncreasePerLevel;
   @Expose
   private final Map<VaultGearRarity, VaultGearCraftingConfig.Range> potentialRanges = new HashMap<>();
   @Expose
   private Map<String, Double> potentialModifiers = new HashMap<>();
   @Expose
   private int totalMaximumProficiency;
   @Expose
   private int totalCategoryProficiency;
   @Expose
   private VaultGearCraftingConfig.Range proficiencyPerCraft;
   @Expose
   private final LevelEntryList<VaultGearCraftingConfig.Pool> proficiencyPools = new LevelEntryList<>();

   @Override
   public String getName() {
      return "gear%sgear_crafting".formatted(File.separator);
   }

   public float getLegendaryModifierChance() {
      return this.legendaryModifierChance;
   }

   public int getTotalMaximumProficiency() {
      return this.totalMaximumProficiency;
   }

   public int getTotalCategoryProficiency() {
      return this.totalCategoryProficiency;
   }

   public int getRandomProficiencyGain() {
      return this.proficiencyPerCraft.getRandom(rand);
   }

   public float getPotentialIncreasePerLevel() {
      return this.potentialIncreasePerLevel;
   }

   public int getMaxCraftingPotential(VaultGearRarity rarity, String pool) {
      VaultGearCraftingConfig.Range range = this.potentialRanges.get(rarity);
      return range == null ? 0 : (int)Math.round(range.getMax() * this.potentialModifiers.getOrDefault(pool, 1.0));
   }

   public int getNewCraftingPotential(VaultGearRarity rarity, String pool) {
      VaultGearCraftingConfig.Range range = this.potentialRanges.get(rarity);
      return range == null ? 0 : (int)Math.round(range.getRandom(rand) * this.potentialModifiers.getOrDefault(pool, 1.0));
   }

   public VaultGearTypeConfig.RollType getDefaultCraftedPool() {
      return ModConfigs.VAULT_GEAR_TYPE_CONFIG.getRollPool(this.defaultCraftedPool).orElse(ModConfigs.VAULT_GEAR_TYPE_CONFIG.getDefaultRoll());
   }

   public VaultGearTypeConfig.RollType getRollPool(int proficiency) {
      String rollPool = this.proficiencyPools.getForLevel(proficiency).map(VaultGearCraftingConfig.Pool::getPool).orElse(this.defaultCraftedPool);
      return ModConfigs.VAULT_GEAR_TYPE_CONFIG.getRollPool(rollPool).orElse(ModConfigs.VAULT_GEAR_TYPE_CONFIG.getDefaultRoll());
   }

   @Override
   protected void reset() {
      this.defaultCraftedPool = "Scrappy";
      this.legendaryModifierChance = 0.03F;
      this.potentialIncreasePerLevel = 0.015F;
      this.potentialRanges.clear();
      this.potentialRanges.put(VaultGearRarity.SCRAPPY, new VaultGearCraftingConfig.Range(30, 50));
      this.potentialRanges.put(VaultGearRarity.COMMON, new VaultGearCraftingConfig.Range(50, 70));
      this.potentialRanges.put(VaultGearRarity.RARE, new VaultGearCraftingConfig.Range(70, 90));
      this.potentialRanges.put(VaultGearRarity.EPIC, new VaultGearCraftingConfig.Range(90, 115));
      this.potentialRanges.put(VaultGearRarity.OMEGA, new VaultGearCraftingConfig.Range(115, 140));
      this.totalMaximumProficiency = 2000;
      this.totalCategoryProficiency = 1000;
      this.proficiencyPerCraft = new VaultGearCraftingConfig.Range(5, 15);
      this.proficiencyPools.clear();
      this.proficiencyPools.add(new VaultGearCraftingConfig.Pool(0, "Scrappy"));
      this.proficiencyPools.add(new VaultGearCraftingConfig.Pool(300, "Scrappy+"));
   }

   public static class Pool implements LevelEntryList.ILevelEntry {
      @Expose
      private int minProficiency;
      @Expose
      private String pool;

      public Pool(int minProficiency, String pool) {
         this.minProficiency = minProficiency;
         this.pool = pool;
      }

      public String getPool() {
         return this.pool;
      }

      @Override
      public int getLevel() {
         return this.minProficiency;
      }
   }

   public static class Range {
      @Expose
      private int min;
      @Expose
      private int max;

      public Range(int min, int max) {
         this.min = min;
         this.max = max;
      }

      public int getRandom(Random rand) {
         return Mth.randomBetweenInclusive(rand, this.min, this.max);
      }

      public int getMax() {
         return this.max;
      }
   }
}
