package iskallia.vault.antique.condition;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.GsonHelper;

public class AntiqueConditionOr extends AntiqueCondition {
   private final List<AntiqueCondition> conditions = new ArrayList<>();

   public void addCondition(AntiqueCondition condition) {
      this.conditions.add(condition);
   }

   @Override
   public boolean test(DropConditionContext context) {
      return this.conditions.stream().anyMatch(condition -> condition.test(context));
   }

   @Override
   public void deserialize(JsonDeserializationContext ctx, JsonObject json) {
      JsonArray array = GsonHelper.getAsJsonArray(json, "conditions");
      array.forEach(element -> {
         JsonObject conditionJson = element.getAsJsonObject();
         AntiqueCondition condition = (AntiqueCondition)ctx.deserialize(conditionJson, AntiqueCondition.class);
         this.conditions.add(condition);
      });
   }

   @Override
   public JsonObject serialize(JsonSerializationContext ctx) {
      JsonObject json = new JsonObject();
      JsonArray array = new JsonArray();
      this.conditions.forEach(condition -> array.add(ctx.serialize(condition)));
      json.add("conditions", array);
      return json;
   }
}
