package iskallia.vault.entity.model.tier3;

import iskallia.vault.entity.entity.tier3.Tier3PiglinEntity;
import net.minecraft.client.model.PiglinModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class Tier3PiglinModel extends PiglinModel<Tier3PiglinEntity> {
   public Tier3PiglinModel(ModelPart root) {
      super(root);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create()
            .texOffs(0, 16)
            .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(24, 28)
            .addBox(-4.0F, 5.0F, -5.0F, 8.0F, 7.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(24, 16)
            .addBox(-4.0F, 0.0F, 1.0F, 8.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(44, 35)
            .addBox(-3.0F, 8.0F, 1.0F, 6.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(46, 28)
            .addBox(-4.0F, 1.0F, -3.0F, 8.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
      partdefinition.addOrReplaceChild("ear", CubeListBuilder.create(), PartPose.ZERO);
      partdefinition.addOrReplaceChild("cloak", CubeListBuilder.create(), PartPose.ZERO);
      partdefinition.addOrReplaceChild("left_sleeve", CubeListBuilder.create(), PartPose.ZERO);
      partdefinition.addOrReplaceChild("right_sleeve", CubeListBuilder.create(), PartPose.ZERO);
      partdefinition.addOrReplaceChild("left_pants", CubeListBuilder.create(), PartPose.ZERO);
      partdefinition.addOrReplaceChild("right_pants", CubeListBuilder.create(), PartPose.ZERO);
      partdefinition.addOrReplaceChild("jacket", CubeListBuilder.create(), PartPose.ZERO);
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-5.0F, -8.0F, -4.0F, 10.0F, 8.0F, 8.0F, new CubeDeformation(-0.02F))
            .texOffs(48, 21)
            .addBox(-2.0F, -4.0F, -5.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 3)
            .addBox(2.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-3.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition left_ear = head.addOrReplaceChild(
         "left_ear",
         CubeListBuilder.create().texOffs(48, 12).addBox(-1.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(5.0F, -6.0F, 0.0F, 0.0F, 0.0F, -0.5236F)
      );
      PartDefinition right_ear = head.addOrReplaceChild(
         "right_ear",
         CubeListBuilder.create().texOffs(0, 48).addBox(0.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-5.0F, -6.0F, 0.0F, 0.0F, 0.0F, 0.5236F)
      );
      PartDefinition right_arm = partdefinition.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create().texOffs(16, 38).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-5.0F, 2.0F, 0.0F)
      );
      PartDefinition left_arm = partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create().texOffs(16, 38).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(5.0F, 2.0F, 0.0F)
      );
      PartDefinition right_leg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create().texOffs(36, 0).addBox(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-1.9F, 12.0F, 0.0F)
      );
      PartDefinition left_leg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create().texOffs(0, 32).addBox(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(1.9F, 12.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 64, 64);
   }
}
