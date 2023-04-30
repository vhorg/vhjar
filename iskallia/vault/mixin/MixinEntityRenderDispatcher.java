package iskallia.vault.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import iskallia.vault.client.render.FearIndicatorRenderer;
import iskallia.vault.client.render.GlacialShatterIndicatorRenderer;
import iskallia.vault.client.render.TauntCharmIndicatorRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EntityRenderDispatcher.class})
public abstract class MixinEntityRenderDispatcher {
   @Shadow
   public abstract Quaternion cameraOrientation();

   @Inject(
      method = {"render"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;render(Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
         shift = Shift.AFTER
      )}
   )
   private void renderTauntCharmIndicator(
      Entity entity,
      double worldX,
      double worldY,
      double worldZ,
      float entityYRot,
      float partialTicks,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      int light,
      CallbackInfo ci
   ) {
      TauntCharmIndicatorRenderer.render(entity, poseStack, bufferSource, this.cameraOrientation());
      FearIndicatorRenderer.render(entity, poseStack, bufferSource, this.cameraOrientation());
      GlacialShatterIndicatorRenderer.render(entity, poseStack, bufferSource, this.cameraOrientation());
   }
}
