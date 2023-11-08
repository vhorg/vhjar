package iskallia.vault.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import iskallia.vault.entity.entity.AncientCopperGolemEntity;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;

public class AncientCopperGolemModel<T extends AncientCopperGolemEntity> extends HumanoidModel<T> {
   public AncientCopperGolemModel(ModelPart root) {
      super(root);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition Head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create()
            .texOffs(2, 1)
            .addBox(-4.0F, -4.0F, -4.0F, 8.0F, 5.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(1, 25)
            .addBox(-3.0F, -7.0F, -3.0F, 6.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(1, 34)
            .addBox(-3.5F, -5.0F, -3.5F, 7.0F, 1.0F, 7.0F, new CubeDeformation(0.0F))
            .texOffs(1, 1)
            .addBox(-1.0F, -1.0F, -6.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 13.0F, 0.0F)
      );
      PartDefinition hat = partdefinition.addOrReplaceChild(
         "hat",
         CubeListBuilder.create()
            .texOffs(2, 1)
            .addBox(-4.0F, -4.0F, -4.0F, 8.0F, 5.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(1, 25)
            .addBox(-3.0F, -7.0F, -3.0F, 6.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(1, 34)
            .addBox(-3.5F, -5.0F, -3.5F, 7.0F, 1.0F, 7.0F, new CubeDeformation(0.0F))
            .texOffs(1, 1)
            .addBox(-1.0F, -1.0F, -6.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 13.0F, 0.0F)
      );
      PartDefinition LeftArm = partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create().texOffs(33, 15).addBox(-1.0F, -1.0F, -1.5F, 2.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offset(5.0F, 14.0F, 0.0F)
      );
      PartDefinition RightArm = partdefinition.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create().texOffs(20, 15).addBox(-1.0F, -1.0F, -1.5F, 2.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-5.0F, 14.0F, 0.0F)
      );
      PartDefinition Body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create().texOffs(35, 3).addBox(-4.0F, -3.0F, -2.5F, 8.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 17.0F, 0.0F)
      );
      PartDefinition LeftLeg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create().texOffs(1, 16).addBox(-1.9F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(1.9F, 20.0F, 0.0F)
      );
      PartDefinition RightLeg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create().texOffs(46, 16).addBox(-2.1F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-1.9F, 20.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 64, 64);
   }

   public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
      if (pEntity.getWaxed()) {
         pLimbSwing = pEntity.animationPosition;
         pLimbSwingAmount = pEntity.animationSpeed;
      }

      boolean flag = pEntity.getFallFlyingTicks() > 4;
      boolean flag1 = pEntity.isVisuallySwimming();
      this.head.yRot = pNetHeadYaw * (float) (Math.PI / 180.0);
      if (flag) {
         this.head.xRot = (float) (-Math.PI / 4);
      } else if (this.swimAmount > 0.0F) {
         if (flag1) {
            this.head.xRot = this.rotlerpRad(this.swimAmount, this.head.xRot, (float) (-Math.PI / 4));
         } else {
            this.head.xRot = this.rotlerpRad(this.swimAmount, this.head.xRot, pHeadPitch * (float) (Math.PI / 180.0));
         }
      } else {
         this.head.xRot = pHeadPitch * (float) (Math.PI / 180.0);
      }

      this.body.yRot = 0.0F;
      float f = 1.0F;
      if (flag) {
         f = (float)pEntity.getDeltaMovement().lengthSqr();
         f /= 0.2F;
         f *= f * f;
      }

      if (f < 1.0F) {
         f = 1.0F;
      }

      this.rightArm.xRot = Mth.cos(pLimbSwing * 0.6662F + (float) Math.PI) * 2.0F * pLimbSwingAmount * 0.5F / f;
      this.leftArm.xRot = Mth.cos(pLimbSwing * 0.6662F) * 2.0F * pLimbSwingAmount * 0.5F / f;
      this.rightArm.zRot = 0.0F;
      this.leftArm.zRot = 0.0F;
      this.rightLeg.xRot = Mth.cos(pLimbSwing * 0.6662F) * 1.4F * pLimbSwingAmount / f;
      this.leftLeg.xRot = Mth.cos(pLimbSwing * 0.6662F + (float) Math.PI) * 1.4F * pLimbSwingAmount / f;
      this.rightLeg.yRot = 0.0F;
      this.leftLeg.yRot = 0.0F;
      this.rightLeg.zRot = 0.0F;
      this.leftLeg.zRot = 0.0F;
      if (this.riding) {
         this.rightArm.xRot += (float) (-Math.PI / 5);
         this.leftArm.xRot += (float) (-Math.PI / 5);
         this.rightLeg.xRot = -1.4137167F;
         this.rightLeg.yRot = (float) (Math.PI / 10);
         this.rightLeg.zRot = 0.07853982F;
         this.leftLeg.xRot = -1.4137167F;
         this.leftLeg.yRot = (float) (-Math.PI / 10);
         this.leftLeg.zRot = -0.07853982F;
      }

      this.rightArm.yRot = 0.0F;
      this.leftArm.yRot = 0.0F;
      boolean flag2 = pEntity.getMainArm() == HumanoidArm.RIGHT;
      if (pEntity.isUsingItem()) {
         boolean flag3 = pEntity.getUsedItemHand() == InteractionHand.MAIN_HAND;
         if (flag3 == flag2) {
            this.poseRightArm(pEntity);
         } else {
            this.poseLeftArm(pEntity);
         }
      } else {
         boolean flag4 = flag2 ? this.leftArmPose.isTwoHanded() : this.rightArmPose.isTwoHanded();
         if (flag2 != flag4) {
            this.poseLeftArm(pEntity);
            this.poseRightArm(pEntity);
         } else {
            this.poseRightArm(pEntity);
            this.poseLeftArm(pEntity);
         }
      }

      this.setupAttackAnimation(pEntity, pAgeInTicks);
      if (this.crouching) {
         this.body.xRot = 0.5F;
         this.rightArm.xRot += 0.4F;
         this.leftArm.xRot += 0.4F;
      } else {
         this.body.xRot = 0.0F;
      }

      if (!pEntity.getWaxed()) {
         if (this.rightArmPose != ArmPose.SPYGLASS) {
            AnimationUtils.bobModelPart(this.rightArm, pAgeInTicks, 1.0F);
         }

         if (this.leftArmPose != ArmPose.SPYGLASS) {
            AnimationUtils.bobModelPart(this.leftArm, pAgeInTicks, -1.0F);
         }
      }

      if (pEntity.hasCustomName()) {
         this.rightArm.zRot = (float)Math.toRadians(180.0);
         this.leftArm.zRot = (float)Math.toRadians(180.0);
         this.rightArm.xRot = Mth.cos(pLimbSwing * 0.6662F + (float) Math.PI) * 0.5F * pLimbSwingAmount * 0.5F / f;
         this.leftArm.xRot = Mth.cos(pLimbSwing * 0.6662F) * 0.5F * pLimbSwingAmount * 0.5F / f;
      }

      this.hat.copyFrom(this.head);
   }

   private void poseRightArm(T pLivingEntity) {
      switch (this.rightArmPose) {
         case EMPTY:
            this.rightArm.yRot = 0.0F;
            break;
         case BLOCK:
            this.rightArm.xRot = this.rightArm.xRot * 0.5F - 0.9424779F;
            this.rightArm.yRot = (float) (-Math.PI / 6);
            break;
         case ITEM:
            this.rightArm.xRot = this.rightArm.xRot * 0.5F - (float) (Math.PI / 10);
            this.rightArm.yRot = 0.0F;
            break;
         case THROW_SPEAR:
            this.rightArm.xRot = this.rightArm.xRot * 0.5F - (float) Math.PI;
            this.rightArm.yRot = 0.0F;
            break;
         case BOW_AND_ARROW:
            this.rightArm.yRot = -0.1F + this.head.yRot;
            this.leftArm.yRot = 0.1F + this.head.yRot + 0.4F;
            this.rightArm.xRot = (float) (-Math.PI / 2) + this.head.xRot;
            this.leftArm.xRot = (float) (-Math.PI / 2) + this.head.xRot;
            break;
         case CROSSBOW_CHARGE:
            AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, pLivingEntity, true);
            break;
         case CROSSBOW_HOLD:
            AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, true);
            break;
         case SPYGLASS:
            this.rightArm.xRot = Mth.clamp(this.head.xRot - 1.9198622F - (pLivingEntity.isCrouching() ? (float) (Math.PI / 12) : 0.0F), -2.4F, 3.3F);
            this.rightArm.yRot = this.head.yRot - (float) (Math.PI / 12);
      }
   }

   private void poseLeftArm(T pLivingEntity) {
      switch (this.leftArmPose) {
         case EMPTY:
            this.leftArm.yRot = 0.0F;
            break;
         case BLOCK:
            this.leftArm.xRot = this.leftArm.xRot * 0.5F - 0.9424779F;
            this.leftArm.yRot = (float) (Math.PI / 6);
            break;
         case ITEM:
            this.leftArm.xRot = this.leftArm.xRot * 0.5F - (float) (Math.PI / 10);
            this.leftArm.yRot = 0.0F;
            break;
         case THROW_SPEAR:
            this.leftArm.xRot = this.leftArm.xRot * 0.5F - (float) Math.PI;
            this.leftArm.yRot = 0.0F;
            break;
         case BOW_AND_ARROW:
            this.rightArm.yRot = -0.1F + this.head.yRot - 0.4F;
            this.leftArm.yRot = 0.1F + this.head.yRot;
            this.rightArm.xRot = (float) (-Math.PI / 2) + this.head.xRot;
            this.leftArm.xRot = (float) (-Math.PI / 2) + this.head.xRot;
            break;
         case CROSSBOW_CHARGE:
            AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, pLivingEntity, false);
            break;
         case CROSSBOW_HOLD:
            AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, false);
            break;
         case SPYGLASS:
            this.leftArm.xRot = Mth.clamp(this.head.xRot - 1.9198622F - (pLivingEntity.isCrouching() ? (float) (Math.PI / 12) : 0.0F), -2.4F, 3.3F);
            this.leftArm.yRot = this.head.yRot + (float) (Math.PI / 12);
      }
   }

   private HumanoidArm getAttackArm(T pEntity) {
      HumanoidArm humanoidarm = pEntity.getMainArm();
      return pEntity.swingingArm == InteractionHand.MAIN_HAND ? humanoidarm : humanoidarm.getOpposite();
   }

   private float quadraticArmUpdate(float pLimbSwing) {
      return -65.0F * pLimbSwing + pLimbSwing * pLimbSwing;
   }

   public void renderToBuffer(
      PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha
   ) {
      this.head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
      this.leftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
      this.rightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
      this.body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
      this.leftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
      this.rightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
   }
}
