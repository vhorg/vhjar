package iskallia.vault.config;

import com.google.common.collect.Lists;
import com.google.gson.annotations.Expose;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.MathHelper;

public class FinalRaidConfig extends Config {
   @Expose
   private final Map<String, List<RaidConfig.Level>> mobPools = new HashMap<>();
   @Expose
   private final List<RaidConfig.WaveSetup> raidWaves = new ArrayList<>();

   @Nullable
   public RaidConfig.MobPool getPool(String pool, int level) {
      List<RaidConfig.Level> mobLevelPool = this.mobPools.get(pool);
      return mobLevelPool == null ? null : this.getForLevel(mobLevelPool, level).orElse(RaidConfig.DEFAULT).mobPool;
   }

   public RaidConfig.WaveSetup getWaveSetup(int index) {
      index = MathHelper.func_76125_a(index, 0, this.raidWaves.size() - 1);
      return this.raidWaves.get(index);
   }

   @Override
   public String getName() {
      return "final_raid";
   }

   @Override
   protected void reset() {
      this.mobPools.clear();
      this.raidWaves.clear();
      this.mobPools
         .put(
            "ranged",
            Lists.newArrayList(
               new RaidConfig.Level[]{
                  new RaidConfig.Level(0, new RaidConfig.MobPool().add(EntityType.field_200741_ag, 1)),
                  new RaidConfig.Level(75, new RaidConfig.MobPool().add(EntityType.field_200741_ag, 1).add(EntityType.field_200750_ap, 1))
               }
            )
         );
      this.mobPools
         .put(
            "melee",
            Lists.newArrayList(
               new RaidConfig.Level[]{
                  new RaidConfig.Level(0, new RaidConfig.MobPool().add(EntityType.field_200725_aD, 1)),
                  new RaidConfig.Level(50, new RaidConfig.MobPool().add(EntityType.field_200725_aD, 2).add(EntityType.field_200758_ax, 1))
               }
            )
         );
      RaidConfig.WaveSetup waveSetup = new RaidConfig.WaveSetup()
         .addWave(new RaidConfig.CompoundWave(new RaidConfig.ConfiguredWave(2, 3, "ranged"), new RaidConfig.ConfiguredWave(2, 3, "melee")))
         .addWave(new RaidConfig.CompoundWave(new RaidConfig.ConfiguredWave(4, 5, "ranged"), new RaidConfig.ConfiguredWave(4, 6, "melee")))
         .addWave(new RaidConfig.CompoundWave(new RaidConfig.ConfiguredWave(6, 7, "ranged"), new RaidConfig.ConfiguredWave(5, 8, "melee")));
      this.raidWaves.add(waveSetup);
      this.raidWaves.add(waveSetup);
      this.raidWaves.add(waveSetup);
   }

   private Optional<RaidConfig.Level> getForLevel(List<RaidConfig.Level> levels, int level) {
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
}
