package iskallia.vault.core.world.loot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.world.roll.IntRoll;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

public class LootTable implements ISerializable<CompoundTag, JsonObject> {
   private static final Gson GSON = new GsonBuilder()
      .registerTypeHierarchyAdapter(LootTable.class, Adapters.LOOT_TABLE)
      .setPrettyPrinting()
      .excludeFieldsWithoutExposeAnnotation()
      .create();
   protected List<LootTable.Entry> entries = new ArrayList<>();
   protected String path;

   public LootTable() {
   }

   public LootTable(List<LootTable.Entry> entries) {
      this.entries = entries;
   }

   public static LootTable fromPath(String path) {
      LootTable lootTable;
      try {
         lootTable = (LootTable)GSON.fromJson(new FileReader(path), LootTable.class);
      } catch (FileNotFoundException var3) {
         return null;
      }

      lootTable.path = path;
      return lootTable;
   }

   public String getPath() {
      return this.path;
   }

   public List<LootTable.Entry> getEntries() {
      return this.entries;
   }

   public LootTable add(IntRoll roll, LootPool pool) {
      this.entries.add(new LootTable.Entry(roll, pool));
      return this;
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      CompoundTag nbt = new CompoundTag();
      ListTag list = new ListTag();

      for (LootTable.Entry entry : this.entries) {
         entry.writeNbt().ifPresent(list::add);
      }

      nbt.put("entries", list);
      return Optional.of(nbt);
   }

   public void readNbt(CompoundTag nbt) {
      this.entries.clear();

      for (Tag tag : nbt.getList("entries", 10)) {
         LootTable.Entry entry = new LootTable.Entry();
         entry.readNbt((CompoundTag)tag);
         this.entries.add(entry);
      }
   }

   @Override
   public Optional<JsonObject> writeJson() {
      JsonObject json = new JsonObject();
      JsonArray list = new JsonArray();

      for (LootTable.Entry entry : this.entries) {
         entry.writeJson().ifPresent(list::add);
      }

      json.add("entries", list);
      return Optional.of(json);
   }

   public void readJson(JsonObject json) {
      this.entries.clear();

      for (JsonElement element : json.get("entries").getAsJsonArray()) {
         LootTable.Entry entry = new LootTable.Entry();
         entry.readJson(element.getAsJsonObject());
         this.entries.add(entry);
      }
   }

   public static class Entry implements ISerializable<CompoundTag, JsonObject> {
      protected IntRoll roll;
      protected LootPool pool;

      public Entry() {
      }

      public Entry(IntRoll roll, LootPool pool) {
         this.roll = roll;
         this.pool = pool;
      }

      public IntRoll getRoll() {
         return this.roll;
      }

      public LootPool getPool() {
         return this.pool;
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         CompoundTag nbt = new CompoundTag();
         Adapters.INT_ROLL.writeNbt(this.roll).ifPresent(roll -> nbt.put("roll", roll));
         Adapters.LOOT_POOL.writeNbt(this.pool).ifPresent(pool -> nbt.put("pool", pool));
         return Optional.of(nbt);
      }

      public void readNbt(CompoundTag nbt) {
         this.roll = Adapters.INT_ROLL.readNbt(nbt.get("roll")).orElse(IntRoll.ofConstant(1));
         this.pool = Adapters.LOOT_POOL.readNbt((ListTag)nbt.get("pool")).orElse(new LootPool());
      }

      @Override
      public Optional<JsonObject> writeJson() {
         JsonObject json = new JsonObject();
         Adapters.INT_ROLL.writeJson(this.roll).ifPresent(roll -> json.add("roll", roll));
         Adapters.LOOT_POOL.writeJson(this.pool).ifPresent(pool -> json.add("pool", pool));
         return Optional.of(json);
      }

      public void readJson(JsonObject json) {
         this.roll = Adapters.INT_ROLL.readJson(json.get("roll")).orElse(IntRoll.ofConstant(1));
         this.pool = Adapters.LOOT_POOL.readJson(json.getAsJsonArray("pool")).orElse(new LootPool());
      }
   }
}
