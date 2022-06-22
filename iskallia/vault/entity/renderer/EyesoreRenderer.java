package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.matrix.MatrixStack.Entry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import iskallia.vault.Vault;
import iskallia.vault.client.util.RenderTypeDecorator;
import iskallia.vault.entity.EyesoreEntity;
import iskallia.vault.entity.model.EyesoreModel;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.IRenderTypeBuffer.Impl;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;

public class EyesoreRenderer extends MobRenderer<EyesoreEntity, EyesoreModel> {
   public static final ResourceLocation DEFAULT_TEXTURE = Vault.id("textures/entity/eyesore/default.png");
   public static final ResourceLocation SORE_EYE_TEXTURE = Vault.id("textures/entity/eyesore/sore_eye.png");
   private static final ResourceLocation GUARDIAN_BEAM_TEXTURE = Vault.id("textures/entity/eyesore/laser_beam.png");
   private static final RenderType field_229107_h_ = RenderType.func_228640_c_(GUARDIAN_BEAM_TEXTURE);

   public EyesoreRenderer(EntityRendererManager rendererManager) {
      super(rendererManager, new EyesoreModel(), 0.5F);
   }

   protected void preRenderCallback(@Nonnull EyesoreEntity entity, @Nonnull MatrixStack matrixStack, float partialTickTime) {
      float f = 9.0F;
      matrixStack.func_227862_a_(f, f, f);
   }

   protected void renderName(EyesoreEntity entityIn, ITextComponent displayNameIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
   }

   protected boolean canRenderName(EyesoreEntity entity) {
      return false;
   }

   @Nonnull
   public ResourceLocation getEntityTexture(@Nonnull EyesoreEntity entity) {
      UUID targetPlayer = (UUID)((Optional)entity.func_184212_Q().func_187225_a(EyesoreEntity.LASER_TARGET)).orElse(null);
      if ((targetPlayer == null || entity.field_70170_p.func_217371_b(targetPlayer) == null)
         && !(Boolean)entity.func_184212_Q().func_187225_a(EyesoreEntity.WATCH_CLIENT)) {
         return entity.getState() == EyesoreEntity.State.GIVING_BIRTH ? SORE_EYE_TEXTURE : DEFAULT_TEXTURE;
      } else {
         return SORE_EYE_TEXTURE;
      }
   }

   public void render(EyesoreEntity entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLightIn) {
      super.func_225623_a_(entity, entityYaw, partialTicks, matrixStack, buffer, packedLightIn);
      LivingEntity livingentity = (LivingEntity)((Optional)entity.func_184212_Q().func_187225_a(EyesoreEntity.LASER_TARGET))
         .<PlayerEntity>map(playerId -> entity.func_130014_f_().func_217371_b(playerId))
         .orElse(null);
      ((EyesoreModel)this.field_77045_g).tentaclesRemaining = entity.getTentaclesRemaining();
      if (livingentity != null) {
         float f = this.getAttackAnimationScale(entity, partialTicks);
         float f1 = (float)entity.field_70170_p.func_82737_E() + partialTicks;
         float f2 = f1 * 0.5F % 1.0F;
         float f3 = entity.func_70047_e();
         matrixStack.func_227860_a_();
         matrixStack.func_227861_a_(0.0, f3, 0.0);
         Vector3d vector3d = this.getPosition(livingentity, livingentity.func_213302_cg() * 0.5, partialTicks);
         Vector3d vector3d1 = this.getPosition(entity, f3, partialTicks);
         Vector3d eyePos1 = entity.func_174824_e(partialTicks);
         RayTraceContext context = new RayTraceContext(eyePos1, vector3d, BlockMode.COLLIDER, FluidMode.NONE, entity);
         BlockRayTraceResult result = entity.field_70170_p.func_217299_a(context);
         vector3d1 = eyePos1;
         if (result.func_216346_c() != Type.MISS) {
            vector3d = result.func_216347_e();
         }

         Vector3d vector3d2 = vector3d.func_178788_d(eyePos1);
         float f4 = (float)(vector3d2.func_72433_c() + 1.0);
         vector3d2 = vector3d2.func_72432_b();
         float f5 = (float)Math.acos(vector3d2.field_72448_b);
         float f6 = (float)Math.atan2(vector3d2.field_72449_c, vector3d2.field_72450_a);
         matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(((float) (Math.PI / 2) - f6) * (180.0F / (float)Math.PI)));
         matrixStack.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(f5 * (180.0F / (float)Math.PI)));
         int i = 1;
         float f7 = f1 * 0.05F * -1.5F;
         float f8 = f * f;
         int j = 190;
         int k = 0;
         int l = 0;
         float f9 = 0.2F;
         float f10 = 0.282F;
         float f11 = MathHelper.func_76134_b(f7 + (float) (Math.PI * 3.0 / 4.0)) * 0.282F;
         float f12 = MathHelper.func_76126_a(f7 + (float) (Math.PI * 3.0 / 4.0)) * 0.282F;
         float f13 = MathHelper.func_76134_b(f7 + (float) (Math.PI / 4)) * 0.282F;
         float f14 = MathHelper.func_76126_a(f7 + (float) (Math.PI / 4)) * 0.282F;
         float f15 = MathHelper.func_76134_b(f7 + ((float) Math.PI * 5.0F / 4.0F)) * 0.282F;
         float f16 = MathHelper.func_76126_a(f7 + ((float) Math.PI * 5.0F / 4.0F)) * 0.282F;
         float f17 = MathHelper.func_76134_b(f7 + ((float) Math.PI * 7.0F / 4.0F)) * 0.282F;
         float f18 = MathHelper.func_76126_a(f7 + ((float) Math.PI * 7.0F / 4.0F)) * 0.282F;
         float f19 = MathHelper.func_76134_b(f7 + (float) Math.PI) * 0.2F;
         float f20 = MathHelper.func_76126_a(f7 + (float) Math.PI) * 0.2F;
         float f21 = MathHelper.func_76134_b(f7 + 0.0F) * 0.2F;
         float f22 = MathHelper.func_76126_a(f7 + 0.0F) * 0.2F;
         float f23 = MathHelper.func_76134_b(f7 + (float) (Math.PI / 2)) * 0.2F;
         float f24 = MathHelper.func_76126_a(f7 + (float) (Math.PI / 2)) * 0.2F;
         float f25 = MathHelper.func_76134_b(f7 + (float) (Math.PI * 3.0 / 2.0)) * 0.2F;
         float f26 = MathHelper.func_76126_a(f7 + (float) (Math.PI * 3.0 / 2.0)) * 0.2F;
         float f27 = 0.0F;
         float f28 = 0.4999F;
         float f29 = -1.0F + f2;
         float f30 = f4 * 2.5F + f29;
         Vector3d direction = vector3d.func_178788_d(eyePos1);
         double directionLength = direction.func_72433_c();
         direction = direction.func_72432_b();

         for (int step = 0; step <= directionLength; step++) {
            Vector3d pos = vector3d1.func_178787_e(direction.func_186678_a(step));
            entity.field_70170_p.func_195594_a(RedstoneParticleData.field_197564_a, pos.field_72450_a, pos.field_72448_b, pos.field_72449_c, 0.0, 0.0, 0.0);
         }

         RenderType type = RenderTypeDecorator.decorate(field_229107_h_, () -> RenderSystem.disableCull(), () -> {});
         IVertexBuilder ivertexbuilder = buffer.getBuffer(type);
         Entry matrixstack$entry = matrixStack.func_227866_c_();
         Matrix4f matrix4f = matrixstack$entry.func_227870_a_();
         Matrix3f matrix3f = matrixstack$entry.func_227872_b_();
         func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f19, f4, f20, j, k, l, 0.4999F, f30);
         func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f19, 0.0F, f20, j, k, l, 0.4999F, f29);
         func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f21, 0.0F, f22, j, k, l, 0.0F, f29);
         func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f21, f4, f22, j, k, l, 0.0F, f30);
         func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f23, f4, f24, j, k, l, 0.4999F, f30);
         func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f23, 0.0F, f24, j, k, l, 0.4999F, f29);
         func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f25, 0.0F, f26, j, k, l, 0.0F, f29);
         func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f25, f4, f26, j, k, l, 0.0F, f30);
         float f31 = 0.0F;
         if (entity.field_70173_aa % 2 == 0) {
            f31 = 0.5F;
         }

         func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f11, f4, f12, j, k, l, 0.5F, f31 + 0.5F);
         func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f13, f4, f14, j, k, l, 1.0F, f31 + 0.5F);
         func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f17, f4, f18, j, k, l, 1.0F, f31);
         func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f15, f4, f16, j, k, l, 0.5F, f31);
         matrixStack.func_227865_b_();
         if (buffer instanceof Impl) {
            ((Impl)buffer).func_228462_a_(type);
         }
      }
   }

   private static void func_229108_a_(
      IVertexBuilder builder, Matrix4f matrix, Matrix3f normal, float x, float y, float z, int r, int g, int b, float u, float v
   ) {
      builder.func_227888_a_(matrix, x, y, z)
         .func_225586_a_(r, g, b, 255)
         .func_225583_a_(u, v)
         .func_227891_b_(OverlayTexture.field_229196_a_)
         .func_227886_a_(15728880)
         .func_227887_a_(normal, 0.0F, 1.0F, 0.0F)
         .func_181675_d();
   }

   public float getAttackAnimationScale(EyesoreEntity entity, float p_175477_1_) {
      return (entity.laserTick + p_175477_1_) / 80.0F;
   }

   private Vector3d getPosition(LivingEntity entityLivingBaseIn, double p_177110_2_, float p_177110_4_) {
      double d0 = MathHelper.func_219803_d(p_177110_4_, entityLivingBaseIn.field_70142_S, entityLivingBaseIn.func_226277_ct_());
      double d1 = MathHelper.func_219803_d(p_177110_4_, entityLivingBaseIn.field_70137_T, entityLivingBaseIn.func_226278_cu_()) + p_177110_2_;
      double d2 = MathHelper.func_219803_d(p_177110_4_, entityLivingBaseIn.field_70136_U, entityLivingBaseIn.func_226281_cx_());
      return new Vector3d(d0, d1, d2);
   }
}
