package iskallia.vault.entity.renderer.eyesore;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.eyesore.EyesoreEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.eyesore.EyesoreModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class EyesoreRenderer extends MobRenderer<EyesoreEntity, EyesoreModel> {
   public static final ResourceLocation DEFAULT_TEXTURE = VaultMod.id("textures/entity/eyesore/default.png");
   public static final ResourceLocation SORE_EYE_TEXTURE = VaultMod.id("textures/entity/eyesore/sore_eye.png");
   private static final ResourceLocation GUARDIAN_BEAM_TEXTURE = VaultMod.id("textures/entity/eyesore/laser_beam.png");
   private static final RenderType BEAM_RENDER_TYPE = RenderType.entityCutoutNoCull(GUARDIAN_BEAM_TEXTURE);

   public EyesoreRenderer(Context context) {
      super(context, new EyesoreModel(context.bakeLayer(ModModelLayers.EYESORE)), 0.5F);
   }

   protected void scale(@Nonnull EyesoreEntity entity, @Nonnull PoseStack matrixStack, float partialTickTime) {
      float f = 4.5F;
      matrixStack.scale(f, f, f);
   }

   protected void renderNameTag(EyesoreEntity entityIn, Component displayNameIn, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
   }

   protected boolean shouldShowName(EyesoreEntity entity) {
      return false;
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull EyesoreEntity entity) {
      int targetId = (Integer)entity.getEntityData().get(EyesoreEntity.LASER_TARGET);
      if (entity.level.getEntity(targetId) != null) {
         return SORE_EYE_TEXTURE;
      } else {
         return entity.getState() == EyesoreEntity.State.GIVING_BIRTH ? SORE_EYE_TEXTURE : DEFAULT_TEXTURE;
      }
   }

   public void render(EyesoreEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLightIn) {
      super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLightIn);
      int targetId = (Integer)entity.getEntityData().get(EyesoreEntity.LASER_TARGET);
      Entity target = entity.getCommandSenderWorld().getEntity(targetId);
      LivingEntity livingentity = target instanceof LivingEntity ? (LivingEntity)target : null;
      if (livingentity != null) {
         float f = this.getAttackAnimationScale(entity, partialTicks);
         float f1 = (float)entity.level.getGameTime() + partialTicks;
         float f2 = f1 * 0.5F % 1.0F;
         float f3 = entity.getEyeHeight();
         matrixStack.pushPose();
         matrixStack.translate(0.0, f3, 0.0);
         Vec3 vector3d = this.getPosition(livingentity, livingentity.getBbHeight() * 0.5, partialTicks);
         Vec3 vector3d1 = this.getPosition(entity, f3, partialTicks);
         Vec3 eyePos1 = entity.getEyePosition(1.0F);
         ClipContext context = new ClipContext(eyePos1, vector3d, Block.COLLIDER, Fluid.NONE, entity);
         BlockHitResult result = entity.level.clip(context);
         if (result.getType() != Type.MISS) {
            vector3d = result.getLocation();
         }

         Vec3 vector3d2 = vector3d.subtract(eyePos1);
         float f4 = (float)(vector3d2.length() + 1.0);
         vector3d2 = vector3d2.normalize();
         float f5 = (float)Math.acos(vector3d2.y);
         float f6 = (float)Math.atan2(vector3d2.z, vector3d2.x);
         matrixStack.mulPose(Vector3f.YP.rotationDegrees(((float) (Math.PI / 2) - f6) * (180.0F / (float)Math.PI)));
         matrixStack.mulPose(Vector3f.XP.rotationDegrees(f5 * (180.0F / (float)Math.PI)));
         int i = 1;
         float f7 = f1 * 0.05F * -1.5F;
         float f8 = f * f;
         int j = 190;
         int k = 0;
         int l = 0;
         float f9 = 0.2F;
         float f10 = 0.282F;
         float f11 = Mth.cos(f7 + (float) (Math.PI * 3.0 / 4.0)) * 0.282F;
         float f12 = Mth.sin(f7 + (float) (Math.PI * 3.0 / 4.0)) * 0.282F;
         float f13 = Mth.cos(f7 + (float) (Math.PI / 4)) * 0.282F;
         float f14 = Mth.sin(f7 + (float) (Math.PI / 4)) * 0.282F;
         float f15 = Mth.cos(f7 + ((float) Math.PI * 5.0F / 4.0F)) * 0.282F;
         float f16 = Mth.sin(f7 + ((float) Math.PI * 5.0F / 4.0F)) * 0.282F;
         float f17 = Mth.cos(f7 + ((float) Math.PI * 7.0F / 4.0F)) * 0.282F;
         float f18 = Mth.sin(f7 + ((float) Math.PI * 7.0F / 4.0F)) * 0.282F;
         float f19 = Mth.cos(f7 + (float) Math.PI) * 0.2F;
         float f20 = Mth.sin(f7 + (float) Math.PI) * 0.2F;
         float f21 = Mth.cos(f7 + 0.0F) * 0.2F;
         float f22 = Mth.sin(f7 + 0.0F) * 0.2F;
         float f23 = Mth.cos(f7 + (float) (Math.PI / 2)) * 0.2F;
         float f24 = Mth.sin(f7 + (float) (Math.PI / 2)) * 0.2F;
         float f25 = Mth.cos(f7 + (float) (Math.PI * 3.0 / 2.0)) * 0.2F;
         float f26 = Mth.sin(f7 + (float) (Math.PI * 3.0 / 2.0)) * 0.2F;
         float f27 = 0.0F;
         float f28 = 0.4999F;
         float f29 = -1.0F + f2;
         float f30 = f4 * 2.5F + f29;
         VertexConsumer ivertexbuilder = buffer.getBuffer(BEAM_RENDER_TYPE);
         Pose matrixstack$entry = matrixStack.last();
         Matrix4f matrix4f = matrixstack$entry.pose();
         Matrix3f matrix3f = matrixstack$entry.normal();
         vertex(ivertexbuilder, matrix4f, matrix3f, f19, f4, f20, j, k, l, 0.4999F, f30);
         vertex(ivertexbuilder, matrix4f, matrix3f, f19, 0.0F, f20, j, k, l, 0.4999F, f29);
         vertex(ivertexbuilder, matrix4f, matrix3f, f21, 0.0F, f22, j, k, l, 0.0F, f29);
         vertex(ivertexbuilder, matrix4f, matrix3f, f21, f4, f22, j, k, l, 0.0F, f30);
         vertex(ivertexbuilder, matrix4f, matrix3f, f23, f4, f24, j, k, l, 0.4999F, f30);
         vertex(ivertexbuilder, matrix4f, matrix3f, f23, 0.0F, f24, j, k, l, 0.4999F, f29);
         vertex(ivertexbuilder, matrix4f, matrix3f, f25, 0.0F, f26, j, k, l, 0.0F, f29);
         vertex(ivertexbuilder, matrix4f, matrix3f, f25, f4, f26, j, k, l, 0.0F, f30);
         float f31 = 0.0F;
         if (entity.tickCount % 2 == 0) {
            f31 = 0.5F;
         }

         vertex(ivertexbuilder, matrix4f, matrix3f, f11, f4, f12, j, k, l, 0.5F, f31 + 0.5F);
         vertex(ivertexbuilder, matrix4f, matrix3f, f13, f4, f14, j, k, l, 1.0F, f31 + 0.5F);
         vertex(ivertexbuilder, matrix4f, matrix3f, f17, f4, f18, j, k, l, 1.0F, f31);
         vertex(ivertexbuilder, matrix4f, matrix3f, f15, f4, f16, j, k, l, 0.5F, f31);
         matrixStack.popPose();
      }
   }

   private static void vertex(VertexConsumer builder, Matrix4f matrix, Matrix3f normal, float x, float y, float z, int r, int g, int b, float u, float v) {
      builder.vertex(matrix, x, y, z)
         .color(r, g, b, 255)
         .uv(u, v)
         .overlayCoords(OverlayTexture.NO_OVERLAY)
         .uv2(15728880)
         .normal(normal, 0.0F, 1.0F, 0.0F)
         .endVertex();
   }

   public float getAttackAnimationScale(EyesoreEntity entity, float p_175477_1_) {
      return (entity.laserAttack.tick + p_175477_1_) / 80.0F;
   }

   private Vec3 getPosition(LivingEntity entityLivingBaseIn, double p_177110_2_, float p_177110_4_) {
      double d0 = Mth.lerp(p_177110_4_, entityLivingBaseIn.xOld, entityLivingBaseIn.getX());
      double d1 = Mth.lerp(p_177110_4_, entityLivingBaseIn.yOld, entityLivingBaseIn.getY()) + p_177110_2_;
      double d2 = Mth.lerp(p_177110_4_, entityLivingBaseIn.zOld, entityLivingBaseIn.getZ());
      return new Vec3(d0, d1, d2);
   }
}
