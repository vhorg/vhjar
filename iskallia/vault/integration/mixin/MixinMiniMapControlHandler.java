package iskallia.vault.integration.mixin;

import iskallia.vault.util.MiscUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.controls.ControlsHandler;
import xaero.common.settings.ModSettings;

@Mixin({ControlsHandler.class})
public class MixinMiniMapControlHandler {
   @Inject(
      method = {"keyDown"},
      at = {@At("HEAD")},
      cancellable = true,
      remap = false
   )
   public void exitIfInVault(KeyMapping kb, boolean tickEnd, boolean isRepeat, CallbackInfo ci) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null
         && MiscUtils.getVault(player).isPresent()
         && (kb == ModSettings.keyBindZoom || kb == ModSettings.keyBindZoom1 || kb == ModSettings.keyLargeMap)) {
         ci.cancel();
      }
   }
}
