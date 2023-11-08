package iskallia.vault.entity.model.bloodmoon;

import iskallia.vault.entity.entity.bloodmoon.Tier4BloodSkeletonEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class Tier4BloodSkeletonModel extends SkeletonModel<Tier4BloodSkeletonEntity> {
   public Tier4BloodSkeletonModel(ModelPart root) {
      super(root);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition hat = partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create()
            .texOffs(0, 17)
            .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.25F))
            .texOffs(0, 0)
            .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, -6.0F, 0.0F)
      );
      PartDefinition left_arm = partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create()
            .texOffs(58, 31)
            .addBox(0.0F, -2.0F, -2.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.1F))
            .texOffs(17, 66)
            .addBox(1.0F, -2.0F, -1.0F, 2.0F, 14.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(5.0F, -4.0F, 0.0F)
      );
      PartDefinition right_arm = partdefinition.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create()
            .texOffs(58, 31)
            .mirror()
            .addBox(-4.0F, -2.0F, -2.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.1F))
            .mirror(false)
            .texOffs(17, 66)
            .mirror()
            .addBox(-3.0F, -2.0F, -1.0F, 2.0F, 14.0F, 2.0F, new CubeDeformation(0.0F))
            .mirror(false),
         PartPose.offset(-5.0F, -4.0F, 0.0F)
      );
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create()
            .texOffs(29, 30)
            .addBox(-5.0F, 0.0F, -2.0F, 10.0F, 16.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(28, 12)
            .addBox(-5.5F, 11.0F, -2.5F, 11.0F, 5.0F, 5.0F, new CubeDeformation(0.1F)),
         PartPose.offset(0.0F, -6.0F, 0.0F)
      );
      PartDefinition armour_r1 = body.addOrReplaceChild(
         "armour_r1",
         CubeListBuilder.create()
            .texOffs(0, 49)
            .addBox(-5.0F, 0.0F, -3.0F, 5.0F, 8.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(0, 34)
            .addBox(-5.0F, 8.0F, -3.0F, 5.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-1.0F, 14.0F, 0.0F, 0.0F, 0.0F, 0.3927F)
      );
      PartDefinition armour_r2 = body.addOrReplaceChild(
         "armour_r2",
         CubeListBuilder.create().texOffs(55, 17).addBox(-5.0F, -1.0F, -3.0F, 5.0F, 7.0F, 6.0F, new CubeDeformation(0.1F)),
         PartPose.offsetAndRotation(-1.0F, 14.0F, 0.0F, 0.0F, 0.0F, 0.7854F)
      );
      PartDefinition armour_r3 = body.addOrReplaceChild(
         "armour_r3",
         CubeListBuilder.create()
            .texOffs(23, 51)
            .addBox(0.0F, 8.0F, -3.0F, 5.0F, 8.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(46, 51)
            .addBox(0.0F, 0.0F, -3.0F, 5.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(1.0F, 14.0F, 0.0F, 0.0F, 0.0F, -0.3927F)
      );
      PartDefinition armour_r4 = body.addOrReplaceChild(
         "armour_r4",
         CubeListBuilder.create().texOffs(56, 0).addBox(0.0F, -1.0F, -3.0F, 5.0F, 7.0F, 6.0F, new CubeDeformation(0.1F)),
         PartPose.offsetAndRotation(1.0F, 14.0F, 0.0F, 0.0F, 0.0F, -0.7854F)
      );
      PartDefinition armour_r5 = body.addOrReplaceChild(
         "armour_r5",
         CubeListBuilder.create().texOffs(26, 66).addBox(-2.0F, -2.0F, -1.5F, 4.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 14.0F, -2.5F, 0.0F, 0.0F, -0.7854F)
      );
      PartDefinition left_leg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create()
            .texOffs(0, 64)
            .addBox(-1.9F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(33, 0)
            .addBox(-1.9F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.25F)),
         PartPose.offset(2.9F, 10.0F, 0.0F)
      );
      PartDefinition right_leg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create()
            .texOffs(0, 64)
            .mirror()
            .addBox(-2.1F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.0F))
            .mirror(false)
            .texOffs(33, 0)
            .mirror()
            .addBox(-2.1F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.25F))
            .mirror(false),
         PartPose.offset(-2.9F, 10.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }
}
