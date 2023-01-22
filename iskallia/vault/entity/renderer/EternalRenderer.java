package iskallia.vault.entity.renderer;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import iskallia.vault.VaultMod;
import iskallia.vault.client.gui.overlay.ArenaScoreboardOverlay;
import iskallia.vault.entity.entity.EternalEntity;
import iskallia.vault.entity.model.EternalModel;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.world.data.EternalsData;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.BeeStingerLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EternalRenderer extends LivingEntityRenderer<EternalEntity, EternalModel> {
   private static final Map<EternalsData.EternalVariant, ResourceLocation> LOCATION_BY_VARIANT = (Map<EternalsData.EternalVariant, ResourceLocation>)Util.make(
      Maps.newEnumMap(EternalsData.EternalVariant.class), p_114874_ -> {
         p_114874_.put(EternalsData.EternalVariant.CAVE, VaultMod.id("textures/entity/vaultdwellercave.png"));
         p_114874_.put(EternalsData.EternalVariant.DESERT, VaultMod.id("textures/entity/vaultdwellerdesert.png"));
         p_114874_.put(EternalsData.EternalVariant.HELL, VaultMod.id("textures/entity/vaultdwellerhell.png"));
         p_114874_.put(EternalsData.EternalVariant.ICE, VaultMod.id("textures/entity/vaultdwellerice.png"));
         p_114874_.put(EternalsData.EternalVariant.LUSH, VaultMod.id("textures/entity/vaultdwellerlush.png"));
         p_114874_.put(EternalsData.EternalVariant.VOID, VaultMod.id("textures/entity/vaultdwellervoid.png"));
      }
   );

   public static Map<EternalsData.EternalVariant, ResourceLocation> getLocationByVariant() {
      return LOCATION_BY_VARIANT;
   }

   public EternalRenderer(Context context) {
      this(context, false);
   }

   public EternalRenderer(Context context, boolean slim) {
      super(context, new EternalModel(context.bakeLayer(ModModelLayers.ETERNAL), slim), 0.5F);
      this.addLayer(
         new HumanoidArmorLayer(
            this,
            new HumanoidModel(context.bakeLayer(slim ? ModelLayers.PLAYER_SLIM_INNER_ARMOR : ModelLayers.PLAYER_INNER_ARMOR)),
            new HumanoidModel(context.bakeLayer(slim ? ModelLayers.PLAYER_SLIM_OUTER_ARMOR : ModelLayers.PLAYER_OUTER_ARMOR))
         )
      );
      this.addLayer(new EternalRenderer.EternalItemInHandLayer(this));
      this.addLayer(new ArrowLayer(context, this));
      this.addLayer(new CustomHeadLayer(this, context.getModelSet()));
      this.addLayer(new ElytraLayer(this, context.getModelSet()));
      this.addLayer(new BeeStingerLayer(this));
   }

   protected void scale(EternalEntity entity, PoseStack matrixStack, float partialTickTime) {
      float f = entity.sizeMultiplier;
      matrixStack.scale(f, f, f);
   }

   public void render(EternalEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLightIn) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.5F);
      this.setModelVisibilities(entity);
      String nickname = entity.getDisplayName().getString();
      if (nickname.equals(ArenaScoreboardOverlay.scoreboard.getMVP())) {
         this.renderCrown(entity, matrixStack, buffer);
      }

      super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLightIn);
   }

   public void renderCrown(EternalEntity entity, PoseStack matrixStack, MultiBufferSource buffer) {
      matrixStack.pushPose();
      float sizeMultiplier = entity.getSizeMultiplier();
      matrixStack.scale(sizeMultiplier, sizeMultiplier, sizeMultiplier);
      matrixStack.translate(0.0, 2.5, 0.0);
      float scale = 2.5F;
      matrixStack.scale(scale, scale, scale);
      matrixStack.mulPose(Vector3f.YP.rotationDegrees(entity.tickCount));
      matrixStack.mulPose(Vector3f.ZP.rotationDegrees(20.0F));
      ItemStack itemStack = new ItemStack((ItemLike)Registry.ITEM.get(VaultMod.id("mvp_crown")));
      BakedModel ibakedmodel = Minecraft.getInstance().getItemRenderer().getModel(itemStack, null, null, 0);
      Minecraft.getInstance().getItemRenderer().render(itemStack, TransformType.GROUND, true, matrixStack, buffer, 15728864, 655360, ibakedmodel);
      matrixStack.popPose();
   }

   public Vec3 getRenderOffset(EternalEntity entityIn, float partialTicks) {
      return entityIn.isCrouching() ? new Vec3(0.0, -0.125, 0.0) : super.getRenderOffset(entityIn, partialTicks);
   }

   private void setModelVisibilities(EternalEntity clientPlayer) {
      EternalModel playermodel = (EternalModel)this.getModel();
      if (clientPlayer.isSpectator()) {
         playermodel.setAllVisible(false);
         playermodel.head.visible = true;
         playermodel.hat.visible = true;
      } else {
         playermodel.setAllVisible(true);
         playermodel.crouching = clientPlayer.isCrouching();
         ArmPose bipedmodel$armpose = getArmPose(clientPlayer, InteractionHand.MAIN_HAND);
         ArmPose bipedmodel$armpose1 = getArmPose(clientPlayer, InteractionHand.OFF_HAND);
         if (bipedmodel$armpose.isTwoHanded()) {
            bipedmodel$armpose1 = clientPlayer.getOffhandItem().isEmpty() ? ArmPose.EMPTY : ArmPose.ITEM;
         }

         if (clientPlayer.getMainArm() == HumanoidArm.RIGHT) {
            playermodel.rightArmPose = bipedmodel$armpose;
            playermodel.leftArmPose = bipedmodel$armpose1;
         } else {
            playermodel.rightArmPose = bipedmodel$armpose1;
            playermodel.leftArmPose = bipedmodel$armpose;
         }
      }
   }

   private static ArmPose getArmPose(EternalEntity p_241741_0_, InteractionHand p_241741_1_) {
      ItemStack itemstack = p_241741_0_.getItemInHand(p_241741_1_);
      if (itemstack.isEmpty()) {
         return ArmPose.EMPTY;
      } else {
         if (p_241741_0_.getUsedItemHand() == p_241741_1_ && p_241741_0_.getUseItemRemainingTicks() > 0) {
            UseAnim useaction = itemstack.getUseAnimation();
            if (useaction == UseAnim.BLOCK) {
               return ArmPose.BLOCK;
            }

            if (useaction == UseAnim.BOW) {
               return ArmPose.BOW_AND_ARROW;
            }

            if (useaction == UseAnim.SPEAR) {
               return ArmPose.THROW_SPEAR;
            }

            if (useaction == UseAnim.CROSSBOW && p_241741_1_ == p_241741_0_.getUsedItemHand()) {
               return ArmPose.CROSSBOW_CHARGE;
            }
         } else if (!p_241741_0_.swinging && itemstack.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemstack)) {
            return ArmPose.CROSSBOW_HOLD;
         }

         return ArmPose.ITEM;
      }
   }

   public ResourceLocation getTextureLocation(EternalEntity entity) {
      return !entity.isUsingPlayerSkin() ? LOCATION_BY_VARIANT.get(entity.getVariant()) : entity.getLocationSkin();
   }

   protected void preRenderCallback(AbstractClientPlayer entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
      float f = 0.9375F;
      matrixStackIn.scale(0.9375F, 0.9375F, 0.9375F);
   }

   protected boolean shouldShowName(EternalEntity pEntity) {
      return pEntity.shouldShowName() && super.shouldShowName(pEntity);
   }

   protected void renderNameTag(EternalEntity entityIn, Component displayNameIn, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
      double d0 = this.entityRenderDispatcher.distanceToSqr(entityIn);
      matrixStackIn.pushPose();
      super.renderNameTag(entityIn, displayNameIn, matrixStackIn, bufferIn, packedLightIn);
      matrixStackIn.popPose();
   }

   public void renderRightArm(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, EternalEntity playerIn) {
      this.renderItem(matrixStackIn, bufferIn, combinedLightIn, playerIn, ((EternalModel)this.model).rightArm, ((EternalModel)this.model).rightSleeve);
   }

   public void renderLeftArm(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, EternalEntity playerIn) {
      this.renderItem(matrixStackIn, bufferIn, combinedLightIn, playerIn, ((EternalModel)this.model).leftArm, ((EternalModel)this.model).leftSleeve);
   }

   private void renderItem(
      PoseStack matrixStackIn, MultiBufferSource buffer, int combinedLight, EternalEntity entity, ModelPart rendererArm, ModelPart rendererArmWear
   ) {
      EternalModel playermodel = (EternalModel)this.getModel();
      this.setModelVisibilities(entity);
      playermodel.attackTime = 0.0F;
      playermodel.crouching = false;
      playermodel.swimAmount = 0.0F;
      playermodel.setupAnim(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
      rendererArm.xRot = 0.0F;
      rendererArm.render(matrixStackIn, buffer.getBuffer(RenderType.entitySolid(this.getTextureLocation(entity))), combinedLight, OverlayTexture.NO_OVERLAY);
      rendererArmWear.xRot = 0.0F;
      rendererArmWear.render(
         matrixStackIn, buffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity))), combinedLight, OverlayTexture.NO_OVERLAY
      );
   }

   protected void setupRotations(EternalEntity pEntityLiving, PoseStack pMatrixStack, float pAgeInTicks, float pRotationYaw, float pPartialTicks) {
      float f = pEntityLiving.getSwimAmount(pPartialTicks);
      if (pEntityLiving.isFallFlying()) {
         super.setupRotations(pEntityLiving, pMatrixStack, pAgeInTicks, pRotationYaw, pPartialTicks);
         float f1 = pEntityLiving.getFallFlyingTicks() + pPartialTicks;
         float f2 = Mth.clamp(f1 * f1 / 100.0F, 0.0F, 1.0F);
         if (!pEntityLiving.isAutoSpinAttack()) {
            pMatrixStack.mulPose(Vector3f.XP.rotationDegrees(f2 * (-90.0F - pEntityLiving.getXRot())));
         }

         Vec3 vec3 = pEntityLiving.getViewVector(pPartialTicks);
         Vec3 vec31 = pEntityLiving.getDeltaMovement();
         double d0 = vec31.horizontalDistanceSqr();
         double d1 = vec3.horizontalDistanceSqr();
         if (d0 > 0.0 && d1 > 0.0) {
            double d2 = (vec31.x * vec3.x + vec31.z * vec3.z) / Math.sqrt(d0 * d1);
            double d3 = vec31.x * vec3.z - vec31.z * vec3.x;
            pMatrixStack.mulPose(Vector3f.YP.rotation((float)(Math.signum(d3) * Math.acos(d2))));
         }
      } else if (f > 0.0F) {
         super.setupRotations(pEntityLiving, pMatrixStack, pAgeInTicks, pRotationYaw, pPartialTicks);
         float f3 = pEntityLiving.isInWater() ? -90.0F - pEntityLiving.getXRot() : -90.0F;
         float f4 = Mth.lerp(f, 0.0F, f3);
         pMatrixStack.mulPose(Vector3f.XP.rotationDegrees(f4));
         if (pEntityLiving.isVisuallySwimming()) {
            pMatrixStack.translate(0.0, -1.0, 0.3F);
         }
      } else {
         super.setupRotations(pEntityLiving, pMatrixStack, pAgeInTicks, pRotationYaw, pPartialTicks);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public class EternalItemInHandLayer<T extends EternalEntity, M extends EntityModel<T> & ArmedModel & HeadedModel> extends ItemInHandLayer<T, M> {
      public EternalItemInHandLayer(RenderLayerParent<T, M> p_174516_) {
         super(p_174516_);
      }

      protected void renderArmWithItem(
         LivingEntity p_174525_,
         ItemStack p_174526_,
         TransformType p_174527_,
         HumanoidArm p_174528_,
         PoseStack p_174529_,
         MultiBufferSource p_174530_,
         int p_174531_
      ) {
         if (p_174526_.is(Items.SPYGLASS) && p_174525_.getUseItem() == p_174526_ && p_174525_.swingTime == 0) {
            this.renderArmWithSpyglass(p_174525_, p_174526_, p_174528_, p_174529_, p_174530_, p_174531_);
         } else {
            super.renderArmWithItem(p_174525_, p_174526_, p_174527_, p_174528_, p_174529_, p_174530_, p_174531_);
         }
      }

      private void renderArmWithSpyglass(
         LivingEntity p_174518_, ItemStack p_174519_, HumanoidArm p_174520_, PoseStack p_174521_, MultiBufferSource p_174522_, int p_174523_
      ) {
         p_174521_.pushPose();
         ModelPart modelpart = ((HeadedModel)this.getParentModel()).getHead();
         float f = modelpart.xRot;
         modelpart.xRot = Mth.clamp(modelpart.xRot, (float) (-Math.PI / 6), (float) (Math.PI / 2));
         modelpart.translateAndRotate(p_174521_);
         modelpart.xRot = f;
         CustomHeadLayer.translateToHead(p_174521_, false);
         boolean flag = p_174520_ == HumanoidArm.LEFT;
         p_174521_.translate((flag ? -2.5F : 2.5F) / 16.0F, -0.0625, 0.0);
         Minecraft.getInstance().getItemInHandRenderer().renderItem(p_174518_, p_174519_, TransformType.HEAD, false, p_174521_, p_174522_, p_174523_);
         p_174521_.popPose();
      }
   }
}
