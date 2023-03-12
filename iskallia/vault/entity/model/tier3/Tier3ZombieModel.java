package iskallia.vault.entity.model.tier3;

import iskallia.vault.entity.entity.tier2.Tier3ZombieEntity;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class Tier3ZombieModel extends ZombieModel<Tier3ZombieEntity> {
   public Tier3ZombieModel(ModelPart root) {
      super(root);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create()
            .texOffs(0, 32)
            .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-5.0F, 4.0F, -4.0F, 10.0F, 9.0F, 7.0F, new CubeDeformation(0.0F))
            .texOffs(48, 26)
            .addBox(-4.5F, 3.0F, -5.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(32, 16)
            .addBox(-4.0F, 5.0F, -6.0F, 8.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(27, 27)
            .addBox(-4.0F, 0.0F, 2.0F, 8.0F, 9.0F, 5.0F, new CubeDeformation(0.0F))
            .texOffs(12, 48)
            .addBox(-5.5F, 5.5F, -5.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create()
            .texOffs(0, 16)
            .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(27, 0)
            .addBox(3.0F, -6.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(24, 20)
            .addBox(3.0F, -3.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(3.75F, -3.5F, 1.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(2, 81)
            .addBox(-3.5F, -7.75F, -3.5F, 7.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition right_arm = partdefinition.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create()
            .texOffs(0, 48)
            .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(24, 16)
            .addBox(-4.0F, 1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(0, 20)
            .addBox(-3.5F, 4.0F, -2.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-5.0F, 2.0F, 0.0F)
      );
      PartDefinition left_arm = partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create()
            .texOffs(0, 16)
            .addBox(1.5F, 6.0F, 0.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(50, 4)
            .addBox(2.0F, -1.0F, -2.75F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(40, 41)
            .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(5.0F, 2.0F, 0.0F)
      );
      PartDefinition right_leg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create().texOffs(24, 41).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-1.9F, 12.0F, 0.0F)
      );
      PartDefinition left_leg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create().texOffs(34, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(1.9F, 12.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }
}
