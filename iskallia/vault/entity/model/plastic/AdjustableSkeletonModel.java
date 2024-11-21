package iskallia.vault.entity.model.plastic;

import com.mojang.math.Vector3f;
import iskallia.vault.entity.entity.plastic.PlasticSkeletonEntity;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class AdjustableSkeletonModel extends SkeletonModel<PlasticSkeletonEntity> {
   protected Vector3f headPos = Vector3f.ZERO;
   protected Vector3f hatPos = Vector3f.ZERO;
   protected Vector3f bodyPos = Vector3f.ZERO;
   protected Vector3f rightArmPos = new Vector3f(-5.0F, 2.0F, 0.0F);
   protected Vector3f leftArmPos = new Vector3f(5.0F, 2.0F, 0.0F);
   protected Vector3f rightLegPos = new Vector3f(0.0F, 12.0F, 0.0F);
   protected Vector3f leftLegPos = new Vector3f(0.0F, 12.0F, 0.0F);

   public AdjustableSkeletonModel(ModelPart root) {
      super(root);
   }

   public AdjustableSkeletonModel setHeadPos(Vector3f headPos) {
      this.headPos = headPos;
      return this;
   }

   public AdjustableSkeletonModel setHatPos(Vector3f hatPos) {
      this.hatPos = hatPos;
      return this;
   }

   public AdjustableSkeletonModel setBodyPos(Vector3f bodyPos) {
      this.bodyPos = bodyPos;
      return this;
   }

   public AdjustableSkeletonModel setRightArmPos(Vector3f rightArmPos) {
      this.rightArmPos = rightArmPos;
      return this;
   }

   public AdjustableSkeletonModel setLeftArmPos(Vector3f leftArmPos) {
      this.leftArmPos = leftArmPos;
      return this;
   }

   public AdjustableSkeletonModel setRightLegPos(Vector3f rightLegPos) {
      this.rightLegPos = rightLegPos;
      return this;
   }

   public AdjustableSkeletonModel setLeftLegPos(Vector3f leftLegPos) {
      this.leftLegPos = leftLegPos;
      return this;
   }

   public void setupAnim(@NotNull PlasticSkeletonEntity skeleton, float limbSwing, float limbSwingAmount, float age, float headYaw, float headPitch) {
      this.head.yRot = headYaw * (float) (Math.PI / 180.0);
      this.head.xRot = headPitch * (float) (Math.PI / 180.0);
      this.body.yRot = 0.0F;
      this.rightArm.x = this.rightArmPos.x();
      this.rightArm.z = this.rightArmPos.z();
      this.leftArm.x = this.leftArmPos.x();
      this.leftArm.z = this.leftArmPos.z();
      float limbSwingFactor = 0.6662F;
      this.rightArm.xRot = Mth.cos(limbSwing * limbSwingFactor + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F;
      this.leftArm.xRot = Mth.cos(limbSwing * limbSwingFactor) * 2.0F * limbSwingAmount * 0.5F;
      this.rightArm.zRot = 0.0F;
      this.leftArm.zRot = 0.0F;
      this.rightLeg.xRot = Mth.cos(limbSwing * limbSwingFactor) * 1.4F * limbSwingAmount;
      this.leftLeg.xRot = Mth.cos(limbSwing * limbSwingFactor + (float) Math.PI) * 1.4F * limbSwingAmount;
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
      boolean isRightArm = skeleton.getMainArm() == HumanoidArm.RIGHT;
      if (skeleton.isUsingItem()) {
         boolean isMainHand = skeleton.getUsedItemHand() == InteractionHand.MAIN_HAND;
         if (isMainHand == isRightArm) {
            this.poseRightArm();
         } else {
            this.poseLeftArm();
         }
      } else {
         boolean isMainHand = isRightArm ? this.leftArmPose.isTwoHanded() : this.rightArmPose.isTwoHanded();
         if (isRightArm != isMainHand) {
            this.poseLeftArm();
            this.poseRightArm();
         } else {
            this.poseRightArm();
            this.poseLeftArm();
         }
      }

      this.setupAttackAnimation(skeleton, age);
      this.resetModel();
      AnimationUtils.bobModelPart(this.rightArm, age, 1.0F);
      AnimationUtils.bobModelPart(this.leftArm, age, -1.0F);
      this.hat.copyFrom(this.head);
      ItemStack itemInHand = skeleton.getMainHandItem();
      boolean isAggressiveWithoutBow = skeleton.isAggressive() && (itemInHand.isEmpty() || !itemInHand.is(Items.BOW));
      if (isAggressiveWithoutBow) {
         this.attackWithHands(age);
      }
   }

   private void resetModel() {
      this.body.xRot = 0.0F;
      this.head.y = this.headPos.y();
      this.body.y = this.bodyPos.y();
      this.rightArm.y = this.rightArmPos.y();
      this.leftArm.y = this.leftArmPos.y();
      this.rightLeg.y = this.rightLegPos.y();
      this.rightLeg.z = this.rightLegPos.z();
      this.leftLeg.y = this.leftLegPos.y();
      this.leftLeg.z = this.leftLegPos.z();
   }

   private void attackWithHands(float age) {
      float swingProgress = Mth.sin((float)(this.attackTime * Math.PI));
      float maxSwing = Mth.sin((float)((1.0F - (1.0F - this.attackTime) * (1.0F - this.attackTime)) * Math.PI));
      this.rightArm.zRot = 0.0F;
      this.leftArm.zRot = 0.0F;
      this.rightArm.yRot = -(0.1F - swingProgress * 0.6F);
      this.leftArm.yRot = 0.1F - swingProgress * 0.6F;
      this.rightArm.xRot = (float) (-Math.PI / 2);
      this.leftArm.xRot = (float) (-Math.PI / 2);
      ModelPart rightArm = this.rightArm;
      rightArm.xRot -= swingProgress * 1.2F - maxSwing * 0.4F;
      rightArm = this.leftArm;
      rightArm.xRot -= swingProgress * 1.2F - maxSwing * 0.4F;
      AnimationUtils.bobArms(this.rightArm, this.leftArm, age);
   }

   private void poseRightArm() {
      switch (this.rightArmPose) {
         case EMPTY:
            this.rightArm.yRot = 0.0F;
            break;
         case BOW_AND_ARROW:
            this.rightArm.yRot = -0.1F + this.head.yRot;
            this.leftArm.yRot = 0.1F + this.head.yRot + 0.4F;
            this.rightArm.xRot = (float) (-Math.PI / 2) + this.head.xRot;
            this.leftArm.xRot = (float) (-Math.PI / 2) + this.head.xRot;
      }
   }

   private void poseLeftArm() {
      switch (this.leftArmPose) {
         case EMPTY:
            this.leftArm.yRot = 0.0F;
            break;
         case BOW_AND_ARROW:
            this.rightArm.yRot = -0.1F + this.head.yRot - 0.4F;
            this.leftArm.yRot = 0.1F + this.head.yRot;
            this.rightArm.xRot = (float) (-Math.PI / 2) + this.head.xRot;
            this.leftArm.xRot = (float) (-Math.PI / 2) + this.head.xRot;
      }
   }
}
