package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.Vault;
import iskallia.vault.entity.FighterEntity;
import iskallia.vault.entity.model.FighterModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.BeeStingerLayer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.BipedModel.ArmPose;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;

public class FighterRenderer extends LivingRenderer<FighterEntity, FighterModel> {
   public FighterRenderer(EntityRendererManager renderManager) {
      this(renderManager, false);
   }

   public FighterRenderer(EntityRendererManager renderManager, boolean useSmallArms) {
      super(renderManager, new FighterModel(0.0F, useSmallArms), 0.5F);
      this.func_177094_a(new BipedArmorLayer(this, new BipedModel(0.5F), new BipedModel(1.0F)));
      this.func_177094_a(new HeldItemLayer(this));
      this.func_177094_a(new ArrowLayer(this));
      this.func_177094_a(new HeadLayer(this));
      this.func_177094_a(new ElytraLayer(this));
      this.func_177094_a(new BeeStingerLayer(this));
   }

   protected void preRenderCallback(FighterEntity entity, MatrixStack matrixStack, float partialTickTime) {
      float f = entity.sizeMultiplier;
      matrixStack.func_227862_a_(f, f, f);
   }

   public void render(FighterEntity entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLightIn) {
      this.setModelVisibilities(entity);
      super.func_225623_a_(entity, entityYaw, partialTicks, matrixStack, buffer, packedLightIn);
   }

   public void renderCrown(FighterEntity entity, MatrixStack matrixStack, IRenderTypeBuffer buffer) {
      matrixStack.func_227860_a_();
      float sizeMultiplier = entity.getSizeMultiplier();
      matrixStack.func_227862_a_(sizeMultiplier, sizeMultiplier, sizeMultiplier);
      matrixStack.func_227861_a_(0.0, 2.5, 0.0);
      float scale = 2.5F;
      matrixStack.func_227862_a_(scale, scale, scale);
      matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(entity.field_70173_aa));
      matrixStack.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(20.0F));
      ItemStack itemStack = new ItemStack((IItemProvider)Registry.field_212630_s.func_82594_a(Vault.id("mvp_crown")));
      IBakedModel ibakedmodel = Minecraft.func_71410_x().func_175599_af().func_184393_a(itemStack, null, null);
      Minecraft.func_71410_x().func_175599_af().func_229111_a_(itemStack, TransformType.GROUND, true, matrixStack, buffer, 15728864, 655360, ibakedmodel);
      matrixStack.func_227865_b_();
   }

   public Vector3d getRenderOffset(FighterEntity entityIn, float partialTicks) {
      return entityIn.func_213453_ef() ? new Vector3d(0.0, -0.125, 0.0) : super.func_225627_b_(entityIn, partialTicks);
   }

   private void setModelVisibilities(FighterEntity clientPlayer) {
      FighterModel playermodel = (FighterModel)this.func_217764_d();
      if (clientPlayer.func_175149_v()) {
         playermodel.func_178719_a(false);
         playermodel.field_78116_c.field_78806_j = true;
         playermodel.field_178720_f.field_78806_j = true;
      } else {
         playermodel.func_178719_a(true);
         playermodel.field_228270_o_ = clientPlayer.func_213453_ef();
         ArmPose bipedmodel$armpose = func_241741_a_(clientPlayer, Hand.MAIN_HAND);
         ArmPose bipedmodel$armpose1 = func_241741_a_(clientPlayer, Hand.OFF_HAND);
         if (bipedmodel$armpose.func_241657_a_()) {
            bipedmodel$armpose1 = clientPlayer.func_184592_cb().func_190926_b() ? ArmPose.EMPTY : ArmPose.ITEM;
         }

         if (clientPlayer.func_184591_cq() == HandSide.RIGHT) {
            playermodel.field_187076_m = bipedmodel$armpose;
            playermodel.field_187075_l = bipedmodel$armpose1;
         } else {
            playermodel.field_187076_m = bipedmodel$armpose1;
            playermodel.field_187075_l = bipedmodel$armpose;
         }
      }
   }

   private static ArmPose func_241741_a_(FighterEntity p_241741_0_, Hand p_241741_1_) {
      ItemStack itemstack = p_241741_0_.func_184586_b(p_241741_1_);
      if (itemstack.func_190926_b()) {
         return ArmPose.EMPTY;
      } else {
         if (p_241741_0_.func_184600_cs() == p_241741_1_ && p_241741_0_.func_184605_cv() > 0) {
            UseAction useaction = itemstack.func_77975_n();
            if (useaction == UseAction.BLOCK) {
               return ArmPose.BLOCK;
            }

            if (useaction == UseAction.BOW) {
               return ArmPose.BOW_AND_ARROW;
            }

            if (useaction == UseAction.SPEAR) {
               return ArmPose.THROW_SPEAR;
            }

            if (useaction == UseAction.CROSSBOW && p_241741_1_ == p_241741_0_.func_184600_cs()) {
               return ArmPose.CROSSBOW_CHARGE;
            }
         } else if (!p_241741_0_.field_82175_bq && itemstack.func_77973_b() == Items.field_222114_py && CrossbowItem.func_220012_d(itemstack)) {
            return ArmPose.CROSSBOW_HOLD;
         }

         return ArmPose.ITEM;
      }
   }

   public ResourceLocation getEntityTexture(FighterEntity entity) {
      return entity.getLocationSkin();
   }

   protected void preRenderCallback(AbstractClientPlayerEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
      float f = 0.9375F;
      matrixStackIn.func_227862_a_(0.9375F, 0.9375F, 0.9375F);
   }

   protected void renderName(FighterEntity entityIn, ITextComponent displayNameIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
      double d0 = this.field_76990_c.func_229099_b_(entityIn);
      matrixStackIn.func_227860_a_();
      super.func_225629_a_(entityIn, displayNameIn, matrixStackIn, bufferIn, packedLightIn);
      matrixStackIn.func_227865_b_();
   }

   public void renderRightArm(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, FighterEntity playerIn) {
      this.renderItem(
         matrixStackIn,
         bufferIn,
         combinedLightIn,
         playerIn,
         ((FighterModel)this.field_77045_g).field_178723_h,
         ((FighterModel)this.field_77045_g).field_178732_b
      );
   }

   public void renderLeftArm(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, FighterEntity playerIn) {
      this.renderItem(
         matrixStackIn,
         bufferIn,
         combinedLightIn,
         playerIn,
         ((FighterModel)this.field_77045_g).field_178724_i,
         ((FighterModel)this.field_77045_g).field_178734_a
      );
   }

   private void renderItem(
      MatrixStack matrixStackIn, IRenderTypeBuffer buffer, int combinedLight, FighterEntity entity, ModelRenderer rendererArm, ModelRenderer rendererArmWear
   ) {
      FighterModel playermodel = (FighterModel)this.func_217764_d();
      this.setModelVisibilities(entity);
      playermodel.field_217112_c = 0.0F;
      playermodel.field_228270_o_ = false;
      playermodel.field_205061_a = 0.0F;
      playermodel.func_225597_a_(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
      rendererArm.field_78795_f = 0.0F;
      rendererArm.func_228308_a_(
         matrixStackIn, buffer.getBuffer(RenderType.func_228634_a_(this.getEntityTexture(entity))), combinedLight, OverlayTexture.field_229196_a_
      );
      rendererArmWear.field_78795_f = 0.0F;
      rendererArmWear.func_228308_a_(
         matrixStackIn, buffer.getBuffer(RenderType.func_228644_e_(this.getEntityTexture(entity))), combinedLight, OverlayTexture.field_229196_a_
      );
   }

   protected void applyRotations(FighterEntity entityLiving, MatrixStack matrixStack, float ageInTicks, float rotationYaw, float partialTicks) {
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
