package iskallia.vault.config;

import com.google.common.collect.Lists;
import com.google.gson.annotations.Expose;
import iskallia.vault.util.data.WeightedList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public class VaultGearScalingConfig extends Config {
   @Expose
   private final Map<String, List<VaultGearScalingConfig.Level>> pooledRarityOutcomes = new HashMap<>();

   @Override
   public String getName() {
      return "vault_gear_scaling";
   }

   @Override
   protected void reset() {
      this.pooledRarityOutcomes.clear();
      VaultGearScalingConfig.Level defaultLevel = new VaultGearScalingConfig.Level(
         0, new WeightedList<VaultGearScalingConfig.GearRarityOutcome>().add(new VaultGearScalingConfig.GearRarityOutcome(0, "Scrappy"), 1)
      );
      this.pooledRarityOutcomes.put("Scaling", Lists.newArrayList(new VaultGearScalingConfig.Level[]{defaultLevel}));
   }

   @Nullable
   public VaultGearScalingConfig.GearRarityOutcome getGearRollType(String pool, int playerLevel) {
      List<VaultGearScalingConfig.Level> levelConfig = this.pooledRarityOutcomes.get(pool);
      if (levelConfig == null) {
         return null;
      } else {
         VaultGearScalingConfig.Level level = this.getForLevel(levelConfig, playerLevel);
         return level == null ? null : level.outcomes.getRandom(rand);
      }
   }

   @Nullable
   public VaultGearScalingConfig.Level getForLevel(List<VaultGearScalingConfig.Level> levels, int level) {
      for (int i = 0; i < levels.size(); i++) {
         if (level < levels.get(i).level) {
            if (i != 0) {
               return levels.get(i - 1);
            }
            break;
         }

         if (i == levels.size() - 1) {
            return levels.get(i);
         }
      }

      return null;
   }

   public static class GearRarityOutcome {
      @Expose
      private final int tier;
      @Expose
      private final String rarity;

      public GearRarityOutcome(int tier, String rarity) {
         this.tier = tier;
         this.rarity = rarity;
      }

      public int getTier() {
         return this.tier;
      }

      public String getRarity() {
         return this.rarity;
      }
   }

   public static class Level {
      @Expose
      private final int level;
      @Expose
      private final WeightedList<VaultGearScalingConfig.GearRarityOutcome> outcomes;

      public Level(int level, WeightedList<VaultGearScalingConfig.GearRarityOutcome> outcomes) {
         this.level = level;
         this.outcomes = outcomes;
      }
   }
}
