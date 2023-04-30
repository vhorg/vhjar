package iskallia.vault.mixin;

import iskallia.vault.init.ModEffects;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({Gui.class})
public abstract class MixinGui {
   @Redirect(
      method = {"renderEffects"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/effect/MobEffectInstance;getDuration()I",
         ordinal = 0
      )
   )
   public int getDurationRedirect(MobEffectInstance mobEffectInstance) {
      return ModEffects.PREVENT_DURATION_FLASH.contains(mobEffectInstance.getEffect()) ? 32767 : mobEffectInstance.getDuration();
   }
}
