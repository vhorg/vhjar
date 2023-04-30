package iskallia.vault.entity.model.winterwalker;

import iskallia.vault.entity.entity.winterwalker.Tier2WinterwalkerEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class Tier2WinterwalkerModel extends SkeletonModel<Tier2WinterwalkerEntity> {
   public Tier2WinterwalkerModel(ModelPart root) {
      super(root);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition hat = partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create()
            .texOffs(25, 29)
            .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.25F))
            .texOffs(21, 0)
            .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(0, 20)
            .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.4F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition spear3_r1 = head.addOrReplaceChild(
         "spear3_r1",
         CubeListBuilder.create().texOffs(0, 20).addBox(-0.5F, -2.7678F, -2.7678F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.1F)),
         PartPose.offsetAndRotation(0.0126F, -6.2019F, -4.5481F, 1.0326F, -0.673F, 0.1884F)
      );
      PartDefinition spear3_r2 = head.addOrReplaceChild(
         "spear3_r2",
         CubeListBuilder.create()
            .texOffs(51, 20)
            .addBox(0.0F, -1.5F, -10.5F, 0.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-0.5F, -0.5F, -2.5F, 1.0F, 1.0F, 18.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0126F, -6.2019F, -4.5481F, 0.2472F, -0.673F, 0.1884F)
      );
      PartDefinition left_arm = partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create().texOffs(0, 54).addBox(-1.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(5.0F, 0.0F, 0.0F)
      );
      PartDefinition cube_r1 = left_arm.addOrReplaceChild(
         "cube_r1",
         CubeListBuilder.create().texOffs(25, 46).addBox(-3.25F, -2.25F, -2.5F, 7.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(1.5F, -0.5F, 0.0F, 0.0F, 0.0F, 0.3927F)
      );
      PartDefinition right_arm = partdefinition.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create().texOffs(50, 46).addBox(-3.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-5.0F, 0.0F, 0.0F)
      );
      PartDefinition cube_r2 = right_arm.addOrReplaceChild(
         "cube_r2",
         CubeListBuilder.create().texOffs(34, 17).addBox(-3.75F, -2.25F, -2.5F, 7.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-1.5F, -0.5F, 0.0F, 0.0F, 0.0F, -0.3927F)
      );
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create()
            .texOffs(0, 37)
            .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(50, 32)
            .addBox(-5.0F, 1.0F, -2.25F, 10.0F, 3.0F, 0.0F, new CubeDeformation(0.0F))
            .texOffs(25, 20)
            .addBox(-1.0F, 3.0F, -1.75F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition spear2_r1 = body.addOrReplaceChild(
         "spear2_r1",
         CubeListBuilder.create().texOffs(0, 20).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.1F)),
         PartPose.offsetAndRotation(1.0558F, 4.682F, -4.682F, 1.2069F, 0.3614F, 0.1572F)
      );
      PartDefinition spear2_r2 = body.addOrReplaceChild(
         "spear2_r2",
         CubeListBuilder.create()
            .texOffs(51, 20)
            .addBox(0.0F, -1.5F, -17.0F, 0.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-0.5F, -0.5F, -9.0F, 1.0F, 1.0F, 18.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(4.5F, 1.5F, 3.0F, 0.4215F, 0.3614F, 0.1572F)
      );
      PartDefinition spear1_r1 = body.addOrReplaceChild(
         "spear1_r1",
         CubeListBuilder.create().texOffs(0, 20).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.1F)),
         PartPose.offsetAndRotation(-2.9442F, 7.682F, -4.682F, 1.2069F, 0.3614F, 0.1572F)
      );
      PartDefinition spear1_r2 = body.addOrReplaceChild(
         "spear1_r2",
         CubeListBuilder.create()
            .texOffs(51, 20)
            .addBox(0.0F, -1.5F, -17.0F, 0.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-0.5F, -0.5F, -9.0F, 1.0F, 1.0F, 18.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.5F, 4.5F, 3.0F, 0.4215F, 0.3614F, 0.1572F)
      );
      PartDefinition left_leg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create().texOffs(54, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(2.0F, 12.0F, 0.0F)
      );
      PartDefinition right_leg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-2.0F, 12.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }
}
