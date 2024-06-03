package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.config.entry.LevelEntryList;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.world.roll.IntRoll;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;

public class CatalystConfig extends Config {
   @Expose
   public Map<ResourceLocation, LevelEntryList<CatalystConfig.Pool>> pools;

   @Override
   public String getName() {
      return "catalyst";
   }

   @Override
   protected void reset() {
      this.pools = new LinkedHashMap<>();
      this.pools
         .put(
            VaultMod.id("craft_wooden_cascade"),
            new LevelEntryList<CatalystConfig.Pool>()
               .put(
                  new CatalystConfig.Pool(
                     0,
                     new WeightedList<CatalystConfig.Entry>()
                        .add(new CatalystConfig.Entry(IntRoll.ofUniform(15, 25), Arrays.asList(VaultMod.id("wooden_cascade")), 1), 1)
                        .add(
                           new CatalystConfig.Entry(IntRoll.ofUniform(15, 25), Arrays.asList(VaultMod.id("wooden_cascade"), VaultMod.id("random_negative")), 1),
                           1
                        )
                  )
               )
         );
      this.pools
         .put(
            VaultMod.id("craft_coin_cascade"),
            new LevelEntryList<CatalystConfig.Pool>()
               .put(
                  new CatalystConfig.Pool(
                     0,
                     new WeightedList<CatalystConfig.Entry>()
                        .add(new CatalystConfig.Entry(IntRoll.ofUniform(15, 25), Arrays.asList(VaultMod.id("coin_cascade")), 1), 1)
                        .add(
                           new CatalystConfig.Entry(IntRoll.ofUniform(15, 25), Arrays.asList(VaultMod.id("coin_cascade"), VaultMod.id("random_negative")), 1),
                           1
                        )
                  )
               )
         );
   }

   public Optional<CompoundTag> generate(ResourceLocation id, int level, RandomSource random) {
      return Optional.ofNullable(this.pools.get(id)).flatMap(p -> p.getForLevel(level)).flatMap(pool -> pool.pool.getRandom(random)).map(entry -> {
         CompoundTag nbt = new CompoundTag();
         nbt.putInt("size", entry.size.get(random));
         nbt.putInt("model", entry.model);
         ListTag modifiers = new ListTag();

         for (ResourceLocation modifier : entry.modifiers) {
            modifiers.add(StringTag.valueOf(modifier.toString()));
         }

         nbt.put("modifiers", modifiers);
         return nbt;
      });
   }

   private static class Entry {
      @Expose
      private final IntRoll size;
      @Expose
      private final List<ResourceLocation> modifiers;
      @Expose
      private final int model;

      public Entry(IntRoll size, List<ResourceLocation> modifiers, int model) {
         this.size = size;
         this.modifiers = modifiers;
         this.model = model;
      }
   }

   private static class Pool implements LevelEntryList.ILevelEntry {
      @Expose
      private final int level;
      @Expose
      private final WeightedList<CatalystConfig.Entry> pool;

      public Pool(int level, WeightedList<CatalystConfig.Entry> pool) {
         this.level = level;
         this.pool = pool;
      }

      @Override
      public int getLevel() {
         return this.level;
      }
   }
}
