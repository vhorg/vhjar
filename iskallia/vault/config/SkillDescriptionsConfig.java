package iskallia.vault.config;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModConfigs;
import java.util.HashMap;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component.Serializer;

public class SkillDescriptionsConfig extends Config {
   @Expose
   private HashMap<String, JsonElement> descriptions;

   @Override
   public String getName() {
      return "skill_descriptions";
   }

   @Override
   public <T extends Config> T readConfig() {
      SkillDescriptionsConfig config = super.readConfig();

      for (JsonElement element : config.descriptions.values()) {
         ModConfigs.COLORS.replaceColorStrings(element);
      }

      return (T)config;
   }

   public MutableComponent getDescriptionFor(String skillName) {
      JsonElement element = this.descriptions.get(skillName);
      return element == null
         ? Serializer.fromJsonLenient(
            "[{text:'No description for ', color:'#192022'},{text: '" + skillName + "', color: '#fcf5c5'},{text: ', yet', color: '#192022'}]"
         )
         : Serializer.fromJson(element);
   }

   @Override
   protected void reset() {
      this.descriptions = new HashMap<>();
   }
}
