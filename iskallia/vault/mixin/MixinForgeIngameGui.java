package iskallia.vault.mixin;

import iskallia.vault.init.ModEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.ForgeIngameGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({ForgeIngameGui.class})
public class MixinForgeIngameGui {
   @Redirect(
      method = {"renderHealth"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/player/Player;hasEffect(Lnet/minecraft/world/effect/MobEffect;)Z",
         ordinal = 0
      )
   )
   public boolean hasEffectRedirect(Player player, MobEffect mobEffect) {
      return player.hasEffect(ModEffects.TOTEM_PLAYER_HEALTH) ? true : player.hasEffect(mobEffect);
   }
}
