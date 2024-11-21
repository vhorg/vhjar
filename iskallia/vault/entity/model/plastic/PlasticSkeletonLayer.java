package iskallia.vault.entity.model.plastic;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class PlasticSkeletonLayer {
   public static LayerDefinition createDefaultBodyLayer() {
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition root = mesh.getRoot();
      PartDefinition head = root.addOrReplaceChild(
         "head", CubeListBuilder.create().texOffs(33, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition hat = root.addOrReplaceChild(
         "hat",
         CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.1F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition body = root.addOrReplaceChild(
         "body", CubeListBuilder.create().texOffs(25, 17).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F), PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition right_arm = root.addOrReplaceChild(
         "right_arm", CubeListBuilder.create().texOffs(9, 50).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(-5.0F, 1.0F, 0.0F)
      );
      PartDefinition rightItem = right_arm.addOrReplaceChild("rightItem", CubeListBuilder.create(), PartPose.offset(0.5F, 10.0F, 1.0F));
      PartDefinition left_arm = root.addOrReplaceChild(
         "left_arm", CubeListBuilder.create().texOffs(0, 50).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(5.0F, 1.0F, 0.0F)
      );
      PartDefinition leftItem = left_arm.addOrReplaceChild("leftItem", CubeListBuilder.create(), PartPose.offset(-0.5F, 10.0F, 1.0F));
      PartDefinition right_leg = root.addOrReplaceChild(
         "right_leg", CubeListBuilder.create().texOffs(34, 34).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(-2.0F, 12.0F, 0.0F)
      );
      PartDefinition left_leg = root.addOrReplaceChild(
         "left_leg", CubeListBuilder.create().texOffs(25, 34).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(2.0F, 12.0F, 0.0F)
      );
      PartDefinition clothes = body.addOrReplaceChild(
         "clothes",
         CubeListBuilder.create()
            .texOffs(0, 34)
            .addBox(-4.0F, 10.0F, -2.0F, 8.0F, 8.0F, 4.0F, new CubeDeformation(0.1F))
            .texOffs(0, 17)
            .addBox(-4.0F, 0.175F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.2F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      return LayerDefinition.create(mesh, 128, 128);
   }

   public static LayerDefinition createTier4BodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create().texOffs(2, 23).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, -6.0F, 0.0F)
      );
      PartDefinition hat = partdefinition.addOrReplaceChild(
         "hat",
         CubeListBuilder.create().texOffs(3, 3).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.1F)),
         PartPose.offset(0.0F, -6.0F, 0.0F)
      );
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create().texOffs(35, 37).addBox(-5.0F, 0.0F, -2.5F, 10.0F, 15.0F, 5.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, -6.0F, 0.0F)
      );
      PartDefinition right_arm = partdefinition.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create().texOffs(56, 16).addBox(-1.5F, -1.5F, -1.0F, 3.0F, 14.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-6.5F, -3.5F, 0.0F)
      );
      PartDefinition rightItem = right_arm.addOrReplaceChild("rightItem", CubeListBuilder.create(), PartPose.offset(0.5F, 11.0F, 1.0F));
      PartDefinition left_arm = partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create().texOffs(18, 43).addBox(-1.5F, -1.5F, -1.0F, 3.0F, 14.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offset(6.5F, -3.5F, 0.0F)
      );
      PartDefinition leftItem = left_arm.addOrReplaceChild("leftItem", CubeListBuilder.create(), PartPose.offset(-0.5F, 11.0F, 1.0F));
      PartDefinition right_leg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create().texOffs(2, 43).addBox(-1.5F, 0.0F, -1.0F, 3.0F, 15.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-2.5F, 9.0F, 0.0F)
      );
      PartDefinition left_leg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create().texOffs(41, 0).addBox(-1.5F, 0.0F, -1.0F, 3.0F, 15.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offset(2.5F, 9.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }
}
