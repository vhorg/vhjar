package iskallia.vault.entity.model.mummy;

import iskallia.vault.entity.entity.mummy.Tier2MummyEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class Tier2MummyModel extends ZombieModel<Tier2MummyEntity> {
   public Tier2MummyModel(ModelPart root) {
      super(root);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition hat = partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(24, 30)
            .addBox(-4.0F, -3.0F, -5.0F, 8.0F, 5.0F, 5.0F, new CubeDeformation(0.01F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition cube_r1 = head.addOrReplaceChild(
         "cube_r1",
         CubeListBuilder.create()
            .texOffs(33, 8)
            .addBox(-2.0F, 1.0F, -0.25F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(51, 43)
            .addBox(-2.5F, -3.0F, -1.0F, 5.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -8.0F, -4.0F, -0.3927F, 0.0F, 0.0F)
      );
      PartDefinition left_arm = partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create()
            .texOffs(34, 41)
            .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(0, 52)
            .addBox(-1.0F, -4.0F, -2.5F, 1.0F, 5.0F, 5.0F, new CubeDeformation(0.01F))
            .texOffs(50, 10)
            .addBox(0.0F, 0.0F, -2.5F, 4.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
            .texOffs(13, 58)
            .addBox(0.0F, -3.0F, -2.0F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.02F))
            .texOffs(60, 58)
            .addBox(2.0F, 5.0F, -2.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.25F)),
         PartPose.offset(5.0F, 2.0F, 0.0F)
      );
      PartDefinition right_arm = partdefinition.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create()
            .texOffs(34, 41)
            .mirror()
            .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
            .mirror(false)
            .texOffs(0, 52)
            .mirror()
            .addBox(0.0F, -4.0F, -2.5F, 1.0F, 5.0F, 5.0F, new CubeDeformation(0.01F))
            .mirror(false)
            .texOffs(50, 10)
            .mirror()
            .addBox(-4.0F, 0.0F, -2.5F, 4.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
            .mirror(false)
            .texOffs(13, 58)
            .mirror()
            .addBox(-3.0F, -3.0F, -2.0F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.02F))
            .mirror(false)
            .texOffs(60, 58)
            .mirror()
            .addBox(-4.0F, 5.0F, -2.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.25F))
            .mirror(false),
         PartPose.offset(-5.0F, 2.0F, 0.0F)
      );
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create()
            .texOffs(29, 13)
            .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(0, 17)
            .addBox(-4.5F, 0.0F, -2.5F, 9.0F, 12.0F, 5.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition left_leg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create().texOffs(51, 26).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(2.0F, 12.0F, 0.0F)
      );
      PartDefinition right_leg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create().texOffs(17, 41).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-2.0F, 12.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }
}
