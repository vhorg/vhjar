package iskallia.vault.config.gear;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.Config;
import iskallia.vault.config.entry.LevelEntryList;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public class VaultGearTypePoolConfig extends Config {
   @Expose
   private final Map<String, LevelEntryList<VaultGearTypePoolConfig.RollTypeLevel>> pooledOutcomes = new HashMap<>();

   @Override
   public String getName() {
      return "gear%sgear_roll_type_pools".formatted(File.separator);
   }

   @Nullable
   public String getGearRollType(String pool, int playerLevel, RandomSource random) {
      List<VaultGearTypePoolConfig.RollTypeLevel> rollTypeConfigs = this.pooledOutcomes.get(pool);
      if (rollTypeConfigs == null) {
         return null;
      } else {
         VaultGearTypePoolConfig.RollTypeLevel level = this.getForLevel(rollTypeConfigs, playerLevel);
         return level == null ? null : level.rollTypeOutcomes.getRandom(random).orElse(null);
      }
   }

   @Override
   protected void reset() {
      this.pooledOutcomes.clear();
      VaultGearTypePoolConfig.RollTypeLevel rollTypeLevel = new VaultGearTypePoolConfig.RollTypeLevel(0, new WeightedList<String>().add("Scrappy", 1));
      LevelEntryList<VaultGearTypePoolConfig.RollTypeLevel> list = new LevelEntryList<>();
      list.add(rollTypeLevel);
      this.pooledOutcomes.put("Scaling", list);
   }

   @Nullable
   public VaultGearTypePoolConfig.RollTypeLevel getForLevel(List<VaultGearTypePoolConfig.RollTypeLevel> levels, int level) {
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

   public static class RollTypeLevel implements LevelEntryList.ILevelEntry {
      @Expose
      private final int level;
      @Expose
      private final WeightedList<String> rollTypeOutcomes;

      public RollTypeLevel(int level, WeightedList<String> rollTypeOutcomes) {
         this.level = level;
         this.rollTypeOutcomes = rollTypeOutcomes;
      }

      @Override
      public int getLevel() {
         return this.level;
      }
   }
}
