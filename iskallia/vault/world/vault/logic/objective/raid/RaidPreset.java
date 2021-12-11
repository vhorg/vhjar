package iskallia.vault.world.vault.logic.objective.raid;

import iskallia.vault.config.RaidConfig;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.MathUtilities;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

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

   public CompoundNBT serialize() {
      CompoundNBT tag = new CompoundNBT();
      ListNBT waveTag = new ListNBT();
      this.waves.forEach(wave -> waveTag.add(wave.serialize()));
      tag.func_218657_a("waves", waveTag);
      return tag;
   }

   public static RaidPreset deserialize(CompoundNBT tag) {
      RaidPreset preset = new RaidPreset();
      ListNBT waveTag = tag.func_150295_c("waves", 10);

      for (int i = 0; i < waveTag.size(); i++) {
         preset.waves.add(RaidPreset.CompoundWaveSpawn.deserialize(waveTag.func_150305_b(i)));
      }

      return preset;
   }

   public static class CompoundWaveSpawn {
      private final List<RaidPreset.WaveSpawn> waveSpawns = new ArrayList<>();

      public List<RaidPreset.WaveSpawn> getWaveSpawns() {
         return this.waveSpawns;
      }

      public CompoundNBT serialize() {
         CompoundNBT tag = new CompoundNBT();
         ListNBT waveTag = new ListNBT();
         this.waveSpawns.forEach(wave -> waveTag.add(wave.serialize()));
         tag.func_218657_a("waves", waveTag);
         return tag;
      }

      public static RaidPreset.CompoundWaveSpawn deserialize(CompoundNBT tag) {
         RaidPreset.CompoundWaveSpawn compound = new RaidPreset.CompoundWaveSpawn();
         ListNBT waveTag = tag.func_150295_c("waves", 10);

         for (int i = 0; i < waveTag.size(); i++) {
            compound.waveSpawns.add(RaidPreset.WaveSpawn.deserialize(waveTag.func_150305_b(i)));
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

      public CompoundNBT serialize() {
         CompoundNBT tag = new CompoundNBT();
         tag.func_74768_a("mobCount", this.mobCount);
         tag.func_74778_a("mobPool", this.mobPool);
         return tag;
      }

      public static RaidPreset.WaveSpawn deserialize(CompoundNBT tag) {
         return new RaidPreset.WaveSpawn(tag.func_74762_e("mobCount"), tag.func_74779_i("mobPool"));
      }
   }
}
