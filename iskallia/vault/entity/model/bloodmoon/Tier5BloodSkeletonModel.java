package iskallia.vault.entity.model.bloodmoon;

import iskallia.vault.entity.entity.bloodmoon.Tier5BloodSkeletonEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class Tier5BloodSkeletonModel extends SkeletonModel<Tier5BloodSkeletonEntity> {
   public Tier5BloodSkeletonModel(ModelPart root) {
      super(root);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition hat = partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create()
            .texOffs(0, 28)
            .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.25F))
            .texOffs(25, 19)
            .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, -6.0F, 0.0F)
      );
      PartDefinition left_arm = partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create()
            .texOffs(19, 72)
            .addBox(0.0F, -2.0F, -2.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.1F))
            .texOffs(36, 80)
            .addBox(1.0F, -2.0F, -1.0F, 2.0F, 14.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(5.0F, -4.0F, 0.0F)
      );
      PartDefinition cube_r1 = left_arm.addOrReplaceChild(
         "cube_r1",
         CubeListBuilder.create().texOffs(66, 5).addBox(-3.0F, -1.0F, -3.0F, 6.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(4.6058F, -4.5866F, 0.0F, 0.0F, -0.7854F, 0.3927F)
      );
      PartDefinition cube_r2 = left_arm.addOrReplaceChild(
         "cube_r2",
         CubeListBuilder.create().texOffs(0, 45).addBox(-2.0F, -5.0F, -3.0F, 8.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3927F)
      );
      PartDefinition right_arm = partdefinition.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create()
            .texOffs(19, 72)
            .mirror()
            .addBox(-4.0F, -2.0F, -2.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.1F))
            .mirror(false)
            .texOffs(36, 80)
            .mirror()
            .addBox(-3.0F, -2.0F, -1.0F, 2.0F, 14.0F, 2.0F, new CubeDeformation(0.0F))
            .mirror(false),
         PartPose.offset(-5.0F, -4.0F, 0.0F)
      );
      PartDefinition cube_r3 = right_arm.addOrReplaceChild(
         "cube_r3",
         CubeListBuilder.create().texOffs(66, 5).mirror().addBox(-3.0F, -1.0F, -3.0F, 6.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(-4.6058F, -4.5866F, 0.0F, 0.0F, 0.7854F, -0.3927F)
      );
      PartDefinition cube_r4 = right_arm.addOrReplaceChild(
         "cube_r4",
         CubeListBuilder.create().texOffs(0, 45).mirror().addBox(-6.0F, -5.0F, -3.0F, 8.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3927F)
      );
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create()
            .texOffs(33, 36)
            .addBox(-5.0F, 0.0F, -2.0F, 10.0F, 16.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(33, 5)
            .addBox(-5.5F, 11.0F, -2.5F, 11.0F, 5.0F, 5.0F, new CubeDeformation(0.1F))
            .texOffs(33, 0)
            .addBox(-9.0F, 1.0F, 2.0F, 18.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, -6.0F, 0.0F)
      );
      PartDefinition cape_r1 = body.addOrReplaceChild(
         "cape_r1",
         CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, 0.0F, 0.0F, 16.0F, 26.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 2.0F, 3.0F, 0.1309F, 0.0F, 0.0F)
      );
      PartDefinition armour_r1 = body.addOrReplaceChild(
         "armour_r1",
         CubeListBuilder.create()
            .texOffs(23, 57)
            .addBox(-5.0F, 0.0F, -3.0F, 5.0F, 8.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(56, 51)
            .addBox(-5.0F, 8.0F, -3.0F, 5.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-1.0F, 14.0F, 0.0F, 0.0F, 0.0F, 0.3927F)
      );
      PartDefinition armour_r2 = body.addOrReplaceChild(
         "armour_r2",
         CubeListBuilder.create().texOffs(62, 31).addBox(-5.0F, -1.0F, -3.0F, 5.0F, 7.0F, 6.0F, new CubeDeformation(0.1F)),
         PartPose.offsetAndRotation(-1.0F, 14.0F, 0.0F, 0.0F, 0.0F, 0.7854F)
      );
      PartDefinition armour_r3 = body.addOrReplaceChild(
         "armour_r3",
         CubeListBuilder.create()
            .texOffs(0, 58)
            .addBox(0.0F, 8.0F, -3.0F, 5.0F, 8.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(58, 16)
            .addBox(0.0F, 0.0F, -3.0F, 5.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(1.0F, 14.0F, 0.0F, 0.0F, 0.0F, -0.3927F)
      );
      PartDefinition armour_r4 = body.addOrReplaceChild(
         "armour_r4",
         CubeListBuilder.create().texOffs(40, 66).addBox(0.0F, -1.0F, -3.0F, 5.0F, 7.0F, 6.0F, new CubeDeformation(0.1F)),
         PartPose.offsetAndRotation(1.0F, 14.0F, 0.0F, 0.0F, 0.0F, -0.7854F)
      );
      PartDefinition armour_r5 = body.addOrReplaceChild(
         "armour_r5",
         CubeListBuilder.create().texOffs(75, 14).addBox(-2.0F, -2.0F, -1.5F, 4.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 14.0F, -2.5F, 0.0F, 0.0F, -0.7854F)
      );
      PartDefinition left_leg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create()
            .texOffs(0, 73)
            .addBox(-1.9F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(79, 56)
            .addBox(-1.9F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.25F)),
         PartPose.offset(2.9F, 10.0F, 0.0F)
      );
      PartDefinition right_leg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create()
            .texOffs(63, 66)
            .addBox(-2.1F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(73, 45)
            .addBox(-2.1F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.25F)),
         PartPose.offset(-2.9F, 10.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }
}
