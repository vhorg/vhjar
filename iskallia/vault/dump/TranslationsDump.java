package iskallia.vault.dump;

import com.google.gson.JsonObject;
import net.minecraft.locale.Language;

public class TranslationsDump extends JsonDump {
   @Override
   public String fileName() {
      return "translations.json";
   }

   @Override
   public JsonObject dumpToJSON() {
      Language languageMap = Language.getInstance();
      JsonObject jsonObject = new JsonObject();
      languageMap.getLanguageData().forEach((key, value) -> {
         if (key.startsWith("block.") || key.startsWith("item.")) {
            jsonObject.addProperty(key, value);
         }
      });
      return jsonObject;
   }
}
