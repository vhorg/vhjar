package iskallia.vault.entity.model.mushroom;

import iskallia.vault.entity.entity.mushroom.Tier5MushroomEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class Tier5MushroomModel extends HumanoidModel<Tier5MushroomEntity> {
   public Tier5MushroomModel(ModelPart root) {
      super(root);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition hat = partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create().texOffs(0, 25).addBox(-4.0F, -20.0F, -4.0F, 8.0F, 20.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition left_arm = partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create()
            .texOffs(50, 42)
            .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(56, 59)
            .addBox(2.0F, 0.0F, -3.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.01F))
            .texOffs(17, 59)
            .addBox(-1.0F, 6.0F, -4.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.01F))
            .texOffs(43, 59)
            .addBox(1.0F, 3.0F, 1.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.01F))
            .texOffs(30, 59)
            .addBox(2.0F, 4.0F, -3.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.011F)),
         PartPose.offset(5.0F, 2.0F, 0.0F)
      );
      PartDefinition right_arm = partdefinition.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(58, 35)
            .addBox(-5.0F, 0.0F, -3.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.01F))
            .texOffs(58, 30)
            .addBox(-2.0F, 6.0F, -4.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.01F))
            .texOffs(58, 25)
            .addBox(-4.0F, 3.0F, 1.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.01F))
            .texOffs(17, 54)
            .addBox(-5.0F, 4.0F, -3.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.011F)),
         PartPose.offset(-5.0F, 2.0F, 0.0F)
      );
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create()
            .texOffs(33, 25)
            .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-10.0F, 0.0F, -10.0F, 20.0F, 4.0F, 20.0F, new CubeDeformation(0.05F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition left_leg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create().texOffs(0, 54).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(2.0F, 12.0F, 0.0F)
      );
      PartDefinition right_leg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create().texOffs(33, 42).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-2.0F, 12.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }
}
