package iskallia.vault.entity.model.mushroom;

import iskallia.vault.entity.entity.mushroom.Tier4MushroomEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class Tier4MushroomModel extends HumanoidModel<Tier4MushroomEntity> {
   public Tier4MushroomModel(ModelPart root) {
      super(root);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition hat = partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -20.0F, -4.0F, 8.0F, 20.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition left_arm = partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create()
            .texOffs(42, 17)
            .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(26, 46)
            .addBox(2.0F, 0.0F, -3.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.01F))
            .texOffs(26, 46)
            .addBox(-1.0F, 6.0F, -4.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.01F))
            .texOffs(13, 46)
            .addBox(1.0F, 3.0F, 1.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.01F))
            .texOffs(0, 46)
            .addBox(2.0F, 4.0F, -3.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.011F))
            .texOffs(0, 46)
            .addBox(0.0F, -2.5F, 0.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.011F)),
         PartPose.offset(5.0F, 2.0F, 0.0F)
      );
      PartDefinition right_arm = partdefinition.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create()
            .texOffs(42, 17)
            .mirror()
            .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
            .mirror(false)
            .texOffs(26, 46)
            .mirror()
            .addBox(-5.0F, 0.0F, -3.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.01F))
            .mirror(false)
            .texOffs(26, 46)
            .mirror()
            .addBox(-2.0F, 6.0F, -4.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.01F))
            .mirror(false)
            .texOffs(13, 46)
            .mirror()
            .addBox(-4.0F, 3.0F, 1.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.01F))
            .mirror(false)
            .texOffs(0, 46)
            .mirror()
            .addBox(-5.0F, 4.0F, -3.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.011F))
            .mirror(false)
            .texOffs(0, 46)
            .mirror()
            .addBox(-3.0F, -2.5F, 0.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.011F))
            .mirror(false),
         PartPose.offset(-5.0F, 2.0F, 0.0F)
      );
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create().texOffs(0, 29).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition left_leg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create().texOffs(42, 34).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(2.0F, 12.0F, 0.0F)
      );
      PartDefinition right_leg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create().texOffs(33, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-2.0F, 12.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 64, 64);
   }
}
