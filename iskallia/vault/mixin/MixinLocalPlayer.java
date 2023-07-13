package iskallia.vault.mixin;

import iskallia.vault.gear.trinket.TrinketHelper;
import iskallia.vault.gear.trinket.effects.WingsTrinket;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LocalPlayer.class})
public abstract class MixinLocalPlayer {
   @Inject(
      method = {"aiStep"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/player/LocalPlayer;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"
      )}
   )
   protected void elytraTrinket(CallbackInfo ci) {
      LocalPlayer player = (LocalPlayer)this;
      TrinketHelper.getTrinkets(player, WingsTrinket.class).forEach(wings -> {
         if (wings.isUsable(player)) {
            if (player.tryToStartFallFlying()) {
               player.connection.send(new ServerboundPlayerCommandPacket(player, Action.START_FALL_FLYING));
            }
         }
      });
   }
}
