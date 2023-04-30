package iskallia.vault.integration.mixin;

import iskallia.vault.core.vault.ClientVaults;
import iskallia.vault.integration.IntegrationMinimap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.common.settings.ModSettings;

@Mixin({ModSettings.class})
public class MixinModSettings {
   @Inject(
      method = {"getShowCoords"},
      at = {@At("HEAD")},
      remap = false,
      cancellable = true
   )
   public void doesShowCoordinates(CallbackInfoReturnable<Boolean> cir) {
      if (ClientVaults.getActive().isPresent()) {
         cir.setReturnValue(false);
      }
   }

   @Inject(
      method = {"getMinimapSize"},
      at = {@At("RETURN")},
      remap = false,
      cancellable = true
   )
   public void preventOverSized(CallbackInfoReturnable<Integer> cir) {
      if (ClientVaults.getActive().isPresent()) {
         IntegrationMinimap.getMinimapSettings().ifPresent(settings -> {
            int defaultSize = IntegrationMinimap.getDefaultMinimapSize(settings);
            if (cir.getReturnValueI() > defaultSize) {
               cir.setReturnValue(defaultSize);
            }
         });
      }
   }
}
