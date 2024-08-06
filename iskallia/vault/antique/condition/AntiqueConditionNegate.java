package iskallia.vault.antique.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

public class AntiqueConditionNegate extends AntiqueCondition {
   private AntiqueCondition condition = null;

   @Override
   public boolean test(DropConditionContext context) {
      return this.condition != null && !this.condition.test(context);
   }

   @Override
   public void deserialize(JsonDeserializationContext ctx, JsonObject json) {
      this.condition = (AntiqueCondition)ctx.deserialize(json, AntiqueCondition.class);
   }

   @Override
   public JsonObject serialize(JsonSerializationContext ctx) {
      JsonObject json = new JsonObject();
      json.add("condition", ctx.serialize(this.condition));
      return json;
   }
}
