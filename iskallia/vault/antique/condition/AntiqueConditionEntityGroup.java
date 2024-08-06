package iskallia.vault.antique.condition;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import iskallia.vault.init.ModConfigs;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class AntiqueConditionEntityGroup extends AntiqueCondition {
   private final List<ResourceLocation> groups = new ArrayList<>();

   @Override
   public boolean test(DropConditionContext context) {
      if (context instanceof DropEntityConditionContext entityContext) {
         LivingEntity entity = entityContext.getEntity();

         for (ResourceLocation group : this.groups) {
            if (!ModConfigs.ENTITY_GROUPS.isInGroup(group, entity)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void deserialize(JsonDeserializationContext ctx, JsonObject json) {
      if (!json.has("group")) {
         throw new JsonSyntaxException("Missing group, expected to find a string or array");
      } else {
         JsonElement typeElement = json.get("group");
         List<String> groups = new ArrayList<>();
         if (typeElement.isJsonArray()) {
            for (JsonElement element : typeElement.getAsJsonArray()) {
               if (!element.isJsonPrimitive()) {
                  throw new JsonSyntaxException("Expected group to be a string, was " + element);
               }

               groups.add(element.getAsString());
            }
         } else {
            if (!typeElement.isJsonPrimitive()) {
               throw new JsonSyntaxException("Expected group to be a string or an array of strings, was " + typeElement);
            }

            groups.add(typeElement.getAsString());
         }

         for (String group : groups) {
            ResourceLocation groupKey = ResourceLocation.tryParse(group);
            if (groupKey == null) {
               throw new JsonSyntaxException("Invalid resource location '" + group + "'");
            }

            this.groups.add(groupKey);
         }
      }
   }

   @Override
   public JsonObject serialize(JsonSerializationContext ctx) {
      JsonObject json = new JsonObject();
      JsonArray array = new JsonArray();
      this.groups.forEach(key -> array.add(new JsonPrimitive(key.toString())));
      json.add("group", array);
      return json;
   }
}
