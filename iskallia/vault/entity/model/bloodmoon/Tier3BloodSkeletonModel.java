package iskallia.vault.entity.model.bloodmoon;

import iskallia.vault.entity.entity.bloodmoon.Tier3BloodSkeletonEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class Tier3BloodSkeletonModel extends SkeletonModel<Tier3BloodSkeletonEntity> {
   public Tier3BloodSkeletonModel(ModelPart root) {
      super(root);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition hat = partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create()
            .texOffs(0, 15)
            .addBox(-4.0F, -7.0F, -7.0F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0.25F))
            .texOffs(0, 0)
            .addBox(-4.0F, -7.0F, -7.0F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(0, 31)
            .addBox(-4.0F, -1.0F, -7.0F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(25, 7)
            .addBox(-4.0F, 1.0F, -7.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 2.0F, -3.0F)
      );
      PartDefinition left_arm = partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create().texOffs(17, 42).addBox(0.0F, -1.25F, -1.0F, 2.0F, 14.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(5.0F, 2.25F, -1.75F)
      );
      PartDefinition right_arm = partdefinition.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create().texOffs(17, 42).mirror().addBox(-2.0F, -1.25F, -1.0F, 2.0F, 14.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offset(-5.0F, 2.25F, -1.75F)
      );
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create().texOffs(29, 39).addBox(-4.0F, 5.0F, -2.0F, 8.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition heart_r1 = body.addOrReplaceChild(
         "heart_r1",
         CubeListBuilder.create().texOffs(25, 0).addBox(-0.5F, -2.5F, -2.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 2.7664F, -1.3394F, 0.5299F, -1.0059F, 0.3655F)
      );
      PartDefinition cube_r1 = body.addOrReplaceChild(
         "cube_r1",
         CubeListBuilder.create().texOffs(28, 25).addBox(-5.0F, -7.0F, -2.5F, 10.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, 0.3927F, 0.0F, 0.0F)
      );
      PartDefinition left_leg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create()
            .texOffs(50, 0)
            .addBox(-0.9F, 5.0F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(0, 42)
            .addBox(-1.9F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(1.9F, 12.0F, 0.0F)
      );
      PartDefinition right_leg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create()
            .texOffs(50, 0)
            .mirror()
            .addBox(-1.1F, 5.0F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
            .mirror(false)
            .texOffs(0, 42)
            .mirror()
            .addBox(-2.1F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
            .mirror(false),
         PartPose.offset(-1.9F, 12.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 64, 64);
   }
}
