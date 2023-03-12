package iskallia.vault.integration.mixin;

import iskallia.vault.integration.IntegrationMinimap;
import iskallia.vault.util.MiscUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
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
      Player player = Minecraft.getInstance().player;
      if (player != null && MiscUtils.getVault(player).isPresent()) {
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
      Player player = Minecraft.getInstance().player;
      if (player != null && MiscUtils.getVault(player).isPresent()) {
         IntegrationMinimap.getMinimapSettings().ifPresent(settings -> {
            int defaultSize = IntegrationMinimap.getDefaultMinimapSize(settings);
            if (cir.getReturnValueI() > defaultSize) {
               cir.setReturnValue(defaultSize);
            }
         });
      }
   }
}
