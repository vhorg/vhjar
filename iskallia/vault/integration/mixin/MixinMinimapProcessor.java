package iskallia.vault.integration.mixin;

import iskallia.vault.core.vault.ClientVaults;
import iskallia.vault.integration.IntegrationMinimap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.common.minimap.MinimapProcessor;

@Mixin({MinimapProcessor.class})
public class MixinMinimapProcessor {
   @Inject(
      method = {"getTargetZoom"},
      at = {@At("HEAD")},
      remap = false
   )
   public void preventCaveZoom(CallbackInfoReturnable<Double> cir) {
      if (ClientVaults.getActive().isPresent()) {
         IntegrationMinimap.getMinimapSettings().ifPresent(settings -> {
            if (settings.caveZoom < 1) {
               settings.caveZoom = 1;
            }
         });
      }
   }
}
