package iskallia.vault.world.vault.logic.objective.raid;

import iskallia.vault.config.RaidConfig;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.MathUtilities;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class RaidPreset {
   private final List<RaidPreset.CompoundWaveSpawn> waves = new ArrayList<>();

   private RaidPreset() {
   }

   @Nullable
   public static RaidPreset randomFromConfig() {
      RaidConfig.WaveSetup configSetup = ModConfigs.RAID_CONFIG.getRandomWaveSetup();
      if (configSetup == null) {
         return null;
      } else {
         RaidPreset preset = new RaidPreset();

         for (RaidConfig.CompoundWave wave : configSetup.getWaves()) {
            RaidPreset.CompoundWaveSpawn compoundWave = new RaidPreset.CompoundWaveSpawn();

            for (RaidConfig.ConfiguredWave waveSpawnSet : wave.getWaveMobs()) {
               compoundWave.waveSpawns.add(RaidPreset.WaveSpawn.fromConfig(waveSpawnSet));
            }

            preset.waves.add(compoundWave);
         }

         return preset;
      }
   }

   public int getWaves() {
      return this.waves.size();
   }

   @Nullable
   public RaidPreset.CompoundWaveSpawn getWave(int step) {
      return step >= 0 && step < this.waves.size() ? this.waves.get(step) : null;
   }

   public CompoundTag serialize() {
      CompoundTag tag = new CompoundTag();
      ListTag waveTag = new ListTag();
      this.waves.forEach(wave -> waveTag.add(wave.serialize()));
      tag.put("waves", waveTag);
      return tag;
   }

   public static RaidPreset deserialize(CompoundTag tag) {
      RaidPreset preset = new RaidPreset();
      ListTag waveTag = tag.getList("waves", 10);

      for (int i = 0; i < waveTag.size(); i++) {
         preset.waves.add(RaidPreset.CompoundWaveSpawn.deserialize(waveTag.getCompound(i)));
      }

      return preset;
   }

   public static class CompoundWaveSpawn {
      private final List<RaidPreset.WaveSpawn> waveSpawns = new ArrayList<>();

      public List<RaidPreset.WaveSpawn> getWaveSpawns() {
         return this.waveSpawns;
      }

      public CompoundTag serialize() {
         CompoundTag tag = new CompoundTag();
         ListTag waveTag = new ListTag();
         this.waveSpawns.forEach(wave -> waveTag.add(wave.serialize()));
         tag.put("waves", waveTag);
         return tag;
      }

      public static RaidPreset.CompoundWaveSpawn deserialize(CompoundTag tag) {
         RaidPreset.CompoundWaveSpawn compound = new RaidPreset.CompoundWaveSpawn();
         ListTag waveTag = tag.getList("waves", 10);

         for (int i = 0; i < waveTag.size(); i++) {
            compound.waveSpawns.add(RaidPreset.WaveSpawn.deserialize(waveTag.getCompound(i)));
         }

         return compound;
      }
   }

   public static class WaveSpawn {
      private final int mobCount;
      private final String mobPool;

      private WaveSpawn(int mobCount, String mobPool) {
         this.mobCount = mobCount;
         this.mobPool = mobPool;
      }

      public static RaidPreset.WaveSpawn fromConfig(RaidConfig.ConfiguredWave configuredWave) {
         return new RaidPreset.WaveSpawn(MathUtilities.getRandomInt(configuredWave.getMin(), configuredWave.getMax() + 1), configuredWave.getMobPool());
      }

      public int getMobCount() {
         return this.mobCount;
      }

      public String getMobPool() {
         return this.mobPool;
      }

      public CompoundTag serialize() {
         CompoundTag tag = new CompoundTag();
         tag.putInt("mobCount", this.mobCount);
         tag.putString("mobPool", this.mobPool);
         return tag;
      }

      public static RaidPreset.WaveSpawn deserialize(CompoundTag tag) {
         return new RaidPreset.WaveSpawn(tag.getInt("mobCount"), tag.getString("mobPool"));
      }
   }
}
