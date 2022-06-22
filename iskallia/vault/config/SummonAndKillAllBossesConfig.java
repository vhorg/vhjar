package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.data.WeightedList;
import iskallia.vault.world.vault.modifier.VaultModifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class SummonAndKillAllBossesConfig extends Config {
   @Expose
   public List<SummonAndKillAllBossesConfig.Level> LEVELS;

   @Override
   public String getName() {
      return "summon_and_kill_all_bosses";
   }

   @Override
   protected void reset() {
      this.LEVELS = new ArrayList<>();
      SummonAndKillAllBossesConfig.Level level = new SummonAndKillAllBossesConfig.Level(5);
      level.POOLS
         .addAll(
            Arrays.asList(
               new SummonAndKillAllBossesConfig.Pool(2, 2)
                  .add("Crowded", 1)
                  .add("Chaos", 1)
                  .add("Fast", 1)
                  .add("Rush", 1)
                  .add("Easy", 1)
                  .add("Hard", 1)
                  .add("Treasure", 1)
                  .add("Unlucky", 1),
               new SummonAndKillAllBossesConfig.Pool(1, 1).add("Locked", 1).add("Dummy", 3)
            )
         );
      this.LEVELS.add(level);
   }

   public Set<VaultModifier> getRandom(Random random, int level) {
      SummonAndKillAllBossesConfig.Level override = this.getForLevel(level);
      List<SummonAndKillAllBossesConfig.Pool> pools = override.POOLS;
      if (pools == null) {
         return new HashSet<>();
      } else {
         Set<VaultModifier> modifiers = new HashSet<>();
         pools.stream().map(pool -> pool.getRandom(random)).forEach(modifiers::addAll);
         return modifiers;
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

   public static class Level {
      public static SummonAndKillAllBossesConfig.Level EMPTY = new SummonAndKillAllBossesConfig.Level(0);
      @Expose
      public int MIN_LEVEL;
      @Expose
      public List<SummonAndKillAllBossesConfig.Pool> POOLS;

      public Level(int minLevel) {
         this.MIN_LEVEL = minLevel;
         this.POOLS = new ArrayList<>();
      }
   }

   public static class Pool {
      @Expose
      public int MIN_ROLLS;
      @Expose
      public int MAX_ROLLS;
      @Expose
      public WeightedList<String> POOL;

      public Pool(int min, int max) {
         this.MIN_ROLLS = min;
         this.MAX_ROLLS = max;
         this.POOL = new WeightedList<>();
      }

      public SummonAndKillAllBossesConfig.Pool add(String name, int weight) {
         this.POOL.add(name, weight);
         return this;
      }

      public Set<VaultModifier> getRandom(Random random) {
         int rolls = Math.min(this.MIN_ROLLS, this.MAX_ROLLS) + random.nextInt(Math.abs(this.MIN_ROLLS - this.MAX_ROLLS) + 1);
         Set<String> res = new HashSet<>();

         while (res.size() < rolls && res.size() < this.POOL.size()) {
            res.add(this.POOL.getRandom(random));
         }

         return res.stream().map(s -> ModConfigs.VAULT_MODIFIERS.getByName(s)).filter(Objects::nonNull).collect(Collectors.toSet());
      }
   }
}
