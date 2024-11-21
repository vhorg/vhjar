package iskallia.vault.entity.model.guardian;

import iskallia.vault.entity.entity.guardian.PirateGuardianEntity;
import javax.annotation.Nonnull;
import net.minecraft.client.model.PiglinModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class PirateGuardianModel {
   public static class Arbalist extends PiglinModel<PirateGuardianEntity> {
      public Arbalist(ModelPart root) {
         super(root);
      }

      public static LayerDefinition createBodyLayer() {
         MeshDefinition meshdefinition = PiglinModel.createMesh(CubeDeformation.NONE);
         PartDefinition partdefinition = meshdefinition.getRoot();
         PartDefinition head = partdefinition.addOrReplaceChild(
            "head",
            CubeListBuilder.create()
               .texOffs(1, 0)
               .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(2, 4)
               .addBox(2.0F, -1.5F, -5.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(-0.2F))
               .texOffs(2, 0)
               .addBox(-3.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(-0.2F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition head_r1 = head.addOrReplaceChild(
            "head_r1",
            CubeListBuilder.create().texOffs(30, 1).addBox(0.0F, -2.0F, -3.0F, 7.0F, 2.0F, 3.0F, new CubeDeformation(0.1F)),
            PartPose.offsetAndRotation(-3.7F, -0.4F, -1.5F, 0.1304F, 0.0114F, 0.1316F)
         );
         head.addOrReplaceChild("left_ear", CubeListBuilder.create(), PartPose.ZERO);
         head.addOrReplaceChild("right_ear", CubeListBuilder.create(), PartPose.ZERO);
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition jacket = partdefinition.addOrReplaceChild(
            "jacket",
            CubeListBuilder.create().texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create().texOffs(48, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition left_sleeve = partdefinition.addOrReplaceChild(
            "left_sleeve",
            CubeListBuilder.create().texOffs(46, 28).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create().texOffs(41, 9).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition right_sleeve = partdefinition.addOrReplaceChild(
            "right_sleeve",
            CubeListBuilder.create().texOffs(46, 28).mirror().addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)).mirror(false),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(2.0F, 12.0F, 0.0F)
         );
         PartDefinition left_pants = partdefinition.addOrReplaceChild(
            "left_pants",
            CubeListBuilder.create().texOffs(32, 48).addBox(-2.0F, 0.25F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.26F)),
            PartPose.offset(2.0F, 12.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-2.0F, 12.0F, 0.0F)
         );
         PartDefinition right_pants = partdefinition.addOrReplaceChild(
            "right_pants",
            CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, 0.25F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.27F)),
            PartPose.offset(-2.0F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 64, 64);
      }

      public void setupAnim(
         @Nonnull PirateGuardianEntity entity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch
      ) {
         super.setupAnim(entity, pLimbSwing, 0.0F, pAgeInTicks, pNetHeadYaw, pHeadPitch);
         this.leftLeg.setRotation(0.0F, 0.0F, 0.0F);
         this.rightLeg.setRotation(0.0F, 0.0F, 0.0F);
         this.leftPants.setRotation(0.0F, 0.0F, 0.0F);
         this.rightPants.setRotation(0.0F, 0.0F, 0.0F);
      }
   }

   public static class Bruiser extends PiglinModel<PirateGuardianEntity> {
      public Bruiser(ModelPart root) {
         super(root);
      }

      public static LayerDefinition createBodyLayer() {
         MeshDefinition meshdefinition = PiglinModel.createMesh(CubeDeformation.NONE);
         PartDefinition partdefinition = meshdefinition.getRoot();
         PartDefinition head = partdefinition.addOrReplaceChild(
            "head",
            CubeListBuilder.create()
               .texOffs(1, 0)
               .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(2, 4)
               .addBox(2.0F, -1.5F, -5.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(-0.2F))
               .texOffs(2, 0)
               .addBox(-3.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(-0.2F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition head_r1 = head.addOrReplaceChild(
            "head_r1",
            CubeListBuilder.create().texOffs(30, 1).addBox(0.0F, -2.0F, -3.0F, 7.0F, 2.0F, 3.0F, new CubeDeformation(0.1F)),
            PartPose.offsetAndRotation(-3.7F, -0.4F, -1.5F, 0.1304F, 0.0114F, 0.1316F)
         );
         head.addOrReplaceChild("left_ear", CubeListBuilder.create(), PartPose.ZERO);
         head.addOrReplaceChild("right_ear", CubeListBuilder.create(), PartPose.ZERO);
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition jacket = partdefinition.addOrReplaceChild(
            "jacket",
            CubeListBuilder.create().texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create().texOffs(48, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition left_sleeve = partdefinition.addOrReplaceChild(
            "left_sleeve",
            CubeListBuilder.create().texOffs(46, 28).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(41, 10)
               .addBox(-3.0F, 7.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(41, 10)
               .addBox(-2.5F, 6.0F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(41, 10)
               .addBox(-1.5F, 4.5F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(40, 10)
               .addBox(-2.5F, 9.0F, -0.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(42, 10)
               .addBox(0.0F, 7.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(40, 16)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition right_sleeve = partdefinition.addOrReplaceChild(
            "right_sleeve",
            CubeListBuilder.create().texOffs(0, 33).addBox(1.0F, -2.0F, 2.0F, 0.0F, 12.0F, 0.0F, new CubeDeformation(0.25F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(2.0F, 12.0F, 0.0F)
         );
         PartDefinition left_pants = partdefinition.addOrReplaceChild(
            "left_pants",
            CubeListBuilder.create().texOffs(32, 48).addBox(-2.0F, 0.25F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.26F)),
            PartPose.offset(2.0F, 12.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-2.0F, 12.0F, 0.0F)
         );
         PartDefinition right_pants = partdefinition.addOrReplaceChild(
            "right_pants",
            CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, 0.25F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.27F)),
            PartPose.offset(-2.0F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 64, 64);
      }

      public void setupAnim(
         @Nonnull PirateGuardianEntity entity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch
      ) {
         super.setupAnim(entity, pLimbSwing, 0.0F, pAgeInTicks, pNetHeadYaw, pHeadPitch);
         this.leftLeg.setRotation(0.0F, 0.0F, 0.0F);
         this.rightLeg.setRotation(0.0F, 0.0F, 0.0F);
         this.leftPants.setRotation(0.0F, 0.0F, 0.0F);
         this.rightPants.setRotation(0.0F, 0.0F, 0.0F);
      }
   }
}
