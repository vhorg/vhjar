package iskallia.vault.mixin;

import iskallia.vault.util.VHSmpUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.spectator.PlayerMenuItem;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({PlayerMenuItem.class})
public class MixinPlayerMenuItem {
   @Inject(
      method = {"selectItem"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onSelectTeleport(SpectatorMenu pMenu, CallbackInfo ci) {
      Player player = Minecraft.getInstance().player;
      if (VHSmpUtil.isArenaWorld(player)) {
         ci.cancel();
      }
   }
}
