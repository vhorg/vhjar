package iskallia.vault.mixin;

import iskallia.vault.world.data.PlayerTitlesData;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({PlayerTabOverlay.class})
public class MixinPlayerTabOverlay {
   @Redirect(
      method = {"getNameForDisplay"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/multiplayer/PlayerInfo;getTabListDisplayName()Lnet/minecraft/network/chat/Component;"
      )
   )
   public Component getNameForDisplay(PlayerInfo info) {
      MutableComponent name = PlayerTitlesData.getCustomName(
            info.getProfile().getId(), new TextComponent(info.getProfile().getName()), PlayerTitlesData.Type.TAB_LIST, true
         )
         .orElse(null);
      return (Component)(name != null ? name : info.getTabListDisplayName());
   }
}
