package iskallia.vault.integration.mixin;

import iskallia.vault.integration.IntegrationMinimap;
import iskallia.vault.util.MiscUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
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
      Player player = Minecraft.getInstance().player;
      if (player != null && MiscUtils.getVault(player).isPresent()) {
         IntegrationMinimap.getMinimapSettings().ifPresent(settings -> {
            if (settings.caveZoom < 1) {
               settings.caveZoom = 1;
            }
         });
      }
   }
}
