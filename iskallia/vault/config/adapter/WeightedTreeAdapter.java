package iskallia.vault.config.adapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import iskallia.vault.core.util.WeightedTree;
import java.lang.reflect.Type;
import java.util.HashSet;

public abstract class WeightedTreeAdapter<T> implements JsonSerializer<WeightedTree<T>>, JsonDeserializer<WeightedTree<T>> {
   public abstract WeightedTree<T> create();

   public abstract String getName(T var1);

   public abstract <V extends JsonSerializer<T> & JsonDeserializer<T>> V getAdapter(String var1);

   public WeightedTree<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      WeightedTree<T> tree = this.create();
      JsonArray array = json.getAsJsonArray();

      for (int i = 0; i < array.size(); i++) {
         JsonObject element = array.get(i).getAsJsonObject();
         int weight = element.get("weight").getAsInt();
         HashSet<String> keys = new HashSet<>(element.keySet());
         keys.remove("weight");
         String name = keys.iterator().next();
         if (name.equals("pool")) {
            tree.addTree(this.deserialize(element.get(name), typeOfT, context), weight);
         } else {
            tree.addLeaf((T)((JsonDeserializer)this.getAdapter(name)).deserialize(element.get(name), typeOfT, context), weight);
         }
      }

      return tree;
   }

   public JsonElement serialize(WeightedTree<T> src, Type typeOfSrc, JsonSerializationContext context) {
      JsonArray array = new JsonArray();
      src.getChildren().forEach((object, weight) -> {
         JsonObject element = new JsonObject();
         element.addProperty("weight", weight);
         if (object instanceof WeightedTree) {
            element.add("pool", this.serialize((WeightedTree<T>)object, typeOfSrc, context));
         } else {
            String name = this.getName((T)object);
            element.add(name, this.getAdapter(name).serialize(object, typeOfSrc, context));
         }
      });
      return array;
   }
}
