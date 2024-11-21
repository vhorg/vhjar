package iskallia.vault.entity.model.plastic;

import iskallia.vault.entity.entity.plastic.PlasticSlimeEntity;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class PlasticSlimeModel extends SlimeModel<PlasticSlimeEntity> {
   public PlasticSlimeModel(ModelPart root) {
      super(root);
   }

   public static LayerDefinition createOuterLayer() {
      MeshDefinition meshDefinition = new MeshDefinition();
      PartDefinition partDefinition = meshDefinition.getRoot();
      partDefinition.addOrReplaceChild("cube", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 16.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
      return LayerDefinition.create(meshDefinition, 64, 32);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition cube = partdefinition.addOrReplaceChild(
         "cube", CubeListBuilder.create().texOffs(0, 16).addBox(-3.0F, 17.0F, -3.0F, 6.0F, 6.0F, 6.0F), PartPose.ZERO
      );
      PartDefinition eye0 = partdefinition.addOrReplaceChild(
         "right_eye", CubeListBuilder.create().texOffs(32, 0).addBox(-3.3F, 18.0F, -3.5F, 2.0F, 2.0F, 2.0F), PartPose.ZERO
      );
      PartDefinition eye1 = partdefinition.addOrReplaceChild(
         "left_eye", CubeListBuilder.create().texOffs(32, 4).addBox(1.3F, 18.0F, -3.5F, 2.0F, 2.0F, 2.0F), PartPose.ZERO
      );
      PartDefinition mouth = partdefinition.addOrReplaceChild(
         "mouth", CubeListBuilder.create().texOffs(33, 10).addBox(-1.5F, 20.025F, -3.5F, 3.0F, 3.0F, 1.0F), PartPose.ZERO
      );
      return LayerDefinition.create(meshdefinition, 64, 32);
   }
}
