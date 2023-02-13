package iskallia.vault.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.core.event.ClientEvents;
import iskallia.vault.core.event.client.FogColorsEvent;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({FogRenderer.class})
public class MixinFogRenderer {
   @Shadow
   private static float fogRed;
   @Shadow
   private static float fogGreen;
   @Shadow
   private static float fogBlue;

   @Inject(
      method = {"setupColor"},
      at = {@At("RETURN")}
   )
   private static void adjustFogColor(Camera camera, float pTicks, ClientLevel level, int renderDistanceChunks, float bossColorModifier, CallbackInfo ci) {
      FogColorsEvent.Data data = ClientEvents.FOG_COLORS.invoke(fogRed, fogGreen, fogBlue);
      fogRed = data.getRed();
      fogGreen = data.getGreen();
      fogBlue = data.getBlue();
      RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0.0F);
   }
}
