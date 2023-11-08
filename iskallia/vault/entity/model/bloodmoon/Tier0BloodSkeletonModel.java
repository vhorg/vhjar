package iskallia.vault.entity.model.bloodmoon;

import iskallia.vault.entity.entity.bloodmoon.Tier0BloodSkeletonEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class Tier0BloodSkeletonModel extends SkeletonModel<Tier0BloodSkeletonEntity> {
   public Tier0BloodSkeletonModel(ModelPart root) {
      super(root);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition hat = partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 3.0F, 7.0F));
      PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5.0F, 3.0F, 6.0F));
      PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5.0F, 3.0F, 6.0F));
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create()
            .texOffs(29, 30)
            .addBox(-4.0F, 6.0F, -2.0F, 8.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(0, 17)
            .addBox(-4.0F, -2.0F, 7.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.1F))
            .texOffs(0, 0)
            .addBox(-4.0F, -2.0F, 7.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(0, 34)
            .addBox(-6.0F, 2.0F, 5.0F, 2.0F, 15.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(9, 34)
            .addBox(4.0F, 2.0F, 5.0F, 2.0F, 15.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition cube_r1 = body.addOrReplaceChild(
         "cube_r1",
         CubeListBuilder.create().texOffs(29, 13).addBox(-4.0F, -6.0F, -4.0F, 8.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 6.0F, 2.0F, -1.3526F, 0.0F, 0.0F)
      );
      PartDefinition left_leg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create().texOffs(27, 41).addBox(-0.9F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(1.9F, 12.0F, 0.0F)
      );
      PartDefinition right_leg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create().texOffs(18, 34).addBox(-1.1F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-1.9F, 12.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 64, 64);
   }
}
