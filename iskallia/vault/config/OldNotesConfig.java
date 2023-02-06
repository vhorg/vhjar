package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.util.ReservoirSampleHelper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.resources.ResourceLocation;

public class OldNotesConfig extends Config {
   public static final String DEFAULT_LOCALE_CODE = "en_us";
   @Expose
   private final Map<ResourceLocation, Map<String, String>> hints = new HashMap<>();

   @Override
   public String getName() {
      return "old_notes";
   }

   public ResourceLocation getRandomHint(RandomSource random) {
      List<ResourceLocation> samples = ReservoirSampleHelper.sample(this.hints.keySet());
      if (samples.size() == 0) {
         throw new IllegalStateException();
      } else {
         return samples.get(0);
      }
   }

   public MutableComponent getHint(String localeCode, ResourceLocation hintId) {
      Map<String, String> hintByLocales = this.hints.get(hintId);
      String textRaw = hintByLocales.getOrDefault(localeCode, hintByLocales.getOrDefault("en_us", hintId.toString()));
      return Serializer.fromJsonLenient(textRaw);
   }

   @Override
   protected void reset() {
      this.hints.clear();
      Map<String, String> exampleHint = new HashMap<>();
      exampleHint.put(
         "en_us",
         "[\"\",{\"text\":\"This is an \",\"italic\":true},{\"text\":\"example\",\"italic\":true,\"color\":\"aqua\"},{\"text\":\" Hint.\",\"italic\":true}]"
      );
      exampleHint.put(
         "tr_tr",
         "[\"\",{\"text\":\"Bu bir \",\"italic\":true},{\"text\":\"örnek \",\"italic\":true,\"color\":\"aqua\"},{\"text\":\"\\u0130pucu'dur.\",\"italic\":true}]"
      );
      this.hints.put(VaultMod.id("hint.example"), exampleHint);
   }
}
