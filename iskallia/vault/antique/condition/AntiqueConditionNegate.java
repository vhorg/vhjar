package iskallia.vault.antique.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Collections;
import java.util.List;
import net.minecraft.util.GsonHelper;

public class AntiqueConditionNegate extends AntiqueCondition {
   private AntiqueCondition condition = null;

   @Override
   public List<? extends AntiqueCondition> getChildConditions() {
      return Collections.singletonList(this.condition);
   }

   @Override
   public boolean test(DropConditionContext context) {
      return this.condition != null && !this.condition.test(context);
   }

   @Override
   public void deserialize(JsonDeserializationContext ctx, JsonObject json) {
      JsonObject conditionObject = GsonHelper.getAsJsonObject(json, "condition");
      this.condition = (AntiqueCondition)ctx.deserialize(conditionObject, AntiqueCondition.class);
   }

   @Override
   public JsonObject serialize(JsonSerializationContext ctx) {
      JsonObject json = new JsonObject();
      json.add("condition", ctx.serialize(this.condition));
      return json;
   }
}
