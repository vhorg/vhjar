package iskallia.vault.core.world.processor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import iskallia.vault.config.adapter.PaletteAdapter;
import iskallia.vault.core.world.processor.entity.EntityProcessor;
import iskallia.vault.core.world.processor.tile.TileProcessor;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Palette {
   private static final Gson GSON = new GsonBuilder().registerTypeHierarchyAdapter(Palette.class, PaletteAdapter.INSTANCE).setPrettyPrinting().create();
   protected String path;
   protected List<TileProcessor> tileProcessors = new ArrayList<>();
   protected List<EntityProcessor> entityProcessors = new ArrayList<>();
   protected List<Object> decorators = new ArrayList<>();

   public static Palette fromPath(String path) {
      Palette palette;
      try {
         palette = (Palette)GSON.fromJson(new FileReader(path), Palette.class);
      } catch (FileNotFoundException var3) {
         return null;
      }

      palette.path = path;
      return palette;
   }

   public String getPath() {
      return this.path;
   }

   public List<TileProcessor> getTileProcessors() {
      return this.tileProcessors;
   }

   public List<EntityProcessor> getEntityProcessors() {
      return this.entityProcessors;
   }

   public Palette processTile(TileProcessor tile) {
      this.tileProcessors.add(tile);
      return this;
   }

   public Palette processEntity(EntityProcessor entity) {
      this.entityProcessors.add(entity);
      return this;
   }

   public Palette copy() {
      Palette copy = new Palette();
      copy.tileProcessors.addAll(this.tileProcessors);
      copy.entityProcessors.addAll(this.entityProcessors);
      copy.decorators.addAll(this.decorators);
      return copy;
   }
}
