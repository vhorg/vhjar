package iskallia.vault.mixin;

import iskallia.vault.gear.trinket.TrinketHelper;
import iskallia.vault.gear.trinket.effects.WingsTrinket;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
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

   @Redirect(
      method = {"aiStep"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/ItemStack;canElytraFly(Lnet/minecraft/world/entity/LivingEntity;)Z"
      )
   )
   protected boolean canElytraFlyOvr(ItemStack instance, LivingEntity livingEntity) {
      return livingEntity instanceof Player player && player.getCooldowns().isOnCooldown(instance.getItem()) ? false : instance.canElytraFly(livingEntity);
   }
}
