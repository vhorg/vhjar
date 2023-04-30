package iskallia.vault.entity.model.deep_dark;

import iskallia.vault.entity.entity.deepdark.DeepDarkPiglinEntity;
import net.minecraft.client.model.PiglinModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class DeepDarkPiglinModel extends PiglinModel<DeepDarkPiglinEntity> {
   public DeepDarkPiglinModel(ModelPart root) {
      super(root);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = PlayerModel.createMesh(CubeDeformation.NONE, false);
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
      partdefinition.addOrReplaceChild("ear", CubeListBuilder.create(), PartPose.ZERO);
      partdefinition.addOrReplaceChild("cloak", CubeListBuilder.create(), PartPose.ZERO);
      partdefinition.addOrReplaceChild("left_sleeve", CubeListBuilder.create(), PartPose.ZERO);
      partdefinition.addOrReplaceChild("right_sleeve", CubeListBuilder.create(), PartPose.ZERO);
      partdefinition.addOrReplaceChild("left_pants", CubeListBuilder.create(), PartPose.ZERO);
      partdefinition.addOrReplaceChild("right_pants", CubeListBuilder.create(), PartPose.ZERO);
      partdefinition.addOrReplaceChild("jacket", CubeListBuilder.create(), PartPose.ZERO);
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create()
            .texOffs(25, 17)
            .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(0, 17)
            .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-5.0F, -8.0F, -4.0F, 10.0F, 8.0F, 8.0F, new CubeDeformation(-0.02F))
            .texOffs(51, 40)
            .addBox(-2.0F, -4.0F, -5.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 4)
            .addBox(2.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-3.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition left_ear = head.addOrReplaceChild(
         "left_ear",
         CubeListBuilder.create().texOffs(34, 51).addBox(-1.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(5.0F, -6.0F, 0.0F, 0.0F, 0.0F, -0.5236F)
      );
      PartDefinition right_ear = head.addOrReplaceChild(
         "right_ear",
         CubeListBuilder.create().texOffs(51, 30).addBox(0.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-5.0F, -6.0F, 0.0F, 0.0F, 0.0F, 0.5236F)
      );
      PartDefinition right_arm = partdefinition.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create()
            .texOffs(17, 51)
            .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(0, 51)
            .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
         PartPose.offset(-5.0F, 2.0F, 0.0F)
      );
      PartDefinition left_arm = partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create()
            .texOffs(50, 13)
            .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(47, 47)
            .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
         PartPose.offset(5.0F, 2.0F, 0.0F)
      );
      PartDefinition right_leg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create()
            .texOffs(37, 0)
            .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(34, 34)
            .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
         PartPose.offset(-2.0F, 12.0F, 0.0F)
      );
      PartDefinition left_leg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create()
            .texOffs(17, 34)
            .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(0, 34)
            .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
         PartPose.offset(2.0F, 12.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }
}
