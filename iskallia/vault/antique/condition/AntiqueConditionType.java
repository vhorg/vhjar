package iskallia.vault.antique.condition;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AntiqueConditionType extends AntiqueCondition {
   private final List<DropConditionType> types = new ArrayList<>();

   public void addType(DropConditionType... type) {
      Collections.addAll(this.types, type);
   }

   @Override
   public boolean test(DropConditionContext context) {
      return this.types.contains(context.getType());
   }

   @Override
   public void deserialize(JsonDeserializationContext ctx, JsonObject json) {
      if (!json.has("types")) {
         throw new JsonSyntaxException("Missing type, expected to find a string");
      } else {
         JsonElement typeElement = json.get("types");
         List<String> types = new ArrayList<>();
         if (typeElement.isJsonArray()) {
            for (JsonElement element : typeElement.getAsJsonArray()) {
               if (!element.isJsonPrimitive()) {
                  throw new JsonSyntaxException("Expected type to be a string, was " + element);
               }

               types.add(element.getAsString());
            }
         } else {
            if (!typeElement.isJsonPrimitive()) {
               throw new JsonSyntaxException("Expected type to be a string or an array of strings, was " + typeElement);
            }

            types.add(typeElement.getAsString());
         }

         for (String type : types) {
            DropConditionType conditionType = DropConditionType.byName(type);
            if (conditionType == null) {
               throw new JsonSyntaxException("Unknown type '" + type + "'");
            }

            this.types.add(conditionType);
         }
      }
   }

   @Override
   public JsonObject serialize(JsonSerializationContext ctx) {
      JsonObject json = new JsonObject();
      JsonArray array = new JsonArray();
      this.types.forEach(type -> array.add(new JsonPrimitive(type.name())));
      json.add("types", array);
      return json;
   }
}
