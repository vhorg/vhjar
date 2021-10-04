package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.Vault;
import iskallia.vault.entity.TreasureGoblinEntity;
import iskallia.vault.entity.model.TreasureGoblinModel;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel.ArmPose;
import net.minecraft.entity.Entity;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;

public class TreasureGoblinRenderer extends LivingRenderer<TreasureGoblinEntity, TreasureGoblinModel> {
   public static final ResourceLocation TREASURE_GOBLIN_TEXTURES = Vault.id("textures/entity/treasure_goblin.png");

   public TreasureGoblinRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new TreasureGoblinModel(), 0.5F);
   }

   public ResourceLocation getEntityTexture(TreasureGoblinEntity entity) {
      return TREASURE_GOBLIN_TEXTURES;
   }

   protected void preRenderCallback(TreasureGoblinEntity entity, MatrixStack matrixStack, float partialTickTime) {
      float f = 0.75F;
      matrixStack.func_227862_a_(f, f, f);
   }

   public Vector3d getRenderOffset(TreasureGoblinEntity entityIn, float partialTicks) {
      return entityIn.func_213453_ef() ? new Vector3d(0.0, -0.125, 0.0) : super.func_225627_b_(entityIn, partialTicks);
   }

   public void render(TreasureGoblinEntity entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLightIn) {
      this.setModelVisibilities(entity);
      super.func_225623_a_(entity, entityYaw, partialTicks, matrixStack, buffer, packedLightIn);
   }

   protected void renderName(
      TreasureGoblinEntity entityIn, ITextComponent displayNameIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn
   ) {
   }

   protected boolean canRenderName(TreasureGoblinEntity entity) {
      return false;
   }

   private void setModelVisibilities(TreasureGoblinEntity entity) {
      TreasureGoblinModel model = (TreasureGoblinModel)this.func_217764_d();
      if (entity.func_175149_v()) {
         model.func_178719_a(false);
         model.field_78116_c.field_78806_j = true;
         model.field_178720_f.field_78806_j = true;
      } else {
         model.func_178719_a(true);
         model.field_228270_o_ = entity.func_213453_ef();
         ArmPose bipedmodel$armpose = func_241741_a_(entity, Hand.MAIN_HAND);
         ArmPose bipedmodel$armpose1 = func_241741_a_(entity, Hand.OFF_HAND);
         if (bipedmodel$armpose.func_241657_a_()) {
            bipedmodel$armpose1 = entity.func_184592_cb().func_190926_b() ? ArmPose.EMPTY : ArmPose.ITEM;
         }

         if (entity.func_184591_cq() == HandSide.RIGHT) {
            model.field_187076_m = bipedmodel$armpose;
            model.field_187075_l = bipedmodel$armpose1;
         } else {
            model.field_187076_m = bipedmodel$armpose1;
            model.field_187075_l = bipedmodel$armpose;
         }
      }
   }

   private static ArmPose func_241741_a_(TreasureGoblinEntity entity, Hand hand) {
      return ArmPose.EMPTY;
   }

   protected void applyRotations(TreasureGoblinEntity entityLiving, MatrixStack matrixStack, float ageInTicks, float rotationYaw, float partialTicks) {
      float f = entityLiving.func_205015_b(partialTicks);
      if (entityLiving.func_184613_cA()) {
         super.func_225621_a_(entityLiving, matrixStack, ageInTicks, rotationYaw, partialTicks);
         float f1 = entityLiving.func_184599_cB() + partialTicks;
         float f2 = MathHelper.func_76131_a(f1 * f1 / 100.0F, 0.0F, 1.0F);
         if (!entityLiving.func_204805_cN()) {
            matrixStack.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(f2 * (-90.0F - entityLiving.field_70125_A)));
         }

         Vector3d vector3d = entityLiving.func_70676_i(partialTicks);
         Vector3d vector3d1 = entityLiving.func_213322_ci();
         double d0 = Entity.func_213296_b(vector3d1);
         double d1 = Entity.func_213296_b(vector3d);
         if (d0 > 0.0 && d1 > 0.0) {
            double d2 = (vector3d1.field_72450_a * vector3d.field_72450_a + vector3d1.field_72449_c * vector3d.field_72449_c) / Math.sqrt(d0 * d1);
            double d3 = vector3d1.field_72450_a * vector3d.field_72449_c - vector3d1.field_72449_c * vector3d.field_72450_a;
            matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229193_c_((float)(Math.signum(d3) * Math.acos(d2))));
         }
      } else if (f > 0.0F) {
         super.func_225621_a_(entityLiving, matrixStack, ageInTicks, rotationYaw, partialTicks);
         float f3 = entityLiving.func_70090_H() ? -90.0F - entityLiving.field_70125_A : -90.0F;
         float f4 = MathHelper.func_219799_g(f, 0.0F, f3);
         matrixStack.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(f4));
         if (entityLiving.func_213314_bj()) {
            matrixStack.func_227861_a_(0.0, -1.0, 0.3F);
         }
      } else {
         super.func_225621_a_(entityLiving, matrixStack, ageInTicks, rotationYaw, partialTicks);
      }
   }
}
