package iskallia.vault.entity.model.tier3;

import iskallia.vault.entity.entity.tier3.Tier3SkeletonEntity;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class Tier3SkeletonModel extends SkeletonModel<Tier3SkeletonEntity> {
   public Tier3SkeletonModel(ModelPart root) {
      super(root);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create()
            .texOffs(34, 33)
            .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(39, 17)
            .addBox(-4.0F, 11.0F, -2.0F, 8.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-1.0F, 6.0F, 0.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(21, 21)
            .addBox(4.0F, -2.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(29, 0)
            .addBox(-10.0F, -2.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition cube_r1 = body.addOrReplaceChild(
         "cube_r1",
         CubeListBuilder.create().texOffs(0, 40).addBox(-3.0F, 2.0F, 0.0F, 2.0F, 18.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.4232F, -0.1096F, 0.2382F)
      );
      PartDefinition cube_r2 = body.addOrReplaceChild(
         "cube_r2",
         CubeListBuilder.create().texOffs(0, 40).addBox(1.0F, 2.0F, 0.0F, 2.0F, 18.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.4232F, 0.1096F, -0.2382F)
      );
      partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
      PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      PartDefinition head_r1 = head.addOrReplaceChild(
         "head_r1",
         CubeListBuilder.create()
            .texOffs(0, 21)
            .addBox(-2.0F, -5.0F, -4.5F, 4.0F, 4.0F, 13.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-4.0F, -4.0F, -6.5F, 8.0F, 8.0F, 13.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -4.0F, 2.5F, 0.2182F, 0.0F, 0.0F)
      );
      PartDefinition right_arm = partdefinition.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create().texOffs(24, 40).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 14.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-5.0F, 2.0F, 0.0F)
      );
      PartDefinition left_arm = partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create().texOffs(24, 42).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 14.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(5.0F, 2.0F, 0.0F)
      );
      PartDefinition leftItem = left_arm.addOrReplaceChild("leftItem", CubeListBuilder.create(), PartPose.offset(1.0F, 7.0F, 1.0F));
      PartDefinition right_leg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create().texOffs(0, 40).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 13.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-2.0F, 12.0F, 0.0F)
      );
      PartDefinition left_leg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create().texOffs(16, 40).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 13.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(2.0F, 12.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 64, 64);
   }
}
