package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.config.entry.LevelEntryList;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.world.roll.FloatRoll;
import iskallia.vault.core.world.roll.IntRoll;
import iskallia.vault.item.data.InscriptionData;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;

public class InscriptionConfig extends Config {
   @Expose
   public Map<ResourceLocation, LevelEntryList<InscriptionConfig.Pool>> pools;
   @Expose
   public Map<ResourceLocation, Integer> poolToModel;
   @Expose
   public List<Integer> ringWeights;

   @Override
   public String getName() {
      return "inscription";
   }

   public int getModel(ResourceLocation pool) {
      return this.poolToModel.getOrDefault(pool, 0);
   }

   public List<Integer> getRingWeights() {
      return this.ringWeights;
   }

   @Override
   protected void reset() {
      this.pools = new LinkedHashMap<>();
      this.pools
         .put(
            VaultMod.id("test"),
            new LevelEntryList<InscriptionConfig.Pool>()
               .put(
                  new InscriptionConfig.Pool(
                     0,
                     new WeightedList<InscriptionConfig.Entry>()
                        .add(
                           new InscriptionConfig.Entry(
                              Arrays.asList(new InscriptionData.Entry(VaultMod.id("vault/rooms/challenge/crystal_caves"), null, 1, 16711935)),
                              FloatRoll.ofUniform(0.1F, 0.2F),
                              IntRoll.ofUniform(200, 600),
                              IntRoll.ofUniform(10, 30),
                              null,
                              IntRoll.ofUniform(0, 15)
                           ),
                           16
                        )
                        .add(
                           new InscriptionConfig.Entry(
                              Arrays.asList(new InscriptionData.Entry(VaultMod.id("vault/rooms/challenge/digsite"), null, 1, 16711935)),
                              FloatRoll.ofUniform(0.1F, 0.2F),
                              IntRoll.ofUniform(200, 600),
                              IntRoll.ofUniform(10, 30),
                              null,
                              IntRoll.ofUniform(0, 15)
                           ),
                           16
                        )
                        .add(
                           new InscriptionConfig.Entry(
                              Arrays.asList(new InscriptionData.Entry(VaultMod.id("vault/rooms/challenge/dragon"), null, 1, 16711935)),
                              FloatRoll.ofUniform(0.1F, 0.2F),
                              IntRoll.ofUniform(200, 600),
                              IntRoll.ofUniform(10, 30),
                              null,
                              IntRoll.ofUniform(0, 15)
                           ),
                           16
                        )
                        .add(
                           new InscriptionConfig.Entry(
                              Arrays.asList(new InscriptionData.Entry(VaultMod.id("vault/rooms/challenge/factory"), null, 1, 16711935)),
                              FloatRoll.ofUniform(0.1F, 0.2F),
                              IntRoll.ofUniform(200, 600),
                              IntRoll.ofUniform(10, 30),
                              null,
                              IntRoll.ofUniform(0, 15)
                           ),
                           16
                        )
                        .add(
                           new InscriptionConfig.Entry(
                              Arrays.asList(new InscriptionData.Entry(VaultMod.id("vault/rooms/challenge/village"), null, 1, 16711935)),
                              FloatRoll.ofUniform(0.1F, 0.2F),
                              IntRoll.ofUniform(200, 600),
                              IntRoll.ofUniform(10, 30),
                              null,
                              IntRoll.ofUniform(0, 15)
                           ),
                           16
                        )
                        .add(
                           new InscriptionConfig.Entry(
                              Arrays.asList(new InscriptionData.Entry(VaultMod.id("vault/rooms/challenge/wildwest"), null, 1, 16711935)),
                              FloatRoll.ofUniform(0.1F, 0.2F),
                              IntRoll.ofUniform(200, 600),
                              IntRoll.ofUniform(10, 30),
                              null,
                              IntRoll.ofUniform(0, 15)
                           ),
                           16
                        )
                        .add(
                           new InscriptionConfig.Entry(
                              Arrays.asList(new InscriptionData.Entry(VaultMod.id("vault/rooms/challenge/x-mark"), null, 1, 16711935)),
                              FloatRoll.ofUniform(0.1F, 0.2F),
                              IntRoll.ofUniform(200, 600),
                              IntRoll.ofUniform(10, 30),
                              null,
                              IntRoll.ofUniform(0, 15)
                           ),
                           16
                        )
                        .add(
                           new InscriptionConfig.Entry(
                              Arrays.asList(new InscriptionData.Entry(VaultMod.id("vault/rooms/omega/cube"), null, 1, 7012096)),
                              FloatRoll.ofUniform(0.1F, 0.2F),
                              IntRoll.ofUniform(200, 600),
                              IntRoll.ofUniform(10, 30),
                              null,
                              IntRoll.ofUniform(0, 15)
                           ),
                           4
                        )
                        .add(
                           new InscriptionConfig.Entry(
                              Arrays.asList(new InscriptionData.Entry(VaultMod.id("vault/rooms/omega/blacksmith"), null, 1, 7012096)),
                              FloatRoll.ofUniform(0.1F, 0.2F),
                              IntRoll.ofUniform(200, 600),
                              IntRoll.ofUniform(10, 30),
                              null,
                              IntRoll.ofUniform(0, 15)
                           ),
                           4
                        )
                        .add(
                           new InscriptionConfig.Entry(
                              Arrays.asList(new InscriptionData.Entry(VaultMod.id("vault/rooms/omega/digsite"), null, 1, 7012096)),
                              FloatRoll.ofUniform(0.1F, 0.2F),
                              IntRoll.ofUniform(200, 600),
                              IntRoll.ofUniform(10, 30),
                              null,
                              IntRoll.ofUniform(0, 15)
                           ),
                           4
                        )
                        .add(
                           new InscriptionConfig.Entry(
                              Arrays.asList(new InscriptionData.Entry(VaultMod.id("vault/rooms/omega/mine"), null, 1, 7012096)),
                              FloatRoll.ofUniform(0.1F, 0.2F),
                              IntRoll.ofUniform(200, 600),
                              IntRoll.ofUniform(10, 30),
                              null,
                              IntRoll.ofUniform(0, 15)
                           ),
                           4
                        )
                        .add(
                           new InscriptionConfig.Entry(
                              Arrays.asList(new InscriptionData.Entry(VaultMod.id("vault/rooms/omega/painting"), null, 1, 7012096)),
                              FloatRoll.ofUniform(0.1F, 0.2F),
                              IntRoll.ofUniform(200, 600),
                              IntRoll.ofUniform(10, 30),
                              null,
                              IntRoll.ofUniform(0, 15)
                           ),
                           4
                        )
                        .add(
                           new InscriptionConfig.Entry(
                              Arrays.asList(new InscriptionData.Entry(VaultMod.id("vault/rooms/omega/vendor"), null, 1, 7012096)),
                              FloatRoll.ofUniform(0.1F, 0.2F),
                              IntRoll.ofUniform(200, 600),
                              IntRoll.ofUniform(10, 30),
                              null,
                              IntRoll.ofUniform(0, 15)
                           ),
                           4
                        )
                  )
               )
         );
      this.poolToModel = new LinkedHashMap<>();
   }

   public Optional<InscriptionData> generate(ResourceLocation id, int level, RandomSource random) {
      return Optional.ofNullable(this.pools.get(id)).flatMap(p -> p.getForLevel(level)).flatMap(pool -> pool.pool.getRandom(random)).map(entry -> {
         InscriptionData data = InscriptionData.empty();
         entry.entries.forEach(data::add);
         if (entry.completion != null) {
            data.setCompletion(entry.completion.get(random));
         }

         if (entry.time != null) {
            data.setTime(entry.time.get(random));
         }

         if (entry.instability != null) {
            data.setInstability(entry.instability.get(random));
         }

         if (entry.size != null) {
            data.setSize(entry.size.get(random));
         }

         data.setModel(entry.model.get(random));
         data.setColor(entry.color);
         return data;
      });
   }

   public static class Entry {
      @Expose
      private final List<InscriptionData.Entry> entries;
      @Expose
      private final FloatRoll completion;
      @Expose
      private final IntRoll time;
      @Expose
      private final FloatRoll instability;
      @Expose
      private final IntRoll size;
      @Expose
      private final IntRoll model;
      @Expose
      private final Integer color;

      public Entry(List<InscriptionData.Entry> entries, FloatRoll completion, IntRoll size, IntRoll time, FloatRoll instability, IntRoll model) {
         this.entries = entries;
         this.completion = completion;
         this.time = time;
         this.instability = instability;
         this.size = size;
         this.model = model;
         this.color = null;
      }
   }

   public static class Pool implements LevelEntryList.ILevelEntry {
      @Expose
      private final int level;
      @Expose
      private final WeightedList<InscriptionConfig.Entry> pool;

      public Pool(int level, WeightedList<InscriptionConfig.Entry> pool) {
         this.level = level;
         this.pool = pool;
      }

      @Override
      public int getLevel() {
         return this.level;
      }
   }
}
