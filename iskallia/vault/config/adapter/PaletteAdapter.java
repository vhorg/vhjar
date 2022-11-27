package iskallia.vault.config.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import iskallia.vault.core.world.processor.Palette;
import iskallia.vault.core.world.processor.entity.EntityProcessor;
import iskallia.vault.core.world.processor.tile.TileProcessor;
import java.lang.reflect.Type;

public class PaletteAdapter implements JsonSerializer<Palette>, JsonDeserializer<Palette> {
   public static final PaletteAdapter INSTANCE = new PaletteAdapter();

   public Palette deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject object = json.getAsJsonObject();
      Palette palette = new Palette();
      if (object.has("tile_processors")) {
         for (JsonElement tile : object.get("tile_processors").getAsJsonArray()) {
            palette.processTile((TileProcessor)ProcessorAdapter.INSTANCE.deserialize(tile, TileProcessor.class, context));
         }
      }

      if (object.has("entity_processors")) {
         for (JsonElement entity : object.get("entity_processors").getAsJsonArray()) {
            palette.processTile((TileProcessor)ProcessorAdapter.INSTANCE.deserialize(entity, EntityProcessor.class, context));
         }
      }

      return palette;
   }

   public JsonElement serialize(Palette src, Type typeOfSrc, JsonSerializationContext context) {
      return null;
   }
}
