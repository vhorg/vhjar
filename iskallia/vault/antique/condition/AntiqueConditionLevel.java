package iskallia.vault.antique.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

public class AntiqueConditionLevel extends AntiqueCondition {
   private int minLevel = -1;
   private int maxLevel = -1;

   public void setMinLevel(int minLevel) {
      this.minLevel = minLevel;
   }

   public void setMaxLevel(int maxLevel) {
      this.maxLevel = maxLevel;
   }

   @Override
   public boolean test(DropConditionContext context) {
      int level = context.getLevel();
      return (this.minLevel == -1 || level >= this.minLevel) && (this.maxLevel == -1 || level <= this.maxLevel);
   }

   @Override
   public void deserialize(JsonDeserializationContext ctx, JsonObject json) {
      this.minLevel = json.has("minLevel") ? json.get("minLevel").getAsInt() : -1;
      this.maxLevel = json.has("maxLevel") ? json.get("maxLevel").getAsInt() : -1;
   }

   @Override
   public JsonObject serialize(JsonSerializationContext ctx) {
      JsonObject json = new JsonObject();
      if (this.minLevel != -1) {
         json.addProperty("minLevel", this.minLevel);
      }

      if (this.maxLevel != -1) {
         json.addProperty("maxLevel", this.maxLevel);
      }

      return json;
   }
}
