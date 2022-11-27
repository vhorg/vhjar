package iskallia.vault.core.world.loot.generator;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.LootTableKey;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.LootGenerationEvent;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.MathUtils;
import iskallia.vault.core.world.loot.LootPool;
import iskallia.vault.core.world.loot.LootTable;
import it.unimi.dsi.fastutil.doubles.Double2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2DoubleMap;
import it.unimi.dsi.fastutil.longs.Long2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

public class TieredLootTableGenerator extends LootTableGenerator {
   public static final Map<TieredLootTableGenerator.CDFKey, TieredLootTableGenerator.CDF> CACHE = new HashMap<>();
   public float itemRarity;
   public float itemQuantity;
   private int[] key;
   protected final Map<LootPool, Integer> poolToIndex = new HashMap<>();
   protected int[] frequencies;
   protected double cdf;

   public TieredLootTableGenerator(Version version, LootTableKey table, float itemRarity, float itemQuantity) {
      super(version, table);
      this.itemRarity = itemRarity;
      this.itemQuantity = itemQuantity;
   }

   public static void clearCache() {
      CACHE.clear();
   }

   public static void addCache(LootTable table) {
      LootPool pool = table.getEntries().get(0).getPool();
      int[] base = new int[pool.getChildren().size() + 1];
      int index = 1;

      for (Entry<Object, Integer> e : pool.getChildren().entrySet()) {
         base[index++] = e.getValue();
      }

      for (int i = 1; i < 54; i++) {
         int[] key = new int[base.length];
         System.arraycopy(base, 0, key, 0, key.length);
         key[0] = i;
         CACHE.computeIfAbsent(new TieredLootTableGenerator.CDFKey(key), TieredLootTableGenerator.CDF::new);
      }
   }

   public static boolean supports(LootTable table) {
      return table.getEntries().size() != 1
         ? false
         : !table.getEntries().get(0).getPool().getChildren().keySet().stream().anyMatch(o -> !(o instanceof LootPool));
   }

   public int[] getKey() {
      return this.key;
   }

   public int[] getFrequencies() {
      return this.frequencies;
   }

   public double getCDF() {
      return this.cdf;
   }

   @Override
   public void generate(RandomSource random) {
      CommonEvents.LOOT_GENERATION.invoke(this, LootGenerationEvent.Phase.PRE);
      LootTable.Entry entry = this.table.get(this.version).getEntries().get(0);
      int roll = entry.getRoll().get(random);
      roll = (int)(roll * (1.0F + this.itemQuantity));
      LootPool pool = entry.getPool();
      this.key = new int[pool.getChildren().size() + 1];
      this.key[0] = roll;
      int index = 1;

      for (Entry<Object, Integer> e : pool.getChildren().entrySet()) {
         this.poolToIndex.put((LootPool)e.getKey(), index - 1);
         this.key[index++] = e.getValue();
      }

      this.frequencies = new int[pool.getChildren().size()];
      this.generateEntry(roll, entry, random);
      this.cdf = CACHE.computeIfAbsent(new TieredLootTableGenerator.CDFKey(this.key), TieredLootTableGenerator.CDF::new).get(this.frequencies);
      CommonEvents.LOOT_GENERATION.invoke(this, LootGenerationEvent.Phase.POST);
   }

   protected void generateEntry(int roll, LootTable.Entry entry, RandomSource random) {
      LootPool adjustedPool = new LootPool();
      Iterator<Entry<Object, Integer>> it = entry.getPool().getChildren().entrySet().iterator();

      for (int index = 0; it.hasNext(); index++) {
         Entry<Object, Integer> child = it.next();
         if (index != 0) {
            adjustedPool.addTree((LootPool)child.getKey(), (int)(child.getValue().intValue() * (1.0F + this.itemRarity)));
         } else {
            adjustedPool.addTree((LootPool)child.getKey(), child.getValue());
         }
      }

      for (int i = 0; i < roll; i++) {
         adjustedPool.getRandomFlat(this.version, random, (children, next) -> {
            if (this.poolToIndex.containsKey(next)) {
               this.frequencies[this.poolToIndex.get(next)]++;
            }
         }).map(e -> e.getStack(random)).ifPresent(this.items::add);
      }
   }

   public static class CDF {
      public static BigDecimal[] FACTORIAL = new BigDecimal[256];
      private final int samples;
      private final int[] weights;
      private final int totalWeight;
      private final double[] scores;
      private final double[] probabilities;
      private final int packBits;
      private final Long2DoubleMap map;

      protected CDF(TieredLootTableGenerator.CDFKey key) {
         this.samples = key.key[0];
         this.weights = new int[key.key.length - 1];
         System.arraycopy(key.key, 1, this.weights, 0, this.weights.length);
         this.totalWeight = Arrays.stream(this.weights).sum();
         this.scores = Arrays.stream(this.weights).mapToDouble(x -> (double)this.totalWeight / x).toArray();
         this.probabilities = Arrays.stream(this.weights).mapToDouble(x -> (double)x / this.totalWeight).toArray();
         this.packBits = 64 / this.weights.length;
         this.map = this.compute();
      }

      public Long2DoubleMap getMap() {
         return this.map;
      }

      public double get(int[] frequencies) {
         return this.map.get(this.pack(frequencies));
      }

      public Long2DoubleMap compute() {
         Double2ObjectArrayMap<LongList> result = new Double2ObjectArrayMap();
         this.permute(
            0,
            this.samples,
            0,
            new int[this.weights.length],
            stack -> ((LongList)result.computeIfAbsent(this.getHeuristic(stack), l -> new LongArrayList())).add(this.pack(stack))
         );
         List<it.unimi.dsi.fastutil.doubles.Double2ObjectMap.Entry<LongList>> sorted = result.double2ObjectEntrySet()
            .stream()
            .sorted(Comparator.comparingDouble(it.unimi.dsi.fastutil.doubles.Double2ObjectMap.Entry::getDoubleKey))
            .toList();
         double[] cumulative = new double[]{0.0};
         Long2DoubleMap map = new Long2DoubleOpenHashMap(sorted.size());
         sorted.forEach(e -> {
            for (int j = 0; j < ((LongList)e.getValue()).size(); j++) {
               long i = ((LongList)e.getValue()).getLong(j);
               cumulative[0] += this.getProbability(this.unpack(i));
               map.put(i, cumulative[0]);
            }
         });
         return map;
      }

      public long pack(int[] frequencies) {
         long v = 0L;

         for (int frequency : frequencies) {
            v <<= this.packBits;
            v |= frequency;
         }

         return v;
      }

      public int[] unpack(long packed) {
         int[] frequencies = new int[this.weights.length];

         for (int i = frequencies.length - 1; i >= 0; i--) {
            frequencies[i] = (int)MathUtils.mask(packed, this.packBits);
            packed >>>= this.packBits;
         }

         return frequencies;
      }

      public void permute(int sum, int total, int depth, int[] stack, Consumer<int[]> action) {
         if (depth == stack.length) {
            action.accept(stack);
         } else if (depth == stack.length - 1) {
            stack[depth] = total - sum;
            this.permute(total, total, depth + 1, stack, action);
         } else {
            for (int i = 0; i <= total - sum; i++) {
               stack[depth] = i;
               this.permute(sum + i, total, depth + 1, stack, action);
            }
         }
      }

      public double getHeuristic(int[] frequencies) {
         double b = 0.0;

         for (int i = 0; i < frequencies.length; i++) {
            b -= this.scores[i] * frequencies[i];
         }

         return b;
      }

      public double getProbability(int[] frequencies) {
         int sum = 0;

         for (int frequency : frequencies) {
            sum += frequency;
         }

         BigDecimal a = BigDecimal.ONE;

         for (int frequency : frequencies) {
            a = a.multiply(FACTORIAL[frequency]);
         }

         a = FACTORIAL[sum].divide(a, 10, RoundingMode.HALF_UP);
         double b = 1.0;

         for (int i = 0; i < frequencies.length; i++) {
            b *= Math.pow(this.probabilities[i], frequencies[i]);
         }

         return a.doubleValue() * b;
      }

      static {
         FACTORIAL[0] = BigDecimal.ONE;

         for (int i = 1; i < FACTORIAL.length; i++) {
            FACTORIAL[i] = FACTORIAL[i - 1].multiply(BigDecimal.valueOf((long)i)).setScale(0, RoundingMode.UNNECESSARY);
         }
      }
   }

   public static class CDFKey {
      public final int[] key;

      public CDFKey(int[] key) {
         this.key = key;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            TieredLootTableGenerator.CDFKey cdfKey = (TieredLootTableGenerator.CDFKey)o;
            return Arrays.equals(this.key, cdfKey.key);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Arrays.hashCode(this.key);
      }
   }
}
