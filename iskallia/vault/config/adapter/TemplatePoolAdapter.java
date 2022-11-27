package iskallia.vault.config.adapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import iskallia.vault.core.data.key.PaletteKey;
import iskallia.vault.core.util.WeightedTree;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.world.template.data.DirectTemplateEntry;
import iskallia.vault.core.world.template.data.IndirectTemplateEntry;
import iskallia.vault.core.world.template.data.TemplateEntry;
import iskallia.vault.core.world.template.data.TemplatePool;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public class TemplatePoolAdapter extends WeightedTreeAdapter<TemplateEntry> {
   public static final TemplatePoolAdapter INSTANCE = new TemplatePoolAdapter();

   @Override
   public WeightedTree<TemplateEntry> create() {
      return new TemplatePool();
   }

   public String getName(TemplateEntry value) {
      if (value instanceof DirectTemplateEntry) {
         return "value";
      } else {
         return value instanceof IndirectTemplateEntry ? "reference" : null;
      }
   }

   @Override
   public <V extends JsonSerializer<TemplateEntry> & JsonDeserializer<TemplateEntry>> V getAdapter(String name) {
      return (V)(switch (name) {
         case "value" -> (JsonSerializer)TemplatePoolAdapter.DirectEntry.INSTANCE;
         case "reference" -> (JsonSerializer)TemplatePoolAdapter.IndirectEntry.INSTANCE;
         default -> null;
      });
   }

   public static class DirectEntry implements JsonSerializer<DirectTemplateEntry>, JsonDeserializer<DirectTemplateEntry> {
      public static final TemplatePoolAdapter.DirectEntry INSTANCE = new TemplatePoolAdapter.DirectEntry();

      public DirectTemplateEntry deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         JsonObject object = json.getAsJsonObject();
         ResourceLocation template = new ResourceLocation(object.get("template").getAsString());
         List<PaletteKey> palettes = new ArrayList<>();
         if (object.has("palettes")) {
            JsonArray array = object.get("palettes").getAsJsonArray();

            for (int i = 0; i < array.size(); i++) {
               palettes.add(VaultRegistry.PALETTE.getKey(array.get(i).getAsString()));
            }
         }

         return new DirectTemplateEntry(template, palettes);
      }

      public JsonElement serialize(DirectTemplateEntry value, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject object = new JsonObject();
         object.addProperty("template", value.getTemplate().getId().toString());
         JsonArray array = new JsonArray();

         for (PaletteKey palette : value.getPalettes()) {
            array.add(palette.getId().toString());
         }

         object.add("palettes", array);
         return object;
      }
   }

   public static class IndirectEntry implements JsonSerializer<IndirectTemplateEntry>, JsonDeserializer<IndirectTemplateEntry> {
      public static final TemplatePoolAdapter.IndirectEntry INSTANCE = new TemplatePoolAdapter.IndirectEntry();

      public IndirectTemplateEntry deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         return new IndirectTemplateEntry(new ResourceLocation(json.getAsString()));
      }

      public JsonElement serialize(IndirectTemplateEntry value, Type typeOfSrc, JsonSerializationContext context) {
         return new JsonPrimitive(value.getReferenceId().toString());
      }
   }
}
