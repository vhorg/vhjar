package iskallia.vault.entity.model.mushroom;

import iskallia.vault.entity.entity.mushroom.DeathcapEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class DeathcapModel extends HumanoidModel<DeathcapEntity> {
   public DeathcapModel(ModelPart root) {
      super(root);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition hat = partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      PartDefinition cube2_r1 = body.addOrReplaceChild(
         "cube2_r1",
         CubeListBuilder.create().texOffs(0, 50).addBox(-8.25F, -6.0F, -1.5F, 16.0F, 4.0F, 16.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.1781F, 0.0F, 0.0F)
      );
      PartDefinition body_r1 = body.addOrReplaceChild(
         "body_r1",
         CubeListBuilder.create()
            .texOffs(0, 39)
            .addBox(-2.5F, 0.0F, -2.0F, 5.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(76, 46)
            .addBox(-4.0F, -6.0F, -3.0F, 8.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(0, 70)
            .addBox(-4.0F, -6.0F, 0.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, 0.3927F, 0.0F, 0.0F)
      );
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create()
            .texOffs(48, 50)
            .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-11.25F, -10.0F, -11.5F, 22.0F, 4.0F, 22.0F, new CubeDeformation(0.0F))
            .texOffs(0, 26)
            .addBox(-10.25F, -6.0F, -10.5F, 20.0F, 4.0F, 20.0F, new CubeDeformation(0.0F))
            .texOffs(0, 50)
            .addBox(-8.25F, -14.0F, -8.5F, 16.0F, 4.0F, 16.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition cube_r1 = head.addOrReplaceChild(
         "cube_r1",
         CubeListBuilder.create().texOffs(24, 70).addBox(-2.0F, -3.0F, -4.5F, 5.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-0.5F, 1.0F, -2.5F, -0.3927F, 0.0F, 0.0F)
      );
      PartDefinition cube_r2 = head.addOrReplaceChild(
         "cube_r2",
         CubeListBuilder.create()
            .texOffs(60, 26)
            .addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(66, 0)
            .addBox(-3.0F, 3.0F, -3.0F, 6.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -3.0F, -7.0F, 0.3927F, 0.0F, 0.0F)
      );
      PartDefinition right_arm = partdefinition.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(58, 66)
            .addBox(-4.0F, 4.0F, -3.0F, 6.0F, 10.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(0, 16)
            .addBox(-6.0F, 6.0F, 0.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(60, 40)
            .addBox(-5.0F, 9.0F, -4.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-5.0F, 2.0F, 0.0F)
      );
      PartDefinition left_arm = partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create()
            .texOffs(20, 82)
            .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(80, 56)
            .addBox(-1.0F, 5.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(5.0F, 2.0F, 0.0F)
      );
      PartDefinition right_leg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create().texOffs(46, 78).addBox(-2.5F, 3.0F, -5.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-1.9F, 12.0F, 3.0F)
      );
      PartDefinition right_leg_r1 = right_leg.addOrReplaceChild(
         "right_leg_r1",
         CubeListBuilder.create().texOffs(78, 78).addBox(-3.0F, -6.5F, -1.0F, 5.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 1.5F, -3.0F, -0.7854F, 0.0F, 0.0F)
      );
      PartDefinition left_leg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create().texOffs(0, 50).addBox(-1.25F, 3.0F, -2.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(1.9F, 12.0F, 0.0F)
      );
      PartDefinition left_leg_r1 = left_leg.addOrReplaceChild(
         "left_leg_r1",
         CubeListBuilder.create().texOffs(0, 26).addBox(-2.5F, -4.5F, -2.0F, 5.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.7F, 0.7929F, 2.1213F, -0.7854F, 0.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }
}
