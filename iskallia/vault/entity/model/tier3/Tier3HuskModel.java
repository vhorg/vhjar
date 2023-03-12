package iskallia.vault.entity.model.tier3;

import iskallia.vault.entity.entity.tier3.Tier3HuskEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class Tier3HuskModel extends ZombieModel<Tier3HuskEntity> {
   public Tier3HuskModel(ModelPart root) {
      super(root);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create()
            .texOffs(24, 16)
            .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(36, 32)
            .addBox(-3.5F, 0.0F, -1.0F, 7.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(24, 0)
            .addBox(-3.0F, 2.0F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(24, 0)
            .addBox(1.0F, 2.0F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(24, 0)
            .addBox(1.0F, 4.0F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(24, 0)
            .addBox(1.0F, 6.0F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(24, 0)
            .addBox(-3.0F, 6.0F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(24, 0)
            .addBox(-3.0F, 4.0F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition cube_r1 = body.addOrReplaceChild(
         "cube_r1",
         CubeListBuilder.create().texOffs(0, 16).addBox(-6.0F, -10.0F, 3.75F, 12.0F, 20.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 10.0F, 2.0F, 0.3927F, 0.0F, 0.0F)
      );
      partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(32, 0)
            .addBox(-3.0F, -9.0F, -5.0F, 6.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition cube_r2 = head.addOrReplaceChild(
         "cube_r2",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(3.0F, -2.25F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(0, 6)
            .addBox(3.0F, -2.25F, -2.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 6)
            .addBox(-5.0F, -2.25F, -2.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-5.0F, -2.25F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(32, 12)
            .addBox(-5.0F, 1.75F, -1.0F, 10.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -8.75F, -5.0F, 0.7854F, 0.0F, 0.0F)
      );
      PartDefinition right_arm = partdefinition.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create()
            .texOffs(0, 36)
            .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(36, 44)
            .addBox(-4.0F, 4.0F, -1.5F, 2.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-5.0F, 2.0F, 0.0F)
      );
      PartDefinition left_arm = partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create()
            .texOffs(0, 36)
            .mirror()
            .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
            .mirror(false)
            .texOffs(36, 44)
            .addBox(2.0F, 4.0F, -1.5F, 2.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offset(5.0F, 2.0F, 0.0F)
      );
      PartDefinition right_leg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create().texOffs(20, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-1.9F, 12.0F, 0.0F)
      );
      PartDefinition left_leg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create().texOffs(20, 32).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offset(1.9F, 12.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 64, 64);
   }
}
