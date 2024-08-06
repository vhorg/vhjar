package iskallia.vault.config.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.vault.objective.bingo.BingoItem;
import java.lang.reflect.Type;
import java.util.Optional;

public class BingoItemAdapter implements JsonSerializer<BingoItem>, JsonDeserializer<BingoItem> {
   public static final BingoItemAdapter INSTANCE = new BingoItemAdapter();

   private BingoItemAdapter() {
   }

   public BingoItem deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
      return jsonElement instanceof JsonObject jsonObject ? Adapters.BINGO_ITEM.readJson(jsonObject).orElse(null) : null;
   }

   public JsonElement serialize(BingoItem bingoItem, Type type, JsonSerializationContext jsonSerializationContext) {
      Optional<JsonObject> json = Adapters.BINGO_ITEM.writeJson(bingoItem);
      return (JsonElement)(json.isPresent() ? (JsonElement)json.get() : JsonNull.INSTANCE);
   }
}
