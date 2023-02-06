package iskallia.vault.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.render.IVaultOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ScreenEffectRenderer.class})
public class MixinScreenEffectRenderer {
   @Inject(
      method = {"renderFire"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V"
      )}
   )
   private static void translateFireDown(Minecraft minecraft, PoseStack pPoseStack, CallbackInfo ci) {
      if (!((IVaultOptions)minecraft.options).doVanillaPotionDamageEffects()) {
         pPoseStack.translate(0.0, -0.2F, 0.0);
      }
   }
}
