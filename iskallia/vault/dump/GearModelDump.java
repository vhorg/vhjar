package iskallia.vault.dump;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class GearModelDump extends JsonDump {
   @Override
   public String fileName() {
      return "gear_models.json";
   }

   @Override
   public JsonObject dumpToJSON() {
      JsonObject jsonObject = new JsonObject();
      JsonArray regularModels = new JsonArray();
      JsonArray specialHeadModels = new JsonArray();
      JsonArray specialChestModels = new JsonArray();
      JsonArray specialLegsModels = new JsonArray();
      JsonArray specialFeetModels = new JsonArray();
      jsonObject.add("regularModels", regularModels);
      jsonObject.add("specialHeadModels", specialHeadModels);
      jsonObject.add("specialChestModels", specialChestModels);
      jsonObject.add("specialLegsModels", specialLegsModels);
      jsonObject.add("specialFeetModels", specialFeetModels);
      return jsonObject;
   }
}
