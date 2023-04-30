package iskallia.vault.integration.mixin;

import iskallia.vault.core.vault.ClientVaults;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.map.controls.ControlsHandler;

@Mixin({ControlsHandler.class})
public class MixinWorldMapControlHandler {
   @Inject(
      method = {"keyDown"},
      at = {@At("HEAD")},
      cancellable = true,
      remap = false
   )
   public void exitIfInVault(KeyMapping kb, boolean tickEnd, boolean isRepeat, CallbackInfo ci) {
      if (ClientVaults.getActive().isPresent()) {
         ci.cancel();
      }
   }
}
