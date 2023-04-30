package iskallia.vault.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class TauntCharmIndicatorRenderer {
   public static void render(Entity entity, PoseStack poseStack, MultiBufferSource bufferSource, Quaternion cameraOrientation) {
      if (entity instanceof LivingEntity livingEntity && livingEntity.hasEffect(ModEffects.TAUNT_CHARM)) {
         poseStack.pushPose();
         poseStack.translate(0.0, entity.getBbHeight() + 0.5, 0.0);
         poseStack.mulPose(cameraOrientation);
         poseStack.scale(-0.025F, -0.025F, 0.025F);
         VertexConsumer buffer = bufferSource.getBuffer(ModRenderTypes.TAUNT_CHARM_INDICATOR);
         Matrix4f matrix = poseStack.last().pose();
         float size = 10.0F;
         buffer.vertex(matrix, -size, -size, 0.01F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(0.0F, 0.0F).uv2(15728880).endVertex();
         buffer.vertex(matrix, -size, size, 0.01F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(0.0F, 0.5625F).uv2(15728880).endVertex();
         buffer.vertex(matrix, size, size, 0.01F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(0.5625F, 0.5625F).uv2(15728880).endVertex();
         buffer.vertex(matrix, size, -size, 0.01F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(0.5625F, 0.0F).uv2(15728880).endVertex();
         poseStack.popPose();
      }
   }
}
