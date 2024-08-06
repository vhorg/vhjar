package iskallia.vault.antique.condition;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class AntiqueConditionTag extends AntiqueCondition {
   private final List<String> tags = new ArrayList<>();

   public void addTag(String... tag) {
      Collections.addAll(this.tags, tag);
   }

   @Override
   public boolean test(DropConditionContext context) {
      return new HashSet<>(this.tags).containsAll(context.getTags());
   }

   @Override
   public void deserialize(JsonDeserializationContext ctx, JsonObject json) {
      if (!json.has("tag")) {
         throw new JsonSyntaxException("Missing tag, expected to find a string or array");
      } else {
         JsonElement typeElement = json.get("tag");
         if (typeElement.isJsonArray()) {
            for (JsonElement element : typeElement.getAsJsonArray()) {
               if (!element.isJsonPrimitive()) {
                  throw new JsonSyntaxException("Expected tag to be a string, was " + element);
               }

               this.tags.add(element.getAsString());
            }
         } else {
            if (!typeElement.isJsonPrimitive()) {
               throw new JsonSyntaxException("Expected tag to be a string or an array of strings, was " + typeElement);
            }

            this.tags.add(typeElement.getAsString());
         }
      }
   }

   @Override
   public JsonObject serialize(JsonSerializationContext ctx) {
      JsonObject json = new JsonObject();
      JsonArray array = new JsonArray();
      this.tags.forEach(array::add);
      json.add("tag", array);
      return json;
   }
}
