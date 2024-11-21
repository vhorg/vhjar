package iskallia.vault.mixin;

import iskallia.vault.init.ModConfigs;
import java.util.List;
import java.util.Map;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({ClientLanguage.class})
public class MixinClientLanguage {
   @Inject(
      method = {"loadFrom"},
      at = {@At(
         value = "INVOKE_ASSIGN",
         target = "Lcom/google/common/collect/Maps;newHashMap()Ljava/util/HashMap;"
      )},
      locals = LocalCapture.CAPTURE_FAILHARD
   )
   private static void injectConfigTranslations(
      ResourceManager pResourceManager, List<LanguageInfo> pLanguageInfo, CallbackInfoReturnable<ClientLanguage> cir, Map langMap
   ) {
      if (ModConfigs.TRANSLATIONS != null) {
         langMap.putAll(ModConfigs.TRANSLATIONS.getTranslations());
      }
   }
}
