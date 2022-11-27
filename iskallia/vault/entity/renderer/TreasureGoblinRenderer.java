package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.TreasureGoblinEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.TreasureGoblinModel;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.phys.Vec3;

public class TreasureGoblinRenderer extends LivingEntityRenderer<TreasureGoblinEntity, TreasureGoblinModel> {
   public static final ResourceLocation TREASURE_GOBLIN_TEXTURES = VaultMod.id("textures/entity/treasure_goblin.png");

   public TreasureGoblinRenderer(Context context) {
      super(context, new TreasureGoblinModel(context.bakeLayer(ModModelLayers.TREASURE_GOBLIN)), 0.5F);
   }

   public ResourceLocation getTextureLocation(TreasureGoblinEntity entity) {
      return TREASURE_GOBLIN_TEXTURES;
   }

   protected void scale(TreasureGoblinEntity entity, PoseStack matrixStack, float partialTickTime) {
      float f = 0.75F;
      matrixStack.scale(f, f, f);
   }

   public Vec3 getRenderOffset(TreasureGoblinEntity entityIn, float partialTicks) {
      return entityIn.isCrouching() ? new Vec3(0.0, -0.125, 0.0) : super.getRenderOffset(entityIn, partialTicks);
   }

   public void render(TreasureGoblinEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLightIn) {
      this.setModelVisibilities(entity);
      super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLightIn);
   }

   protected void renderNameTag(TreasureGoblinEntity entityIn, Component displayNameIn, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
   }

   protected boolean shouldShowName(TreasureGoblinEntity entity) {
      return false;
   }

   private void setModelVisibilities(TreasureGoblinEntity entity) {
      TreasureGoblinModel model = (TreasureGoblinModel)this.getModel();
      if (entity.isSpectator()) {
         model.setAllVisible(false);
         model.head.visible = true;
         model.hat.visible = true;
      } else {
         model.setAllVisible(true);
         model.crouching = entity.isCrouching();
         ArmPose bipedmodel$armpose = getArmPose(entity, InteractionHand.MAIN_HAND);
         ArmPose bipedmodel$armpose1 = getArmPose(entity, InteractionHand.OFF_HAND);
         if (bipedmodel$armpose.isTwoHanded()) {
            bipedmodel$armpose1 = entity.getOffhandItem().isEmpty() ? ArmPose.EMPTY : ArmPose.ITEM;
         }

         if (entity.getMainArm() == HumanoidArm.RIGHT) {
            model.rightArmPose = bipedmodel$armpose;
            model.leftArmPose = bipedmodel$armpose1;
         } else {
            model.rightArmPose = bipedmodel$armpose1;
            model.leftArmPose = bipedmodel$armpose;
         }
      }
   }

   private static ArmPose getArmPose(TreasureGoblinEntity entity, InteractionHand hand) {
      return ArmPose.EMPTY;
   }

   protected void setupRotations(TreasureGoblinEntity entityLiving, PoseStack matrixStack, float ageInTicks, float rotationYaw, float partialTicks) {
      float f = entityLiving.getSwimAmount(partialTicks);
      if (entityLiving.isFallFlying()) {
         super.setupRotations(entityLiving, matrixStack, ageInTicks, rotationYaw, partialTicks);
         float f1 = entityLiving.getFallFlyingTicks() + partialTicks;
         float f2 = Mth.clamp(f1 * f1 / 100.0F, 0.0F, 1.0F);
         if (!entityLiving.isAutoSpinAttack()) {
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(f2 * (-90.0F - entityLiving.getXRot())));
         }

         Vec3 vector3d = entityLiving.getViewVector(partialTicks);
         Vec3 vector3d1 = entityLiving.getDeltaMovement();
         double d0 = vector3d1.horizontalDistanceSqr();
         double d1 = vector3d.horizontalDistanceSqr();
         if (d0 > 0.0 && d1 > 0.0) {
            double d2 = (vector3d1.x * vector3d.x + vector3d1.z * vector3d.z) / Math.sqrt(d0 * d1);
            double d3 = vector3d1.x * vector3d.z - vector3d1.z * vector3d.x;
            matrixStack.mulPose(Vector3f.YP.rotation((float)(Math.signum(d3) * Math.acos(d2))));
         }
      } else if (f > 0.0F) {
         super.setupRotations(entityLiving, matrixStack, ageInTicks, rotationYaw, partialTicks);
         float f3 = entityLiving.isInWater() ? -90.0F - entityLiving.getXRot() : -90.0F;
         float f4 = Mth.lerp(f, 0.0F, f3);
         matrixStack.mulPose(Vector3f.XP.rotationDegrees(f4));
         if (entityLiving.isVisuallySwimming()) {
            matrixStack.translate(0.0, -1.0, 0.3F);
         }
      } else {
         super.setupRotations(entityLiving, matrixStack, ageInTicks, rotationYaw, partialTicks);
      }
   }
}
