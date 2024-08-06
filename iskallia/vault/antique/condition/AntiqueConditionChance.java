package iskallia.vault.antique.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;

public class AntiqueConditionChance extends AntiqueCondition {
   private static final Random rand = new Random();
   private float chance = 0.0F;

   @Override
   public boolean test(DropConditionContext context) {
      return rand.nextFloat() < this.chance;
   }

   @Override
   public void deserialize(JsonDeserializationContext ctx, JsonObject json) {
      this.chance = json.get("chance").getAsFloat();
   }

   @Override
   public JsonObject serialize(JsonSerializationContext ctx) {
      JsonObject json = new JsonObject();
      json.addProperty("chance", this.chance);
      return json;
   }
}
