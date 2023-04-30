package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.config.entry.LevelEntryList;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public class VaultModifierPoolsConfig extends Config {
   private static final LevelEntryList<VaultModifierPoolsConfig.Level> EMPTY = new LevelEntryList<>();
   @Expose
   public Map<ResourceLocation, LevelEntryList<VaultModifierPoolsConfig.Level>> pools;

   @Override
   public String getName() {
      return "vault_modifier_pools";
   }

   @Override
   protected void reset() {
      this.pools = new LinkedHashMap<>();
      LevelEntryList<VaultModifierPoolsConfig.Level> LEVELS = new LevelEntryList<>();
      this.pools.put(VaultMod.id("default"), LEVELS);
      VaultModifierPoolsConfig.Level level = new VaultModifierPoolsConfig.Level(5, new ArrayList<>());
      level.entries
         .addAll(
            Arrays.asList(
               new VaultModifierPoolsConfig.Entry(2, 2)
                  .add(VaultMod.id("mobs"), 1)
                  .add(VaultMod.id("frail"), 1)
                  .add(VaultMod.id("rushed"), 1)
                  .add(VaultMod.id("difficult"), 1)
                  .add(VaultMod.id("extended"), 1)
                  .add(VaultMod.id("rotten"), 1)
                  .add(VaultMod.id("treasure_hunter"), 1),
               new VaultModifierPoolsConfig.Entry(1, 1).add(VaultMod.id("default"), 1).add(VaultMod.id("dummy"), 3)
            )
         );
      LEVELS.add(level);
   }

   public List<VaultModifier<?>> getRandom(ResourceLocation id, int level, RandomSource random) {
      List<VaultModifier<?>> modifiers = new ArrayList<>();

      for (VaultModifierPoolsConfig.Entry entry : this.getForLevel(id, level).entries) {
         entry.fillRandom(random, modifiers);
      }

      return modifiers;
   }

   public Set<VaultModifier<?>> getRandom(Random random, int level, VaultModifierPoolsConfig.ModifierPoolType type, @Nullable ResourceLocation objectiveKey) {
      return null;
   }

   public VaultModifierPoolsConfig.Level getForLevel(ResourceLocation id, int level) {
      return this.pools.getOrDefault(id, EMPTY).getForLevel(level).orElse(VaultModifierPoolsConfig.Level.EMPTY);
   }

   public static class Entry {
      @Expose
      public int min;
      @Expose
      public int max;
      @Expose
      public WeightedList<ResourceLocation> pool;

      public Entry(int min, int max) {
         this.min = min;
         this.max = max;
         this.pool = new WeightedList<>();
      }

      public VaultModifierPoolsConfig.Entry add(ResourceLocation id, int weight) {
         this.pool.add(id, weight);
         return this;
      }

      public Set<VaultModifier<?>> fillRandom(Random random) {
         int rolls = Math.min(this.min, this.max) + random.nextInt(Math.abs(this.min - this.max) + 1);
         Set<ResourceLocation> result = new HashSet<>();

         while (result.size() < rolls && result.size() < this.pool.size()) {
         }

         return result.stream().map(VaultModifierRegistry::getOpt).flatMap(Optional::stream).collect(Collectors.toSet());
      }

      public void fillRandom(RandomSource random, List<VaultModifier<?>> modifiers) {
         int rolls = Math.min(this.min, this.max) + random.nextInt(Math.abs(this.min - this.max) + 1);

         for (int i = 0; i < rolls; i++) {
            this.pool.getRandom(random).flatMap(VaultModifierRegistry::getOpt).ifPresent(modifier -> modifiers.add(modifier));
         }
      }
   }

   public static class Level implements LevelEntryList.ILevelEntry {
      public static VaultModifierPoolsConfig.Level EMPTY = new VaultModifierPoolsConfig.Level(0, new ArrayList<>());
      @Expose
      public int level;
      @Expose
      public List<VaultModifierPoolsConfig.Entry> entries;

      public Level(int level, List<VaultModifierPoolsConfig.Entry> entries) {
         this.level = level;
         this.entries = entries;
      }

      @Override
      public int getLevel() {
         return this.level;
      }
   }

   public static enum ModifierPoolType {
      DEFAULT,
      RAFFLE,
      RAID,
      FINAL_VELARA,
      FINAL_VELARA_ADDS,
      FINAL_TENOS,
      FINAL_TENOS_ADDS,
      FINAL_WENDARR,
      FINAL_IDONA,
      FINAL_IDONA_ADDS;
   }
}
