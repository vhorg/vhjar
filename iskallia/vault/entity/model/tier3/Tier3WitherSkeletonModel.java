package iskallia.vault.entity.model.tier3;

import iskallia.vault.entity.entity.tier3.Tier3WitherSkeletonEntity;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class Tier3WitherSkeletonModel extends SkeletonModel<Tier3WitherSkeletonEntity> {
   public Tier3WitherSkeletonModel(ModelPart root) {
      super(root);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create()
            .texOffs(0, 16)
            .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(20, 22)
            .addBox(-4.0F, 11.0F, -2.0F, 8.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-1.0F, 6.0F, 0.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition cube_r1 = body.addOrReplaceChild(
         "cube_r1",
         CubeListBuilder.create()
            .texOffs(43, 39)
            .addBox(-0.5F, -3.0F, -1.5F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
            .texOffs(39, 44)
            .addBox(-0.5F, -3.0F, -2.5F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(4.5F, -5.0F, -4.5F, 0.7854F, -0.7854F, 0.0F)
      );
      PartDefinition cube_r2 = body.addOrReplaceChild(
         "cube_r2",
         CubeListBuilder.create()
            .texOffs(24, 20)
            .addBox(-3.5F, -5.0F, -0.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(8, 44)
            .addBox(0.5F, -5.0F, -0.5F, 1.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(4.5F, -5.0F, 0.5F, 0.0F, 0.0F, 0.7854F)
      );
      PartDefinition cube_r3 = body.addOrReplaceChild(
         "cube_r3",
         CubeListBuilder.create()
            .texOffs(20, 16)
            .addBox(-0.5F, -5.0F, -1.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(12, 44)
            .addBox(-0.5F, -5.0F, 1.5F, 1.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.5F, -5.0F, 4.5F, -0.7854F, 0.0F, 0.0F)
      );
      PartDefinition cube_r4 = body.addOrReplaceChild(
         "cube_r4",
         CubeListBuilder.create()
            .texOffs(36, 0)
            .addBox(-1.5F, -5.0F, -0.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(31, 44)
            .addBox(-2.5F, -5.0F, -0.5F, 1.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-4.5F, -5.0F, 0.5F, 0.0F, 0.0F, -0.7854F)
      );
      PartDefinition cube_r5 = body.addOrReplaceChild(
         "cube_r5",
         CubeListBuilder.create()
            .texOffs(36, 2)
            .addBox(-1.5F, -4.0F, -0.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(35, 44)
            .addBox(-2.5F, -4.0F, -0.5F, 1.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-4.5F, -5.0F, -4.5F, 0.6155F, -0.5236F, -0.9553F)
      );
      partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
      PartDefinition head_r1 = body.addOrReplaceChild(
         "head_r1",
         CubeListBuilder.create().texOffs(28, 12).addBox(-3.0F, -1.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-3.0F, 0.0F, -4.0F, 0.0F, 0.0F, -0.3927F)
      );
      PartDefinition head_r2 = body.addOrReplaceChild(
         "head_r2",
         CubeListBuilder.create().texOffs(24, 0).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(3.0F, 0.0F, -4.0F, -1.1781F, 0.0F, 0.0F)
      );
      PartDefinition head_r3 = body.addOrReplaceChild(
         "head_r3",
         CubeListBuilder.create().texOffs(32, 28).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-3.0F, 0.0F, 3.0F, 0.3655F, 0.7119F, 0.5299F)
      );
      PartDefinition head_r4 = body.addOrReplaceChild(
         "head_r4",
         CubeListBuilder.create().texOffs(36, 4).addBox(-1.0F, -2.0F, -2.25F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(3.0F, 0.0F, 3.0F, 0.1572F, -0.3614F, -0.4215F)
      );
      PartDefinition head_r5 = body.addOrReplaceChild(
         "head_r5",
         CubeListBuilder.create().texOffs(32, 36).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(6.0F, -2.0F, -2.0F, 0.0F, 0.0F, -0.2182F)
      );
      PartDefinition head_r6 = body.addOrReplaceChild(
         "head_r6",
         CubeListBuilder.create().texOffs(40, 16).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-6.0F, -2.0F, -2.0F, 0.3927F, -0.3927F, 0.0F)
      );
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition right_arm = partdefinition.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create().texOffs(0, 26).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 18.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-5.0F, 2.0F, 0.0F)
      );
      PartDefinition left_arm = partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create().texOffs(8, 26).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 16.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(5.0F, 2.0F, 0.0F)
      );
      PartDefinition leftItem = left_arm.addOrReplaceChild("leftItem", CubeListBuilder.create(), PartPose.offset(1.0F, 7.0F, 1.0F));
      PartDefinition right_leg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create().texOffs(24, 28).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 15.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-2.0F, 12.0F, 0.0F)
      );
      PartDefinition left_leg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create().texOffs(16, 28).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 15.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(2.0F, 12.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 64, 64);
   }
}
