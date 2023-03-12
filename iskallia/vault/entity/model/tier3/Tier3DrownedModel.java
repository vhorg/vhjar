package iskallia.vault.entity.model.tier3;

import iskallia.vault.client.gui.helper.Easing;
import iskallia.vault.entity.entity.tier3.Tier3DrownedEntity;
import javax.annotation.Nonnull;
import net.minecraft.client.model.DrownedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class Tier3DrownedModel extends DrownedModel<Tier3DrownedEntity> {
   private final ModelPart right_arm_fin;
   private final ModelPart left_arm_fin;
   private final ModelPart right_leg_fin;
   private final ModelPart left_leg_fin;

   public Tier3DrownedModel(ModelPart root) {
      super(root);
      ModelPart right_arm = root.getChild("right_arm");
      this.right_arm_fin = right_arm.getChild("right_arm_fin");
      ModelPart left_arm = root.getChild("left_arm");
      this.left_arm_fin = left_arm.getChild("left_arm_fin");
      ModelPart right_leg = root.getChild("right_leg");
      this.right_leg_fin = right_leg.getChild("right_leg_fin");
      ModelPart left_leg = root.getChild("left_leg");
      this.left_leg_fin = left_leg.getChild("left_leg_fin");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      PartDefinition Body_r1 = body.addOrReplaceChild(
         "Body_r1",
         CubeListBuilder.create()
            .texOffs(0, 5)
            .addBox(0.0F, -6.0F, 4.0F, 0.0F, 12.0F, 11.0F, new CubeDeformation(0.0F))
            .texOffs(32, 0)
            .addBox(-4.0F, -6.0F, 0.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, 0.3927F, 0.0F, 0.0F)
      );
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(0, 24)
            .addBox(0.0F, -13.0F, -3.0F, 0.0F, 11.0F, 10.0F, new CubeDeformation(0.0F))
            .texOffs(35, 51)
            .addBox(-3.0F, -1.0F, -6.0F, 6.0F, 5.0F, 7.0F, new CubeDeformation(0.0F))
            .texOffs(36, 47)
            .addBox(-4.0F, -6.0F, -7.0F, 1.0F, 9.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(36, 47)
            .addBox(3.0F, -6.0F, -7.0F, 1.0F, 9.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
      PartDefinition cube_r1 = head.addOrReplaceChild(
         "cube_r1",
         CubeListBuilder.create().texOffs(14, 20).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -3.0F, -7.0F, 0.3927F, 0.0F, 0.0F)
      );
      PartDefinition right_arm = partdefinition.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create().texOffs(20, 34).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-5.0F, 2.0F, 0.0F)
      );
      PartDefinition right_arm_fin = right_arm.addOrReplaceChild(
         "right_arm_fin",
         CubeListBuilder.create().texOffs(24, 0).addBox(-7.0F, 2.0F, 0.0F, 4.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition left_arm = partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create().texOffs(20, 34).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offset(5.0F, 2.0F, 0.0F)
      );
      PartDefinition left_arm_fin = left_arm.addOrReplaceChild(
         "left_arm_fin",
         CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -3.5F, 0.0F, 4.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offset(5.0F, 5.5F, 0.0F)
      );
      PartDefinition right_leg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create().texOffs(0, 45).addBox(-2.5F, 3.0F, -5.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-1.9F, 12.0F, 3.0F)
      );
      PartDefinition RightLeg_r1 = right_leg.addOrReplaceChild(
         "RightLeg_r1",
         CubeListBuilder.create().texOffs(36, 34).addBox(-3.0F, -6.5F, -1.0F, 5.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 1.5F, -3.0F, -0.7854F, 0.0F, 0.0F)
      );
      PartDefinition right_leg_fin = right_leg.addOrReplaceChild(
         "right_leg_fin",
         CubeListBuilder.create().texOffs(24, -4).addBox(-4.5F, 5.0F, -1.0F, 0.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(3.8F, 0.0F, 0.0F)
      );
      PartDefinition left_leg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create().texOffs(0, 45).mirror().addBox(-1.25F, 3.0F, -2.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offset(1.9F, 12.0F, 0.0F)
      );
      PartDefinition LeftLeg_r1 = left_leg.addOrReplaceChild(
         "LeftLeg_r1",
         CubeListBuilder.create().texOffs(36, 34).addBox(-2.5F, -4.5F, -2.0F, 5.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.7F, 0.7929F, 2.1213F, -0.7854F, 0.0F, 0.0F)
      );
      PartDefinition left_leg_fin = left_leg.addOrReplaceChild(
         "left_leg_fin",
         CubeListBuilder.create().texOffs(24, -4).addBox(0.75F, 4.0F, 2.0F, 0.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 64, 64);
   }

   public void setupAnim(@Nonnull Tier3DrownedEntity entity, float limbSwing, float pLimbSwingAmount, float ageInTicks, float pNetHeadYaw, float pHeadPitch) {
      super.setupAnim(entity, limbSwing, pLimbSwingAmount, ageInTicks, pNetHeadYaw, pHeadPitch);
      float armRotation = (Easing.EASE_IN_OUT_SINE.calc(ageInTicks / 18.0F) - 0.5F) * 0.4F;
      this.right_arm_fin.yRot = armRotation;
      this.left_arm_fin.yRot = -armRotation;
      float legRotation = Easing.EASE_IN_OUT_SINE.calc(ageInTicks / 18.0F) - 0.5F;
      this.right_leg_fin.yRot = legRotation;
      this.left_leg_fin.yRot = legRotation;
   }
}
