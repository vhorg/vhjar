package iskallia.vault.config;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import java.util.HashMap;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent.Serializer;

public class SkillDescriptionsConfig extends Config {
   @Expose
   private HashMap<String, JsonElement> descriptions;

   @Override
   public String getName() {
      return "skill_descriptions";
   }

   public IFormattableTextComponent getDescriptionFor(String skillName) {
      JsonElement element = this.descriptions.get(skillName);
      return element == null
         ? Serializer.func_240644_b_(
            "[{text:'No description for ', color:'#192022'},{text: '" + skillName + "', color: '#fcf5c5'},{text: ', yet', color: '#192022'}]"
         )
         : Serializer.func_240641_a_(element);
   }

   @Override
   protected void reset() {
      this.descriptions = new HashMap<>();
   }
}
