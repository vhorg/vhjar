package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.LevelEntryList;
import iskallia.vault.util.data.WeightedList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.world.entity.EntityType;

public class RaidConfig extends Config {
   private static final RaidConfig.Level DEFAULT = new RaidConfig.Level(0, new RaidConfig.MobPool());
   @Expose
   private final Map<String, LevelEntryList<RaidConfig.Level>> mobPools = new HashMap<>();
   @Expose
   private final WeightedList<RaidConfig.WaveSetup> raidWaves = new WeightedList<>();
   @Expose
   private final List<RaidConfig.AmountLevel> amountLevels = new ArrayList<>();

   @Nullable
   public RaidConfig.MobPool getPool(String pool, int level) {
      LevelEntryList<RaidConfig.Level> mobLevelPool = this.mobPools.get(pool);
      return mobLevelPool == null ? null : mobLevelPool.getForLevel(level).orElse(DEFAULT).mobPool;
   }

   public float getMobCountMultiplier(int level) {
      return this.getAmountLevel(this.amountLevels, level).map(RaidConfig.AmountLevel::getMobAmountMultiplier).orElse(1.0F);
   }

   @Nullable
   public RaidConfig.WaveSetup getRandomWaveSetup() {
      return this.raidWaves.getRandom(rand);
   }

   @Override
   public String getName() {
      return "raid";
   }

   @Override
   protected void reset() {
      this.mobPools.clear();
      this.raidWaves.clear();
      this.mobPools
         .put(
            "ranged",
            LevelEntryList.of(
               new RaidConfig.Level(0, new RaidConfig.MobPool().add(EntityType.SKELETON, 1)),
               new RaidConfig.Level(75, new RaidConfig.MobPool().add(EntityType.SKELETON, 1).add(EntityType.STRAY, 1))
            )
         );
      this.mobPools
         .put(
            "melee",
            LevelEntryList.of(
               new RaidConfig.Level(0, new RaidConfig.MobPool().add(EntityType.ZOMBIE, 1)),
               new RaidConfig.Level(50, new RaidConfig.MobPool().add(EntityType.ZOMBIE, 2).add(EntityType.VINDICATOR, 1))
            )
         );
      RaidConfig.WaveSetup waveSetup = new RaidConfig.WaveSetup()
         .addWave(new RaidConfig.CompoundWave(new RaidConfig.ConfiguredWave(2, 3, "ranged"), new RaidConfig.ConfiguredWave(2, 3, "melee")))
         .addWave(new RaidConfig.CompoundWave(new RaidConfig.ConfiguredWave(4, 5, "ranged"), new RaidConfig.ConfiguredWave(4, 6, "melee")))
         .addWave(new RaidConfig.CompoundWave(new RaidConfig.ConfiguredWave(6, 7, "ranged"), new RaidConfig.ConfiguredWave(5, 8, "melee")));
      this.raidWaves.add(waveSetup, 1);
      this.amountLevels.clear();
      this.amountLevels.add(new RaidConfig.AmountLevel(0, 1.0F));
      this.amountLevels.add(new RaidConfig.AmountLevel(50, 1.5F));
      this.amountLevels.add(new RaidConfig.AmountLevel(100, 2.0F));
      this.amountLevels.add(new RaidConfig.AmountLevel(150, 2.5F));
      this.amountLevels.add(new RaidConfig.AmountLevel(200, 3.0F));
      this.amountLevels.add(new RaidConfig.AmountLevel(250, 4.0F));
   }

   private Optional<RaidConfig.AmountLevel> getAmountLevel(List<RaidConfig.AmountLevel> levels, int level) {
      for (int i = 0; i < levels.size(); i++) {
         if (level < levels.get(i).level) {
            if (i != 0) {
               return Optional.of(levels.get(i - 1));
            }
            break;
         }

         if (i == levels.size() - 1) {
            return Optional.of(levels.get(i));
         }
      }

      return Optional.empty();
   }

   public static class AmountLevel {
      @Expose
      private final int level;
      @Expose
      private final float mobAmountMultiplier;

      public AmountLevel(int level, float mobAmountMultiplier) {
         this.level = level;
         this.mobAmountMultiplier = mobAmountMultiplier;
      }

      public float getMobAmountMultiplier() {
         return this.mobAmountMultiplier;
      }
   }

   public static class CompoundWave {
      @Expose
      private final List<RaidConfig.ConfiguredWave> waveMobs = new ArrayList<>();

      public CompoundWave(RaidConfig.ConfiguredWave... waveMobs) {
         this.waveMobs.addAll(Arrays.asList(waveMobs));
      }

      public List<RaidConfig.ConfiguredWave> getWaveMobs() {
         return this.waveMobs;
      }
   }

   public static class ConfiguredWave {
      @Expose
      private int min;
      @Expose
      private int max;
      @Expose
      private String mobPool;

      public ConfiguredWave(int min, int max, String mobPool) {
         this.min = min;
         this.max = max;
         this.mobPool = mobPool;
      }

      public int getMin() {
         return this.min;
      }

      public int getMax() {
         return this.max;
      }

      public String getMobPool() {
         return this.mobPool;
      }
   }

   public static class Level implements LevelEntryList.ILevelEntry {
      @Expose
      private final int level;
      @Expose
      private final RaidConfig.MobPool mobPool;

      public Level(int level, RaidConfig.MobPool mobPool) {
         this.level = level;
         this.mobPool = mobPool;
      }

      @Override
      public int getLevel() {
         return this.level;
      }
   }

   public static class MobPool {
      @Expose
      private final WeightedList<String> mobs = new WeightedList<>();

      public RaidConfig.MobPool add(EntityType<?> type, int weight) {
         this.mobs.add(type.getRegistryName().toString(), weight);
         return this;
      }

      public WeightedList<String> getMobs() {
         return this.mobs;
      }

      public String getRandomMob() {
         return this.mobs.getRandom(Config.rand);
      }
   }

   public static class WaveSetup {
      @Expose
      private final List<RaidConfig.CompoundWave> waves = new ArrayList<>();

      public RaidConfig.WaveSetup addWave(RaidConfig.CompoundWave wave) {
         this.waves.add(wave);
         return this;
      }

      public List<RaidConfig.CompoundWave> getWaves() {
         return this.waves;
      }
   }
}
