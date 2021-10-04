package iskallia.vault.dump;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import iskallia.vault.init.ModModels;
import java.util.Map;
import java.util.Map.Entry;

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
      putRegularModels(regularModels, ModModels.GearModel.REGISTRY, false);
      putRegularModels(regularModels, ModModels.GearModel.SCRAPPY_REGISTRY, true);
      putSpecialModels(specialHeadModels, ModModels.SpecialGearModel.HEAD_REGISTRY);
      putSpecialModels(specialChestModels, ModModels.SpecialGearModel.CHESTPLATE_REGISTRY);
      putSpecialModels(specialLegsModels, ModModels.SpecialGearModel.LEGGINGS_REGISTRY);
      putSpecialModels(specialFeetModels, ModModels.SpecialGearModel.BOOTS_REGISTRY);
      jsonObject.add("regularModels", regularModels);
      jsonObject.add("specialHeadModels", specialHeadModels);
      jsonObject.add("specialChestModels", specialChestModels);
      jsonObject.add("specialLegsModels", specialLegsModels);
      jsonObject.add("specialFeetModels", specialFeetModels);
      return jsonObject;
   }

   private static void putRegularModels(JsonArray array, Map<Integer, ModModels.GearModel> registry, boolean isScrappy) {
      for (Entry<Integer, ModModels.GearModel> entry : registry.entrySet()) {
         Integer modelIndex = entry.getKey();
         ModModels.GearModel model = entry.getValue();
         String modelId = model.getDisplayName().toLowerCase().replace(" ", "_");
         JsonObject modelJson = new JsonObject();
         modelJson.addProperty("modelId", modelId);
         modelJson.addProperty("modelIndex", modelIndex);
         modelJson.addProperty("name", model.getDisplayName());
         modelJson.addProperty("scrappy", isScrappy);
         array.add(modelJson);
      }
   }

   private static void putSpecialModels(JsonArray array, Map<Integer, ModModels.SpecialGearModel> registry) {
      for (Entry<Integer, ModModels.SpecialGearModel> entry : registry.entrySet()) {
         Integer modelIndex = entry.getKey();
         ModModels.SpecialGearModel model = entry.getValue();
         String modelId = model.getDisplayName().toLowerCase().replace(" ", "_");
         JsonObject modelJson = new JsonObject();
         modelJson.addProperty("modelId", modelId);
         modelJson.addProperty("modelIndex", modelIndex);
         modelJson.addProperty("name", model.getDisplayName());
         array.add(modelJson);
      }
   }
}
