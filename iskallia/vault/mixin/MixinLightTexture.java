package iskallia.vault.mixin;

import iskallia.vault.core.event.ClientEvents;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LightTexture.class})
public class MixinLightTexture {
   @Inject(
      method = {"getBrightness"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getBrightness(Level world, int lightLevel, CallbackInfoReturnable<Float> ci) {
      ci.setReturnValue(ClientEvents.AMBIENT_LIGHT.invoke(world, lightLevel, world.dimensionType().brightness(lightLevel)).getBrightness());
   }
}