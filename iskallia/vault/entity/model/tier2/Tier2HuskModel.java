package iskallia.vault.entity.model.tier2;

import iskallia.vault.entity.entity.tier2.Tier2HuskEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class Tier2HuskModel extends ZombieModel<Tier2HuskEntity> {
   public Tier2HuskModel(ModelPart root) {
      super(root);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(32, 0)
            .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition right_arm = partdefinition.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create()
            .texOffs(40, 16)
            .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(11, 38)
            .addBox(-2.0F, 4.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-5.0F, 2.0F, 0.0F)
      );
      PartDefinition left_arm = partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create()
            .texOffs(11, 38)
            .addBox(0.0F, 4.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(40, 16)
            .mirror()
            .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
            .mirror(false),
         PartPose.offset(5.0F, 2.0F, 0.0F)
      );
      PartDefinition right_leg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-1.9F, 12.0F, 0.0F)
      );
      PartDefinition left_leg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offset(1.9F, 12.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 64, 64);
   }
}
