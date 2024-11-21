package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TranslationsConfig extends Config {
   @Expose
   private final Map<String, String> translations = new HashMap<>();

   public Map<String, String> getTranslations() {
      return Collections.unmodifiableMap(this.translations);
   }

   @Override
   public String getName() {
      return "translations";
   }

   @Override
   protected void reset() {
      this.translations.clear();
      this.translations.put("the_vault.gear_attribute.effect_avoidance.avoidance", "%s Avoidance");
      this.translations.put("the_vault.gear_attribute.effect_avoidance.avoidance.bad_effects", "Effect");
   }
}
