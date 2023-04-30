package iskallia.vault.entity.model.mushroom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import iskallia.vault.entity.entity.mushroom.SmolcapEntity;
import javax.annotation.Nonnull;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class SmolcapModel extends EntityModel<SmolcapEntity> {
   private final ModelPart body;
   private final ModelPart head;
   private final ModelPart leftArm;
   private final ModelPart rightArm;
   private final ModelPart leftLeg;
   private final ModelPart rightLeg;

   public SmolcapModel(ModelPart root) {
      this.body = root.getChild("body");
      this.head = root.getChild("head");
      this.leftArm = this.body.getChild("left_arm");
      this.rightArm = this.body.getChild("right_arm");
      this.leftLeg = root.getChild("left_leg");
      this.rightLeg = root.getChild("right_leg");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-4.0F, 6.0F, -10.0F, 8.0F, 9.0F, 5.0F, new CubeDeformation(0.0F))
            .texOffs(18, 14)
            .addBox(-3.0F, 8.0F, -12.0F, 6.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 11.0F, -10.0F, 1.5708F, 0.0F, 0.0F)
      );
      PartDefinition right_arm = body.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create().texOffs(26, 0).addBox(-1.5F, -2.0F, -1.5F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-3.2623F, 7.2167F, -11.0F, -1.5708F, 0.0F, -0.1745F)
      );
      PartDefinition left_arm = body.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create().texOffs(24, 22).addBox(-1.5F, -2.0F, -1.5F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(3.2623F, 7.2167F, -11.0F, -1.5708F, 0.0F, 0.1745F)
      );
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create().texOffs(0, 14).addBox(-2.0F, 0.0F, 3.0F, 4.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 19.0F, -10.0F)
      );
      PartDefinition right_leg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create().texOffs(12, 22).addBox(-2.0F, -2.0F, -8.0F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-3.5F, 22.0F, 11.0F)
      );
      PartDefinition left_leg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create().texOffs(0, 22).addBox(-1.0F, -2.0F, -8.0F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offset(3.5F, 22.0F, 11.0F)
      );
      return LayerDefinition.create(meshdefinition, 64, 64);
   }

   public void setupAnim(@Nonnull SmolcapEntity entity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
      this.rightLeg.xRot = Mth.cos(pLimbSwing * 0.6662F * 0.6F) * 0.5F * pLimbSwingAmount;
      this.leftLeg.xRot = Mth.cos(pLimbSwing * 0.6662F * 0.6F + (float) Math.PI) * 0.5F * pLimbSwingAmount;
      this.rightArm.zRot = Mth.cos(pLimbSwing * 0.6662F * 0.6F + (float) Math.PI) * 0.5F * pLimbSwingAmount;
      this.leftArm.zRot = Mth.cos(pLimbSwing * 0.6662F * 0.6F) * 0.5F * pLimbSwingAmount;
      this.rightArm.xRot = (float) (Math.PI * 3.0 / 2.0);
      this.rightArm.zRot = -0.17453294F;
      this.leftArm.xRot = (float) (Math.PI * 3.0 / 2.0);
      this.leftArm.zRot = 0.17453294F;
      this.rightArm.yRot = 0.0F;
      this.leftArm.yRot = 0.0F;
      this.rightLeg.yRot = 0.0F;
      this.leftLeg.yRot = 0.0F;
      boolean walking = !entity.isInWater() && entity.isOnGround();
      if (walking) {
         float f = 1.0F;
         float f1 = 1.0F;
         float f2 = 5.0F;
         this.rightArm.z = -10.0F + Mth.cos(f * pLimbSwing * 2.0F + (float) Math.PI) * 6.0F * pLimbSwingAmount * f1;
         this.leftArm.z = -10.0F + Mth.cos(f * pLimbSwing * 2.0F) * 6.0F * pLimbSwingAmount * f1;
         this.rightLeg.z = 11.0F + Mth.cos(f * pLimbSwing * 2.0F) * 6.0F * pLimbSwingAmount * f1;
         this.leftLeg.z = 11.0F + Mth.cos(f * pLimbSwing * 2.0F + (float) Math.PI) * 6.0F * pLimbSwingAmount * f1;
      }
   }

   public void renderToBuffer(
      @Nonnull PoseStack poseStack, @Nonnull VertexConsumer buffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha
   ) {
      this.head.render(poseStack, buffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
      this.body.render(poseStack, buffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
      this.rightLeg.render(poseStack, buffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
      this.leftLeg.render(poseStack, buffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
   }
}
