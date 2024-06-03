package iskallia.vault.entity.model.deep_dark;

import iskallia.vault.entity.entity.deepdark.DeepDarkWitchEntity;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.WitchModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class DeepDarkWitchModel extends WitchModel<DeepDarkWitchEntity> {
   public DeepDarkWitchModel(ModelPart root) {
      super(root);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = VillagerModel.createBodyModel();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create().texOffs(29, 14).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition hat = head.addOrReplaceChild(
         "hat",
         CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -3.0187F, 0.0F, 10.0F, 3.0F, 10.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-5.0F, -8.0313F, -5.0F)
      );
      PartDefinition hat2 = hat.addOrReplaceChild(
         "hat2",
         CubeListBuilder.create().texOffs(0, 45).addBox(-5.0F, -10.5F, -5.0F, 7.0F, 10.0F, 7.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(6.75F, 0.0313F, 7.0F, -0.2618F, 0.0F, 0.0F)
      );
      PartDefinition hat3 = hat2.addOrReplaceChild(
         "hat3",
         CubeListBuilder.create().texOffs(52, 33).addBox(-4.0F, -5.5F, -4.0F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -7.0F, 0.0F, -0.2618F, 0.0F, 0.0F)
      );
      PartDefinition nose = head.addOrReplaceChild(
         "nose", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F), PartPose.offset(0.0F, -2.0F, 0.0F)
      );
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create()
            .texOffs(23, 33)
            .addBox(-4.0F, -24.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(0, 14)
            .addBox(-4.0F, -24.0F, -3.0F, 8.0F, 18.0F, 6.0F, new CubeDeformation(0.5F)),
         PartPose.offset(0.0F, 24.0F, 0.0F)
      );
      PartDefinition arms = partdefinition.addOrReplaceChild(
         "arms",
         CubeListBuilder.create()
            .texOffs(31, 0)
            .addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(29, 52)
            .addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(29, 52)
            .addBox(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.7854F, 0.0F, 0.0F)
      );
      PartDefinition right_leg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create().texOffs(48, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-2.0F, 12.0F, 0.0F)
      );
      PartDefinition left_leg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create().texOffs(48, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(2.0F, 12.0F, 0.0F)
      );
      partdefinition.addOrReplaceChild("jacket", CubeListBuilder.create(), PartPose.ZERO);
      hat.addOrReplaceChild("hat_rim", CubeListBuilder.create(), PartPose.ZERO);
      hat3.addOrReplaceChild("hat4", CubeListBuilder.create(), PartPose.ZERO);
      nose.addOrReplaceChild("mole", CubeListBuilder.create(), PartPose.ZERO);
      return LayerDefinition.create(meshdefinition, 128, 128);
   }
}
