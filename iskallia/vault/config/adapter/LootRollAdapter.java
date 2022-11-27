package iskallia.vault.config.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import iskallia.vault.core.world.loot.LootRoll;
import java.lang.reflect.Type;

public class LootRollAdapter implements JsonSerializer<LootRoll>, JsonDeserializer<LootRoll> {
   public static final LootRollAdapter INSTANCE = new LootRollAdapter();

   public LootRoll deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject object = json.getAsJsonObject();
      String type = object.get("type").getAsString();
      switch (type) {
         case "constant":
            return LootRoll.ofConstant(object.get("count").getAsInt());
         case "uniform":
            return LootRoll.ofUniform(object.get("min").getAsInt(), object.get("max").getAsInt());
         default:
            return null;
      }
   }

   public JsonElement serialize(LootRoll value, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject object = new JsonObject();
      if (value instanceof LootRoll.Constant constant) {
         object.addProperty("type", "constant");
         object.addProperty("count", constant.getCount());
      } else if (value instanceof LootRoll.Uniform uniform) {
         object.addProperty("type", "uniform");
         object.addProperty("min", uniform.getMin());
         object.addProperty("max", uniform.getMax());
      }

      return object;
   }
}
