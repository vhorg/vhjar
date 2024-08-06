package iskallia.vault.antique.condition;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public class AntiqueConditionKey extends AntiqueCondition {
   private final List<ResourceLocation> keys = new ArrayList<>();

   @Override
   public boolean test(DropConditionContext context) {
      return this.keys.contains(context.getOwnerKey());
   }

   @Override
   public void deserialize(JsonDeserializationContext ctx, JsonObject json) {
      if (!json.has("key")) {
         throw new JsonSyntaxException("Missing key, expected to find a string or array");
      } else {
         JsonElement typeElement = json.get("key");
         if (typeElement.isJsonArray()) {
            for (JsonElement element : typeElement.getAsJsonArray()) {
               if (!element.isJsonPrimitive()) {
                  throw new JsonSyntaxException("Expected type to be a string, was " + element);
               }

               ResourceLocation key = ResourceLocation.tryParse(element.getAsString());
               if (key == null) {
                  throw new JsonSyntaxException("Expected type to be a valid ResourceLocation, was " + element);
               }

               this.keys.add(key);
            }
         } else {
            if (!typeElement.isJsonPrimitive()) {
               throw new JsonSyntaxException("Expected type to be a string or an array of strings, was " + typeElement);
            }

            ResourceLocation key = ResourceLocation.tryParse(typeElement.getAsString());
            if (key == null) {
               throw new JsonSyntaxException("Expected type to be a valid ResourceLocation, was " + typeElement);
            }

            this.keys.add(key);
         }
      }
   }

   @Override
   public JsonObject serialize(JsonSerializationContext ctx) {
      JsonObject json = new JsonObject();
      JsonArray array = new JsonArray();
      this.keys.forEach(key -> array.add(key.toString()));
      json.add("key", array);
      return json;
   }
}
