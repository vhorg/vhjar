package iskallia.vault.core.world.loot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import iskallia.vault.config.adapter.LootPoolAdapter;
import iskallia.vault.config.adapter.LootRollAdapter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class LootTable {
   private static final Gson GSON = new GsonBuilder()
      .registerTypeHierarchyAdapter(LootRoll.class, LootRollAdapter.INSTANCE)
      .registerTypeHierarchyAdapter(LootPool.class, LootPoolAdapter.INSTANCE)
      .setPrettyPrinting()
      .excludeFieldsWithoutExposeAnnotation()
      .create();
   @Expose
   protected List<LootTable.Entry> entries = new ArrayList<>();
   protected String path;

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

   public LootTable add(LootRoll roll, LootPool pool) {
      this.entries.add(new LootTable.Entry(roll, pool));
      return this;
   }

   public static class Entry {
      @Expose
      protected LootRoll roll;
      @Expose
      protected LootPool pool;

      public Entry(LootRoll roll, LootPool pool) {
         this.roll = roll;
         this.pool = pool;
      }

      public LootRoll getRoll() {
         return this.roll;
      }

      public LootPool getPool() {
         return this.pool;
      }
   }
}
