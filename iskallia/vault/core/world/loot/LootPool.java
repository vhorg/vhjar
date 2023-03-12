package iskallia.vault.core.world.loot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import iskallia.vault.config.adapter.LootPoolAdapter;
import iskallia.vault.core.Version;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.util.WeightedTree;
import iskallia.vault.core.world.loot.entry.ItemLootEntry;
import iskallia.vault.core.world.loot.entry.LootEntry;
import iskallia.vault.core.world.roll.IntRoll;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;

public class LootPool extends WeightedTree<LootEntry> {
   private static final Gson GSON = new GsonBuilder().registerTypeAdapter(LootPool.class, LootPoolAdapter.INSTANCE).setPrettyPrinting().create();
   protected String path;

   public LootPool addItem(Item item, CompoundTag nbt, IntRoll count, int weight) {
      this.addLeaf(new ItemLootEntry(item, nbt, count), weight);
      return this;
   }

   public LootPool addPool(Consumer<LootPool> root, int weight) {
      LootPool pool = new LootPool();
      this.addTree(pool, weight);
      root.accept(pool);
      return this;
   }

   public static LootPool fromPath(String path) {
      LootPool lootPool;
      try {
         lootPool = (LootPool)GSON.fromJson(new FileReader(path), LootPool.class);
      } catch (FileNotFoundException var3) {
         return null;
      }

      lootPool.path = path;
      return lootPool;
   }

   public String getPath() {
      return this.path;
   }

   public Optional<LootEntry> getRandomFlat(Version version, RandomSource random) {
      return super.getRandom(random).map(entry -> entry.flatten(version, random));
   }

   public Optional<LootEntry> getRandomFlat(Version version, RandomSource random, BiConsumer<WeightedList<Object>, Object> step) {
      return super.getRandom(random::nextInt, step).map(entry -> entry.flatten(version, random));
   }
}
