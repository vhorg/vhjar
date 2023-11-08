package iskallia.vault.entity.model.bloodmoon;

import iskallia.vault.entity.entity.bloodmoon.Tier2BloodSkeletonEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class Tier2BloodSkeletonModel extends SkeletonModel<Tier2BloodSkeletonEntity> {
   public Tier2BloodSkeletonModel(ModelPart root) {
      super(root);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition hat = partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 0.0F));
      PartDefinition cube_r1 = head.addOrReplaceChild(
         "cube_r1",
         CubeListBuilder.create()
            .texOffs(0, 17)
            .addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.25F))
            .texOffs(0, 0)
            .addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, -1.5708F, -1.1781F, 1.5708F)
      );
      PartDefinition cube_r2 = head.addOrReplaceChild(
         "cube_r2",
         CubeListBuilder.create()
            .texOffs(42, 21)
            .addBox(-6.0F, -1.0F, -4.0F, 6.0F, 3.0F, 8.0F, new CubeDeformation(0.24F))
            .texOffs(0, 39)
            .addBox(-6.0F, -4.0F, -4.0F, 6.0F, 3.0F, 8.0F, new CubeDeformation(0.2F)),
         PartPose.offsetAndRotation(0.0F, -1.5F, 0.0F, 1.5708F, -0.9163F, -1.5708F)
      );
      PartDefinition left_arm = partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create().texOffs(29, 47).addBox(-1.0F, -1.0F, -2.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(4.0F, 1.0F, 0.0F, 0.0F, -1.5708F, 0.0F)
      );
      PartDefinition right_arm = partdefinition.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create().texOffs(29, 47).mirror().addBox(-1.0F, -1.0F, -2.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(-4.0F, 1.0F, 0.0F, 0.0F, 1.5708F, 0.0F)
      );
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create()
            .texOffs(33, 0)
            .addBox(-2.0F, 0.0F, -4.0F, 4.0F, 12.0F, 8.0F, new CubeDeformation(0.23F))
            .texOffs(25, 26)
            .addBox(-2.0F, 0.0F, -4.0F, 4.0F, 12.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F)
      );
      PartDefinition left_leg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create()
            .texOffs(50, 33)
            .addBox(-0.9F, 5.0F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(46, 43)
            .addBox(-1.9F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(1.9F, 12.0F, 0.0F, 0.0F, -1.5708F, 0.0F)
      );
      PartDefinition right_leg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create()
            .texOffs(50, 33)
            .mirror()
            .addBox(-1.1F, 5.0F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
            .mirror(false)
            .texOffs(46, 43)
            .mirror()
            .addBox(-2.1F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
            .mirror(false),
         PartPose.offsetAndRotation(-1.9F, 12.0F, 0.0F, 0.0F, 1.5708F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }
}
