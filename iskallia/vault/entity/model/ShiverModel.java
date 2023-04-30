package iskallia.vault.entity.model;

import iskallia.vault.entity.entity.ShiverEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class ShiverModel extends ZombieModel<ShiverEntity> {
   public ShiverModel(ModelPart root) {
      super(root);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition hat = partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition cube_r1 = head.addOrReplaceChild(
         "cube_r1",
         CubeListBuilder.create()
            .texOffs(0, 16)
            .addBox(-4.0F, -5.0F, -2.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(24, 25)
            .addBox(3.0F, -5.0F, -2.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 32)
            .addBox(-0.75F, 4.0F, -3.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(12, 32)
            .addBox(-3.25F, 6.0F, -3.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(36, 25)
            .addBox(-3.75F, 4.0F, -3.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(32, 0)
            .addBox(1.75F, 7.0F, -3.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(20, 16)
            .addBox(1.25F, 4.0F, -3.5F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(20, 32)
            .addBox(1.25F, -2.0F, -3.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(25, 19)
            .addBox(-1.5F, -2.0F, -3.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(44, 0)
            .addBox(-2.0F, -1.0F, -3.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(3.0F, -2.0F, -3.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(0, 4)
            .addBox(-5.0F, -2.0F, -3.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(24, 16)
            .addBox(-5.0F, 0.0F, -3.5F, 10.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -2.0F, -2.5F, 0.3491F, 0.0F, 0.0F)
      );
      PartDefinition right_arm = partdefinition.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create()
            .texOffs(36, 37)
            .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(0, 48)
            .addBox(-3.5F, 4.0F, -2.5F, 1.0F, 11.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(8, 48)
            .addBox(-3.5F, 15.0F, -1.5F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(16, 32)
            .addBox(-3.75F, 5.0F, 1.75F, 1.0F, 10.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(48, 36)
            .addBox(-2.25F, 12.0F, -2.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(24, 0)
            .addBox(-4.0F, 5.75F, 0.5F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(40, 25)
            .addBox(-3.25F, 6.0F, -2.75F, 3.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-5.0F, 2.0F, 0.0F)
      );
      PartDefinition left_arm = partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create()
            .texOffs(32, 0)
            .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(48, 0)
            .addBox(3.0F, 4.0F, -2.5F, 1.0F, 11.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(49, 14)
            .addBox(3.0F, 15.0F, -1.5F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(28, 0)
            .addBox(3.0F, 6.0F, 0.5F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(32, 41)
            .addBox(2.75F, 5.0F, 1.75F, 1.0F, 10.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(16, 41)
            .addBox(0.0F, 6.0F, -2.5F, 3.0F, 6.0F, 5.0F, new CubeDeformation(0.0F))
            .texOffs(51, 25)
            .addBox(0.0F, 12.0F, -2.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offset(5.0F, 2.0F, 0.0F)
      );
      PartDefinition right_leg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create().texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-1.9F, 12.0F, 0.0F)
      );
      PartDefinition left_leg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create().texOffs(24, 25).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(1.9F, 12.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 64, 64);
   }
}
