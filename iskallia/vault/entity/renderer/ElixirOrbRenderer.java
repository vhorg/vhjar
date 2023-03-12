package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.ElixirOrbEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ElixirOrbRenderer extends EntityRenderer<ElixirOrbEntity> {
   private static final ResourceLocation EXPERIENCE_ORB_LOCATION = VaultMod.id("textures/entity/elixir_orb.png");
   private static final RenderType RENDER_TYPE = RenderType.itemEntityTranslucentCull(EXPERIENCE_ORB_LOCATION);

   public ElixirOrbRenderer(Context p_174110_) {
      super(p_174110_);
      this.shadowRadius = 0.15F;
      this.shadowStrength = 0.75F;
   }

   protected int getBlockLightLevel(ElixirOrbEntity pEntity, BlockPos pPos) {
      return Mth.clamp(super.getBlockLightLevel(pEntity, pPos) + 7, 0, 15);
   }

   public void render(ElixirOrbEntity orb, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
      pMatrixStack.pushPose();
      int i = Math.abs(orb.getSize());
      float f = (i % 4 * 16 + 0) / 64.0F;
      float f1 = (i % 4 * 16 + 16) / 64.0F;
      float f2 = (i / 4 * 16 + 0) / 64.0F;
      float f3 = (i / 4 * 16 + 16) / 64.0F;
      float f4 = 1.0F;
      float f5 = 0.5F;
      float f6 = 0.25F;
      float f7 = 255.0F;
      float f8 = (orb.tickCount + pPartialTicks) / 2.0F;
      int j = (int)((Mth.sin(f8 + 0.0F) + 1.0F) * 0.5F * 255.0F);
      int k = 255;
      int l = (int)((Mth.sin(f8 + (float) (Math.PI * 4.0 / 3.0)) + 1.0F) * 0.1F * 255.0F);
      short var25;
      byte var26;
      short var27;
      if (orb.getSize() >= 0) {
         var25 = 194;
         var26 = 19;
         var27 = 231;
      } else {
         var25 = 255;
         var26 = 0;
         var27 = 0;
      }

      pMatrixStack.translate(0.0, 0.1F, 0.0);
      pMatrixStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
      pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
      float f9 = 0.3F;
      pMatrixStack.scale(0.3F, 0.3F, 0.3F);
      VertexConsumer vertexconsumer = pBuffer.getBuffer(RENDER_TYPE);
      Pose posestack$pose = pMatrixStack.last();
      Matrix4f matrix4f = posestack$pose.pose();
      Matrix3f matrix3f = posestack$pose.normal();
      vertex(vertexconsumer, matrix4f, matrix3f, -0.5F, -0.25F, var25, var26, var27, f, f3, pPackedLight, Math.min(orb.getAge() * 6, 128));
      vertex(vertexconsumer, matrix4f, matrix3f, 0.5F, -0.25F, var25, var26, var27, f1, f3, pPackedLight, Math.min(orb.getAge() * 6, 128));
      vertex(vertexconsumer, matrix4f, matrix3f, 0.5F, 0.75F, var25, var26, var27, f1, f2, pPackedLight, Math.min(orb.getAge() * 6, 128));
      vertex(vertexconsumer, matrix4f, matrix3f, -0.5F, 0.75F, var25, var26, var27, f, f2, pPackedLight, Math.min(orb.getAge() * 6, 128));
      pMatrixStack.popPose();
      super.render(orb, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
   }

   private static void vertex(
      VertexConsumer pBuffer,
      Matrix4f pMatrix,
      Matrix3f pMatrixNormal,
      float pX,
      float pY,
      int pRed,
      int pGreen,
      int pBlue,
      float pTexU,
      float pTexV,
      int pPackedLight,
      int alpha
   ) {
      pBuffer.vertex(pMatrix, pX, pY, 0.0F)
         .color(pRed, pGreen, pBlue, alpha)
         .uv(pTexU, pTexV)
         .overlayCoords(OverlayTexture.NO_OVERLAY)
         .uv2(pPackedLight)
         .normal(pMatrixNormal, 0.0F, 1.0F, 0.0F)
         .endVertex();
   }

   public ResourceLocation getTextureLocation(ElixirOrbEntity pEntity) {
      return EXPERIENCE_ORB_LOCATION;
   }
}
