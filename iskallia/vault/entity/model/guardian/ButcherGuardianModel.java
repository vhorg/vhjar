package iskallia.vault.entity.model.guardian;

import iskallia.vault.entity.entity.guardian.ButcherGuardianEntity;
import net.minecraft.client.model.PiglinModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class ButcherGuardianModel {
   public static class Arbalist extends PiglinModel<ButcherGuardianEntity> {
      public Arbalist(ModelPart root) {
         super(root);
      }

      public static LayerDefinition createBodyLayer() {
         MeshDefinition meshdefinition = PiglinModel.createMesh(CubeDeformation.NONE);
         PartDefinition partdefinition = meshdefinition.getRoot();
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create().texOffs(0, 34).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition jacket = partdefinition.addOrReplaceChild(
            "jacket",
            CubeListBuilder.create().texOffs(71, 2).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition buckle = jacket.addOrReplaceChild(
            "buckle",
            CubeListBuilder.create()
               .texOffs(92, 0)
               .addBox(-3.0F, -5.0F, 0.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(101, 0)
               .addBox(0.0F, -5.0F, 0.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(87, 31)
               .addBox(-2.0F, -5.0F, 0.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(92, 35)
               .addBox(-2.0F, -2.0F, 0.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(1.0F, 12.75F, -2.375F)
         );
         PartDefinition hat = partdefinition.addOrReplaceChild(
            "hat",
            CubeListBuilder.create().texOffs(0, 17).addBox(-5.0F, -8.0F, -4.0F, 10.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition head = partdefinition.addOrReplaceChild(
            "head",
            CubeListBuilder.create()
               .texOffs(0, 0)
               .addBox(-5.0F, -8.0F, -4.0F, 10.0F, 8.0F, 8.0F, new CubeDeformation(-0.02F))
               .texOffs(55, 0)
               .addBox(-1.0F, -4.0F, -5.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(54, 6)
               .addBox(2.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(54, 10)
               .addBox(-2.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition left_ear = head.addOrReplaceChild(
            "left_ear",
            CubeListBuilder.create().texOffs(34, 51).addBox(-1.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.0F, -6.0F, 0.0F, 0.0F, 0.0F, -0.5236F)
         );
         PartDefinition right_ear = head.addOrReplaceChild(
            "right_ear",
            CubeListBuilder.create()
               .texOffs(70, 47)
               .addBox(0.0F, 0.0F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(60, 58)
               .addBox(0.0F, 4.0F, -2.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.0F, -6.0F, 0.0F, 0.0F, 0.0F, 0.5236F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create().texOffs(25, 34).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition right_sleeve = partdefinition.addOrReplaceChild(
            "right_sleeve",
            CubeListBuilder.create().texOffs(37, 0).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create().texOffs(37, 17).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition left_sleeve = partdefinition.addOrReplaceChild(
            "left_sleeve",
            CubeListBuilder.create()
               .texOffs(42, 34)
               .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F))
               .texOffs(105, 56)
               .addBox(-1.5F, 5.0F, -2.5F, 5.0F, 4.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-1.0F, 11.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(0, 51).addBox(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition right_pants = partdefinition.addOrReplaceChild(
            "right_pants",
            CubeListBuilder.create().texOffs(22, 71).addBox(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(17, 51).addBox(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_pants = partdefinition.addOrReplaceChild(
            "left_pants",
            CubeListBuilder.create().texOffs(0, 72).addBox(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }

   public static class Bruiser extends PiglinModel<ButcherGuardianEntity> {
      public Bruiser(ModelPart root) {
         super(root);
      }

      public static LayerDefinition createBodyLayer() {
         MeshDefinition meshdefinition = PiglinModel.createMesh(CubeDeformation.NONE);
         PartDefinition partdefinition = meshdefinition.getRoot();
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create().texOffs(0, 34).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition jacket = partdefinition.addOrReplaceChild(
            "jacket",
            CubeListBuilder.create().texOffs(71, 2).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition apron = jacket.addOrReplaceChild(
            "apron",
            CubeListBuilder.create()
               .texOffs(2, 108)
               .addBox(-4.5F, -15.0F, -2.625F, 9.0F, 13.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(112, 86)
               .addBox(2.5F, -24.0F, -2.625F, 1.0F, 9.0F, 0.0F, new CubeDeformation(0.0F))
               .texOffs(112, 86)
               .addBox(-3.5F, -24.0F, -2.625F, 1.0F, 9.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 24.0F, 0.0F)
         );
         PartDefinition buckle = jacket.addOrReplaceChild(
            "buckle",
            CubeListBuilder.create()
               .texOffs(92, 0)
               .addBox(-2.0F, -16.25F, -2.375F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(101, 0)
               .addBox(1.0F, -16.25F, -2.375F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(87, 31)
               .addBox(-1.0F, -16.25F, -2.375F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(92, 35)
               .addBox(-1.0F, -13.25F, -2.375F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 24.0F, 0.0F)
         );
         PartDefinition hat = partdefinition.addOrReplaceChild(
            "hat",
            CubeListBuilder.create().texOffs(0, 17).addBox(-5.0F, -8.0F, -4.0F, 10.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition head = partdefinition.addOrReplaceChild(
            "head",
            CubeListBuilder.create()
               .texOffs(0, 0)
               .addBox(-5.0F, -8.0F, -4.0F, 10.0F, 8.0F, 8.0F, new CubeDeformation(-0.02F))
               .texOffs(55, 0)
               .addBox(-1.0F, -4.0F, -5.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(54, 6)
               .addBox(2.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(54, 10)
               .addBox(-2.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition left_ear = head.addOrReplaceChild(
            "left_ear",
            CubeListBuilder.create().texOffs(34, 51).addBox(-1.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.0F, -6.0F, 0.0F, 0.0F, 0.0F, -0.5236F)
         );
         PartDefinition right_ear = head.addOrReplaceChild(
            "right_ear",
            CubeListBuilder.create()
               .texOffs(70, 47)
               .addBox(0.0F, 0.0F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(60, 58)
               .addBox(0.0F, 4.0F, -2.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.0F, -6.0F, 0.0F, 0.0F, 0.0F, 0.5236F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create().texOffs(25, 34).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition right_sleeve = partdefinition.addOrReplaceChild(
            "right_sleeve",
            CubeListBuilder.create().texOffs(37, 0).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create().texOffs(37, 17).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition left_sleeve = partdefinition.addOrReplaceChild(
            "left_sleeve",
            CubeListBuilder.create()
               .texOffs(42, 34)
               .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F))
               .texOffs(105, 56)
               .addBox(-1.5F, 5.0F, -2.5F, 5.0F, 4.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-1.0F, 11.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(0, 51).addBox(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition right_pants = partdefinition.addOrReplaceChild(
            "right_pants",
            CubeListBuilder.create().texOffs(22, 71).addBox(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(17, 51).addBox(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_pants = partdefinition.addOrReplaceChild(
            "left_pants",
            CubeListBuilder.create().texOffs(0, 72).addBox(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
