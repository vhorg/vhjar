package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.VaultBlizzardShard;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlizzardShardRenderer extends EntityRenderer<VaultBlizzardShard> {
   public static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/blizzard_shard.png");

   public BlizzardShardRenderer(Context context) {
      super(context);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull VaultBlizzardShard entity) {
      return TEXTURE_LOCATION;
   }

   @ParametersAreNonnullByDefault
   public void render(VaultBlizzardShard entity, float pEntityYaw, float partialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
      if (entity.tickCount >= 2) {
         pMatrixStack.pushPose();
         pMatrixStack.scale(2.0F, 2.0F, 2.0F);
         pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
         pMatrixStack.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));
         int i = 0;
         float f = 0.0F;
         float f1 = 0.5F;
         float f2 = 0.0F;
         float f3 = 0.15625F;
         float f4 = 0.0F;
         float f5 = 0.15625F;
         float f6 = 0.15625F;
         float f7 = 0.3125F;
         float f8 = 0.05625F;
         float f9 = entity.shakeTime - partialTicks;
         if (f9 > 0.0F) {
            float f10 = -Mth.sin(f9 * 3.0F) * f9;
            pMatrixStack.mulPose(Vector3f.ZP.rotationDegrees(f10));
         }

         pMatrixStack.mulPose(Vector3f.XP.rotationDegrees(45.0F));
         pMatrixStack.scale(0.05625F, 0.05625F, 0.05625F);
         pMatrixStack.translate(-4.0, 0.0, 0.0);
         VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.entityCutout(this.getTextureLocation(entity)));
         Pose posestack$pose = pMatrixStack.last();
         Matrix4f matrix4f = posestack$pose.pose();
         Matrix3f matrix3f = posestack$pose.normal();
         this.vertex(matrix4f, matrix3f, vertexconsumer, -7, -2, -2, 0.0F, 0.15625F, -1, 0, 0, pPackedLight);
         this.vertex(matrix4f, matrix3f, vertexconsumer, -7, -2, 2, 0.15625F, 0.15625F, -1, 0, 0, pPackedLight);
         this.vertex(matrix4f, matrix3f, vertexconsumer, -7, 2, 2, 0.15625F, 0.3125F, -1, 0, 0, pPackedLight);
         this.vertex(matrix4f, matrix3f, vertexconsumer, -7, 2, -2, 0.0F, 0.3125F, -1, 0, 0, pPackedLight);
         this.vertex(matrix4f, matrix3f, vertexconsumer, -7, 2, -2, 0.0F, 0.15625F, 1, 0, 0, pPackedLight);
         this.vertex(matrix4f, matrix3f, vertexconsumer, -7, 2, 2, 0.15625F, 0.15625F, 1, 0, 0, pPackedLight);
         this.vertex(matrix4f, matrix3f, vertexconsumer, -7, -2, 2, 0.15625F, 0.3125F, 1, 0, 0, pPackedLight);
         this.vertex(matrix4f, matrix3f, vertexconsumer, -7, -2, -2, 0.0F, 0.3125F, 1, 0, 0, pPackedLight);

         for (int j = 0; j < 4; j++) {
            pMatrixStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
            this.vertex(matrix4f, matrix3f, vertexconsumer, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, pPackedLight);
            this.vertex(matrix4f, matrix3f, vertexconsumer, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, pPackedLight);
            this.vertex(matrix4f, matrix3f, vertexconsumer, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, pPackedLight);
            this.vertex(matrix4f, matrix3f, vertexconsumer, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, pPackedLight);
         }

         pMatrixStack.popPose();
         super.render(entity, pEntityYaw, partialTicks, pMatrixStack, pBuffer, pPackedLight);
      }
   }

   public void vertex(
      Matrix4f p_113826_,
      Matrix3f p_113827_,
      VertexConsumer p_113828_,
      int p_113829_,
      int p_113830_,
      int p_113831_,
      float p_113832_,
      float p_113833_,
      int p_113834_,
      int p_113835_,
      int p_113836_,
      int p_113837_
   ) {
      p_113828_.vertex(p_113826_, p_113829_, p_113830_, p_113831_)
         .color(255, 255, 255, 255)
         .uv(p_113832_, p_113833_)
         .overlayCoords(OverlayTexture.NO_OVERLAY)
         .uv2(p_113837_)
         .normal(p_113827_, p_113834_, p_113836_, p_113835_)
         .endVertex();
   }
}
