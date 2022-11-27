package iskallia.vault.config;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModConfigs;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.resources.ResourceLocation;

public class ArchetypeDescriptionsConfig extends Config {
   private static final String NAME = "archetype_descriptions";
   @Expose
   private Map<ResourceLocation, JsonElement> descriptions;

   @Override
   public String getName() {
      return "archetype_descriptions";
   }

   @Override
   public <T extends Config> T readConfig() {
      ArchetypeDescriptionsConfig config = super.readConfig();

      for (JsonElement element : config.descriptions.values()) {
         ModConfigs.COLORS.replaceColorStrings(element);
      }

      return (T)config;
   }

   public MutableComponent getDescriptionFor(ResourceLocation archetypeId) {
      JsonElement element = this.descriptions.get(archetypeId);
      return element == null
         ? Serializer.fromJsonLenient(
            "[{text:'No description for ', color:'#192022'},{text: '" + archetypeId + "', color: '#fcf5c5'},{text: ', yet', color: '#192022'}]"
         )
         : Serializer.fromJson(element);
   }

   @Override
   protected void reset() {
      this.descriptions = new HashMap<>();
   }
}
