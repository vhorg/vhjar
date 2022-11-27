package iskallia.vault.entity.model;

import iskallia.vault.entity.entity.VaultSpiderBabyEntity;
import javax.annotation.Nonnull;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class VaultSpiderBabyModel<T extends VaultSpiderBabyEntity> extends HierarchicalModel<T> {
   private final ModelPart root;
   private final ModelPart head;
   private final ModelPart rightHindLeg;
   private final ModelPart leftHindLeg;
   private final ModelPart rightMiddleHindLeg;
   private final ModelPart leftMiddleHindLeg;
   private final ModelPart rightMiddleFrontLeg;
   private final ModelPart leftMiddleFrontLeg;
   private final ModelPart rightFrontLeg;
   private final ModelPart leftFrontLeg;

   public VaultSpiderBabyModel(ModelPart pRoot) {
      this.root = pRoot;
      this.head = pRoot.getChild("head");
      this.rightHindLeg = pRoot.getChild("right_hind_leg");
      this.leftHindLeg = pRoot.getChild("left_hind_leg");
      this.rightMiddleHindLeg = pRoot.getChild("right_middle_hind_leg");
      this.leftMiddleHindLeg = pRoot.getChild("left_middle_hind_leg");
      this.rightMiddleFrontLeg = pRoot.getChild("right_middle_front_leg");
      this.leftMiddleFrontLeg = pRoot.getChild("left_middle_front_leg");
      this.rightFrontLeg = pRoot.getChild("right_front_leg");
      this.leftFrontLeg = pRoot.getChild("left_front_leg");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create().texOffs(0, 30).addBox(-4.0F, -4.0F, -9.75F, 8.0F, 8.0F, 8.0F, new CubeDeformation(2.0F)),
         PartPose.offset(0.0F, 15.0F, -3.0F)
      );
      PartDefinition body0 = partdefinition.addOrReplaceChild(
         "body0",
         CubeListBuilder.create().texOffs(50, 4).addBox(-3.0F, -3.5F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(2.0F)),
         PartPose.offset(0.0F, 15.0F, 0.0F)
      );
      PartDefinition body1 = partdefinition.addOrReplaceChild(
         "body1",
         CubeListBuilder.create().texOffs(0, 5).addBox(-5.0F, -4.0F, -6.0F, 10.0F, 8.0F, 12.0F, new CubeDeformation(2.0F)),
         PartPose.offset(0.0F, 15.0F, 9.0F)
      );
      PartDefinition right_hind_leg = partdefinition.addOrReplaceChild(
         "right_hind_leg",
         CubeListBuilder.create().texOffs(24, 30).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(2.0F)),
         PartPose.offsetAndRotation(-4.0F, 15.0F, 2.0F, 0.0F, 0.7854F, -0.7854F)
      );
      PartDefinition left_hind_leg = partdefinition.addOrReplaceChild(
         "left_hind_leg",
         CubeListBuilder.create().texOffs(24, 30).addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(2.0F)),
         PartPose.offsetAndRotation(4.0F, 15.0F, 2.0F, 0.0F, -0.7854F, 0.7854F)
      );
      PartDefinition right_middle_hind_leg = partdefinition.addOrReplaceChild(
         "right_middle_hind_leg",
         CubeListBuilder.create().texOffs(24, 30).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(2.0F)),
         PartPose.offsetAndRotation(-4.0F, 15.0F, 1.0F, 0.0F, 0.2618F, -0.6109F)
      );
      PartDefinition left_middle_hind_leg = partdefinition.addOrReplaceChild(
         "left_middle_hind_leg",
         CubeListBuilder.create().texOffs(24, 30).addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(2.0F)),
         PartPose.offsetAndRotation(4.0F, 15.0F, 1.0F, 0.0F, -0.2618F, 0.6109F)
      );
      PartDefinition right_middle_front_leg = partdefinition.addOrReplaceChild(
         "right_middle_front_leg",
         CubeListBuilder.create().texOffs(24, 30).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(2.0F)),
         PartPose.offsetAndRotation(-4.0F, 15.0F, 0.0F, 0.0F, -0.2618F, -0.6109F)
      );
      PartDefinition left_middle_front_leg = partdefinition.addOrReplaceChild(
         "left_middle_front_leg",
         CubeListBuilder.create().texOffs(24, 30).addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(2.0F)),
         PartPose.offsetAndRotation(4.0F, 15.0F, 0.0F, 0.0F, 0.2618F, 0.6109F)
      );
      PartDefinition right_front_leg = partdefinition.addOrReplaceChild(
         "right_front_leg",
         CubeListBuilder.create().texOffs(24, 30).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(2.0F)),
         PartPose.offsetAndRotation(-4.0F, 15.0F, -1.0F, 0.0F, -0.7854F, -0.7854F)
      );
      PartDefinition left_front_leg = partdefinition.addOrReplaceChild(
         "left_front_leg",
         CubeListBuilder.create().texOffs(24, 30).addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(2.0F)),
         PartPose.offsetAndRotation(4.0F, 15.0F, -1.0F, 0.0F, 0.7854F, 0.7854F)
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }

   @Nonnull
   public ModelPart root() {
      return this.root;
   }

   public void setupAnim(@Nonnull T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
      this.head.yRot = pNetHeadYaw * (float) (Math.PI / 180.0);
      this.head.xRot = pHeadPitch * (float) (Math.PI / 180.0);
      float f = (float) (Math.PI / 4);
      this.rightHindLeg.zRot = (float) (-Math.PI / 4);
      this.leftHindLeg.zRot = (float) (Math.PI / 4);
      this.rightMiddleHindLeg.zRot = -0.58119464F;
      this.leftMiddleHindLeg.zRot = 0.58119464F;
      this.rightMiddleFrontLeg.zRot = -0.58119464F;
      this.leftMiddleFrontLeg.zRot = 0.58119464F;
      this.rightFrontLeg.zRot = (float) (-Math.PI / 4);
      this.leftFrontLeg.zRot = (float) (Math.PI / 4);
      float f1 = -0.0F;
      float f2 = (float) (Math.PI / 8);
      this.rightHindLeg.yRot = (float) (Math.PI / 4);
      this.leftHindLeg.yRot = (float) (-Math.PI / 4);
      this.rightMiddleHindLeg.yRot = (float) (Math.PI / 8);
      this.leftMiddleHindLeg.yRot = (float) (-Math.PI / 8);
      this.rightMiddleFrontLeg.yRot = (float) (-Math.PI / 8);
      this.leftMiddleFrontLeg.yRot = (float) (Math.PI / 8);
      this.rightFrontLeg.yRot = (float) (-Math.PI / 4);
      this.leftFrontLeg.yRot = (float) (Math.PI / 4);
      float f3 = -(Mth.cos(pLimbSwing * 0.6662F * 2.0F + 0.0F) * 0.4F) * pLimbSwingAmount;
      float f4 = -(Mth.cos(pLimbSwing * 0.6662F * 2.0F + (float) Math.PI) * 0.4F) * pLimbSwingAmount;
      float f5 = -(Mth.cos(pLimbSwing * 0.6662F * 2.0F + (float) (Math.PI / 2)) * 0.4F) * pLimbSwingAmount;
      float f6 = -(Mth.cos(pLimbSwing * 0.6662F * 2.0F + (float) (Math.PI * 3.0 / 2.0)) * 0.4F) * pLimbSwingAmount;
      float f7 = Math.abs(Mth.sin(pLimbSwing * 0.6662F + 0.0F) * 0.4F) * pLimbSwingAmount;
      float f8 = Math.abs(Mth.sin(pLimbSwing * 0.6662F + (float) Math.PI) * 0.4F) * pLimbSwingAmount;
      float f9 = Math.abs(Mth.sin(pLimbSwing * 0.6662F + (float) (Math.PI / 2)) * 0.4F) * pLimbSwingAmount;
      float f10 = Math.abs(Mth.sin(pLimbSwing * 0.6662F + (float) (Math.PI * 3.0 / 2.0)) * 0.4F) * pLimbSwingAmount;
      this.rightHindLeg.yRot += f3;
      this.leftHindLeg.yRot += -f3;
      this.rightMiddleHindLeg.yRot += f4;
      this.leftMiddleHindLeg.yRot += -f4;
      this.rightMiddleFrontLeg.yRot += f5;
      this.leftMiddleFrontLeg.yRot += -f5;
      this.rightFrontLeg.yRot += f6;
      this.leftFrontLeg.yRot += -f6;
      this.rightHindLeg.zRot += f7;
      this.leftHindLeg.zRot += -f7;
      this.rightMiddleHindLeg.zRot += f8;
      this.leftMiddleHindLeg.zRot += -f8;
      this.rightMiddleFrontLeg.zRot += f9;
      this.leftMiddleFrontLeg.zRot += -f9;
      this.rightFrontLeg.zRot += f10;
      this.leftFrontLeg.zRot += -f10;
   }
}
