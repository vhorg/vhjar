package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.config.entry.LevelEntryList;
import iskallia.vault.util.data.WeightedList;
import iskallia.vault.world.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;

public class SummonAndKillAllBossesConfig extends Config {
   @Expose
   public LevelEntryList<SummonAndKillAllBossesConfig.Level> LEVELS;

   @Override
   public String getName() {
      return "summon_and_kill_all_bosses";
   }

   @Override
   protected void reset() {
      this.LEVELS = new LevelEntryList<>();
      SummonAndKillAllBossesConfig.Level level = new SummonAndKillAllBossesConfig.Level(5);
      level.POOLS
         .addAll(
            Arrays.asList(
               new SummonAndKillAllBossesConfig.Pool(2, 2)
                  .add(VaultMod.id("mobs"), 1)
                  .add(VaultMod.id("frail"), 1)
                  .add(VaultMod.id("rushed"), 1)
                  .add(VaultMod.id("difficult"), 1)
                  .add(VaultMod.id("extended"), 1)
                  .add(VaultMod.id("rotten"), 1)
                  .add(VaultMod.id("treasure_hunter"), 1),
               new SummonAndKillAllBossesConfig.Pool(1, 1).add(VaultMod.id("default"), 1).add(VaultMod.id("dummy"), 3)
            )
         );
      this.LEVELS.add(level);
   }

   public Set<VaultModifier<?>> getRandom(Random random, int level) {
      Optional<SummonAndKillAllBossesConfig.Level> override = this.LEVELS.getForLevel(level);
      if (override.isEmpty()) {
         return new HashSet<>();
      } else {
         List<SummonAndKillAllBossesConfig.Pool> pools = override.get().POOLS;
         if (pools == null) {
            return new HashSet<>();
         } else {
            Set<VaultModifier<?>> modifiers = new HashSet<>();
            pools.stream().map(pool -> pool.getRandom(random)).forEach(modifiers::addAll);
            return modifiers;
         }
      }
   }

   public SummonAndKillAllBossesConfig.Level getForLevel(int level) {
      for (int i = 0; i < this.LEVELS.size(); i++) {
         if (level < this.LEVELS.get(i).MIN_LEVEL) {
            if (i != 0) {
               return this.LEVELS.get(i - 1);
            }
            break;
         }

         if (i == this.LEVELS.size() - 1) {
            return this.LEVELS.get(i);
         }
      }

      return SummonAndKillAllBossesConfig.Level.EMPTY;
   }

   public static class Level implements LevelEntryList.ILevelEntry {
      public static SummonAndKillAllBossesConfig.Level EMPTY = new SummonAndKillAllBossesConfig.Level(0);
      @Expose
      public int MIN_LEVEL;
      @Expose
      public List<SummonAndKillAllBossesConfig.Pool> POOLS;

      public Level(int minLevel) {
         this.MIN_LEVEL = minLevel;
         this.POOLS = new ArrayList<>();
      }

      @Override
      public int getLevel() {
         return this.MIN_LEVEL;
      }
   }

   public static class Pool {
      @Expose
      public int MIN_ROLLS;
      @Expose
      public int MAX_ROLLS;
      @Expose
      public WeightedList<ResourceLocation> POOL;

      public Pool(int min, int max) {
         this.MIN_ROLLS = min;
         this.MAX_ROLLS = max;
         this.POOL = new WeightedList<>();
      }

      public SummonAndKillAllBossesConfig.Pool add(ResourceLocation modifierId, int weight) {
         this.POOL.add(modifierId, weight);
         return this;
      }

      public Set<VaultModifier<?>> getRandom(Random random) {
         int rolls = Math.min(this.MIN_ROLLS, this.MAX_ROLLS) + random.nextInt(Math.abs(this.MIN_ROLLS - this.MAX_ROLLS) + 1);
         Set<ResourceLocation> res = new HashSet<>();

         while (res.size() < rolls && res.size() < this.POOL.size()) {
            res.add(this.POOL.getRandom(random));
         }

         return res.stream().map(VaultModifierRegistry::getOpt).flatMap(Optional::stream).collect(Collectors.toSet());
      }
   }
}
