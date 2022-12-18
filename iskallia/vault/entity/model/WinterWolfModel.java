package iskallia.vault.entity.model;

import com.google.common.collect.ImmutableList;
import iskallia.vault.entity.entity.WinterWolfEntity;
import javax.annotation.Nonnull;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class WinterWolfModel extends AgeableListModel<WinterWolfEntity> {
   protected final ModelPart head;
   protected final ModelPart body;
   protected final ModelPart rightHindLeg;
   protected final ModelPart leftHindLeg;
   protected final ModelPart rightFrontLeg;
   protected final ModelPart leftFrontLeg;
   protected final ModelPart tail;
   protected final ModelPart upperBody;

   public ModelPart getHead() {
      return this.head;
   }

   public WinterWolfModel(ModelPart root) {
      this.head = root.getChild("head");
      this.body = root.getChild("body");
      this.upperBody = root.getChild("upper_body");
      this.rightHindLeg = root.getChild("right_hind_leg");
      this.leftHindLeg = root.getChild("left_hind_leg");
      this.rightFrontLeg = root.getChild("right_front_leg");
      this.leftFrontLeg = root.getChild("left_front_leg");
      this.tail = root.getChild("tail");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create()
            .texOffs(0, 33)
            .addBox(-2.0F, -3.0F, -2.0F, 6.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(0, 3)
            .addBox(-2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(33, 14)
            .addBox(-0.5F, -0.0156F, -5.0F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-1.0F, 13.5F, -7.0F)
      );
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create().texOffs(0, 18).addBox(-3.0F, -2.0F, -3.0F, 6.0F, 9.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 14.0F, 2.0F, 1.5708F, 0.0F, 0.0F)
      );
      PartDefinition upper_body = partdefinition.addOrReplaceChild(
         "upper_body",
         CubeListBuilder.create().texOffs(24, 18).addBox(0.0F, -2.0F, -8.0F, 2.0F, 12.0F, 5.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-1.0F, 14.0F, 2.0F, -1.5708F, 0.0F, 0.0F)
      );
      PartDefinition upperBody_r1 = upper_body.addOrReplaceChild(
         "upperBody_r1",
         CubeListBuilder.create().texOffs(34, 0).addBox(-1.5F, -2.5F, 0.0F, 5.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -2.6965F, -4.0119F, -0.3927F, 0.0F, 0.0F)
      );
      PartDefinition upperBody_r2 = upper_body.addOrReplaceChild(
         "upperBody_r2",
         CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -1.0F, 8.0F, 9.0F, 9.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(1.0F, 10.0F, -2.0F, 0.3927F, 0.0F, 0.0F)
      );
      PartDefinition right_hind_leg = partdefinition.addOrReplaceChild(
         "right_hind_leg",
         CubeListBuilder.create().texOffs(42, 41).addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-2.5F, 16.0F, 7.0F)
      );
      PartDefinition left_hind_leg = partdefinition.addOrReplaceChild(
         "left_hind_leg",
         CubeListBuilder.create().texOffs(38, 21).addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.5F, 16.0F, 7.0F)
      );
      PartDefinition right_front_leg = partdefinition.addOrReplaceChild(
         "right_front_leg",
         CubeListBuilder.create().texOffs(36, 33).addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-2.5F, 16.0F, -4.0F)
      );
      PartDefinition left_front_leg = partdefinition.addOrReplaceChild(
         "left_front_leg",
         CubeListBuilder.create().texOffs(28, 35).addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.5F, 16.0F, -4.0F)
      );
      PartDefinition tail = partdefinition.addOrReplaceChild(
         "tail",
         CubeListBuilder.create()
            .texOffs(20, 35)
            .addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(19, 53)
            .addBox(-0.5F, 4.0F, -1.5F, 3.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-1.0F, 12.0F, 8.0F, 0.9599F, 0.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 64, 64);
   }

   @Nonnull
   protected Iterable<ModelPart> headParts() {
      return ImmutableList.of(this.head);
   }

   @Nonnull
   protected Iterable<ModelPart> bodyParts() {
      return ImmutableList.of(this.body, this.rightHindLeg, this.leftHindLeg, this.rightFrontLeg, this.leftFrontLeg, this.tail, this.upperBody);
   }

   public void prepareMobModel(@Nonnull WinterWolfEntity entity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick) {
      this.tail.yRot = Mth.cos(pLimbSwing * 0.6662F) * 1.4F * pLimbSwingAmount;
      this.body.setPos(0.0F, 14.0F, 2.0F);
      this.body.xRot = (float) (Math.PI / 2);
      this.upperBody.setPos(1.0F, 14.0F, -3.0F);
      this.upperBody.xRot = this.body.xRot;
      this.upperBody.zRot = (float) Math.PI;
      this.tail.setPos(-1.0F, 12.0F, 8.0F);
      this.rightHindLeg.setPos(-2.5F, 16.0F, 7.0F);
      this.leftHindLeg.setPos(0.5F, 16.0F, 7.0F);
      this.rightFrontLeg.setPos(-2.5F, 16.0F, -4.0F);
      this.leftFrontLeg.setPos(0.5F, 16.0F, -4.0F);
      this.rightHindLeg.xRot = Mth.cos(pLimbSwing * 0.6662F) * 1.4F * pLimbSwingAmount;
      this.leftHindLeg.xRot = Mth.cos(pLimbSwing * 0.6662F + (float) Math.PI) * 1.4F * pLimbSwingAmount;
      this.rightFrontLeg.xRot = Mth.cos(pLimbSwing * 0.6662F + (float) Math.PI) * 1.4F * pLimbSwingAmount;
      this.leftFrontLeg.xRot = Mth.cos(pLimbSwing * 0.6662F) * 1.4F * pLimbSwingAmount;
   }

   public void setupAnim(@Nonnull WinterWolfEntity entity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
      this.head.xRot = pHeadPitch * (float) (Math.PI / 180.0);
      this.head.yRot = pNetHeadYaw * (float) (Math.PI / 180.0);
      this.tail.xRot = pAgeInTicks;
   }
}
