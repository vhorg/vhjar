package iskallia.vault.entity.model.elite;

import iskallia.vault.client.gui.helper.Easing;
import iskallia.vault.entity.entity.elite.EliteDrownedEntity;
import javax.annotation.Nonnull;
import net.minecraft.client.model.DrownedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class EliteDrownedModel extends DrownedModel<EliteDrownedEntity> {
   private final ModelPart right_arm_fin;
   private final ModelPart left_arm_fin;
   private final ModelPart right_leg_fin;
   private final ModelPart left_leg_fin;

   public EliteDrownedModel(ModelPart root) {
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
      MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create().texOffs(20, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(0, 6)
            .addBox(0.0F, -13.0F, -3.0F, 0.0F, 11.0F, 10.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
      PartDefinition right_arm = partdefinition.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create().texOffs(32, 0).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-5.0F, 2.0F, 0.0F)
      );
      PartDefinition right_arm_fin = right_arm.addOrReplaceChild(
         "right_arm_fin",
         CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -3.5F, 0.0F, 4.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-3.0F, 5.5F, 0.0F)
      );
      PartDefinition left_arm = partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create().texOffs(32, 0).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offset(5.0F, 2.0F, 0.0F)
      );
      PartDefinition left_arm_fin = left_arm.addOrReplaceChild(
         "left_arm_fin",
         CubeListBuilder.create().texOffs(21, 32).addBox(0.0F, -3.5F, 0.0F, 4.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offset(3.0F, 5.5F, 0.0F)
      );
      PartDefinition right_leg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create().texOffs(0, 27).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-1.9F, 12.0F, 0.0F)
      );
      PartDefinition right_leg_fin = right_leg.addOrReplaceChild(
         "right_leg_fin",
         CubeListBuilder.create().texOffs(16, 27).addBox(0.0F, -4.5F, -1.0F, 0.0F, 9.0F, 5.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-0.2F, 6.5F, 2.0F)
      );
      PartDefinition left_leg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create().texOffs(0, 27).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offset(1.9F, 12.0F, 0.0F)
      );
      PartDefinition left_leg_fin = left_leg.addOrReplaceChild(
         "left_leg_fin",
         CubeListBuilder.create().texOffs(16, 27).addBox(0.0F, -4.5F, -1.0F, 0.0F, 9.0F, 5.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 6.5F, 2.0F)
      );
      return LayerDefinition.create(meshdefinition, 64, 64);
   }

   public void setupAnim(@Nonnull EliteDrownedEntity entity, float limbSwing, float pLimbSwingAmount, float ageInTicks, float pNetHeadYaw, float pHeadPitch) {
      super.setupAnim(entity, limbSwing, pLimbSwingAmount, ageInTicks, pNetHeadYaw, pHeadPitch);
      float armRotation = (Easing.EASE_IN_OUT_SINE.calc(ageInTicks / 18.0F) - 0.5F) * 0.4F;
      this.right_arm_fin.yRot = armRotation;
      this.left_arm_fin.yRot = -armRotation;
      float legRotation = Easing.EASE_IN_OUT_SINE.calc(ageInTicks / 18.0F) - 0.5F;
      this.right_leg_fin.yRot = legRotation;
      this.left_leg_fin.yRot = legRotation;
   }
}
