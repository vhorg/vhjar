package iskallia.vault.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.entity.entity.FighterEntity;
import java.util.List;
import java.util.Random;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

public class FighterModel extends HumanoidModel<LivingEntity> {
   private final List<ModelPart> parts;
   public final ModelPart leftSleeve;
   public final ModelPart rightSleeve;
   public final ModelPart leftPants;
   public final ModelPart rightPants;
   public final ModelPart jacket;
   private final ModelPart cloak;
   private final ModelPart ear;
   private final boolean slim;

   public FighterModel(ModelPart pRoot, boolean pSlim) {
      super(pRoot, RenderType::entityTranslucent);
      this.slim = pSlim;
      this.ear = pRoot.getChild("ear");
      this.cloak = pRoot.getChild("cloak");
      this.leftSleeve = pRoot.getChild("left_sleeve");
      this.rightSleeve = pRoot.getChild("right_sleeve");
      this.leftPants = pRoot.getChild("left_pants");
      this.rightPants = pRoot.getChild("right_pants");
      this.jacket = pRoot.getChild("jacket");
      this.parts = pRoot.getAllParts().filter(p_170824_ -> !p_170824_.isEmpty()).collect(ImmutableList.toImmutableList());
   }

   public static LayerDefinition createBodyLayer() {
      return createBodyLayer(new CubeDeformation(0.0F));
   }

   public static LayerDefinition createBodyLayerT3() {
      return createBodyLayerT3(new CubeDeformation(0.0F));
   }

   public static LayerDefinition createBodyLayerT4() {
      return createBodyLayerT4(new CubeDeformation(0.0F));
   }

   public static LayerDefinition createBodyLayer(CubeDeformation pCubeDeformation) {
      MeshDefinition meshdefinition = HumanoidModel.createMesh(pCubeDeformation, 0.0F);
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild(
         "ear", CubeListBuilder.create().texOffs(24, 0).addBox(-3.0F, -6.0F, -1.0F, 6.0F, 6.0F, 1.0F, pCubeDeformation), PartPose.ZERO
      );
      partdefinition.addOrReplaceChild(
         "cloak",
         CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, 0.0F, -1.0F, 10.0F, 16.0F, 1.0F, pCubeDeformation, 1.0F, 0.5F),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, pCubeDeformation),
         PartPose.offset(5.0F, 2.0F, 0.0F)
      );
      partdefinition.addOrReplaceChild(
         "left_sleeve",
         CubeListBuilder.create().texOffs(48, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, pCubeDeformation.extend(0.25F)),
         PartPose.offset(5.0F, 2.0F, 0.0F)
      );
      partdefinition.addOrReplaceChild(
         "right_sleeve",
         CubeListBuilder.create().texOffs(40, 32).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, pCubeDeformation.extend(0.25F)),
         PartPose.offset(-5.0F, 2.0F, 0.0F)
      );
      partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, pCubeDeformation),
         PartPose.offset(1.9F, 12.0F, 0.0F)
      );
      partdefinition.addOrReplaceChild(
         "left_pants",
         CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, pCubeDeformation.extend(0.25F)),
         PartPose.offset(1.9F, 12.0F, 0.0F)
      );
      partdefinition.addOrReplaceChild(
         "right_pants",
         CubeListBuilder.create().texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, pCubeDeformation.extend(0.25F)),
         PartPose.offset(-1.9F, 12.0F, 0.0F)
      );
      partdefinition.addOrReplaceChild(
         "jacket", CubeListBuilder.create().texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, pCubeDeformation.extend(0.25F)), PartPose.ZERO
      );
      return LayerDefinition.create(meshdefinition, 64, 64);
   }

   public static LayerDefinition createBodyLayerT3(CubeDeformation pCubeDeformation) {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head", CubeListBuilder.create().texOffs(20, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, pCubeDeformation), PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition Body = partdefinition.addOrReplaceChild(
         "body", CubeListBuilder.create().texOffs(24, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, pCubeDeformation), PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition sword = Body.addOrReplaceChild(
         "sword",
         CubeListBuilder.create()
            .texOffs(35, 100)
            .addBox(0.0F, 2.75F, 1.65F, 1.0F, 2.0F, 2.0F, pCubeDeformation)
            .texOffs(35, 100)
            .addBox(0.0F, 1.75F, 0.65F, 1.0F, 2.0F, 2.0F, pCubeDeformation)
            .texOffs(12, 117)
            .addBox(0.0F, 1.75F, 6.65F, 1.0F, 2.0F, 2.0F, pCubeDeformation)
            .texOffs(3, 111)
            .addBox(0.0F, 7.75F, 0.65F, 1.0F, 2.0F, 2.0F, pCubeDeformation)
            .texOffs(19, 83)
            .addBox(0.0F, 2.75F, 4.65F, 1.0F, 2.0F, 3.0F, pCubeDeformation)
            .texOffs(2, 88)
            .addBox(0.0F, 5.75F, 1.65F, 1.0F, 3.0F, 2.0F, pCubeDeformation)
            .texOffs(12, 93)
            .addBox(0.0F, 3.75F, 2.65F, 1.0F, 4.0F, 4.0F, pCubeDeformation)
            .texOffs(92, 49)
            .addBox(0.0F, 12.75F, 11.65F, 1.0F, 3.0F, 3.0F, pCubeDeformation)
            .texOffs(92, 49)
            .addBox(0.0F, 11.75F, 10.65F, 1.0F, 3.0F, 3.0F, pCubeDeformation)
            .texOffs(92, 49)
            .addBox(0.0F, 10.75F, 9.65F, 1.0F, 3.0F, 3.0F, pCubeDeformation)
            .texOffs(92, 49)
            .addBox(0.0F, 9.75F, 8.65F, 1.0F, 3.0F, 3.0F, pCubeDeformation)
            .texOffs(92, 49)
            .addBox(0.0F, 8.75F, 7.65F, 1.0F, 3.0F, 3.0F, pCubeDeformation)
            .texOffs(92, 49)
            .addBox(0.0F, 7.75F, 6.65F, 1.0F, 3.0F, 3.0F, pCubeDeformation)
            .texOffs(92, 49)
            .addBox(0.0F, 6.75F, 5.65F, 1.0F, 3.0F, 3.0F, pCubeDeformation)
            .texOffs(92, 49)
            .addBox(0.0F, 5.75F, 4.65F, 1.0F, 3.0F, 3.0F, pCubeDeformation)
            .texOffs(74, 77)
            .addBox(0.0F, -0.25F, -1.35F, 1.0F, 3.0F, 3.0F, pCubeDeformation),
         PartPose.offsetAndRotation(-0.5F, 5.5F, -12.5F, 0.7854F, 0.0F, 0.0F)
      );
      PartDefinition cube_r2 = Body.addOrReplaceChild(
         "cube_r2",
         CubeListBuilder.create().texOffs(75, 70).addBox(-5.5F, -1.0F, 1.5F, 1.0F, 16.0F, 16.0F, pCubeDeformation),
         PartPose.offsetAndRotation(5.0F, 8.05F, -13.975F, 0.7854F, 0.0F, 0.0F)
      );
      PartDefinition right_arm = partdefinition.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create().texOffs(32, 56).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, pCubeDeformation),
         PartPose.offset(-5.0F, 2.0F, 0.0F)
      );
      PartDefinition left_arm = partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create().texOffs(48, 28).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, pCubeDeformation),
         PartPose.offset(5.0F, 2.0F, 0.0F)
      );
      PartDefinition right_leg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, pCubeDeformation),
         PartPose.offset(-1.9F, 12.0F, 0.0F)
      );
      PartDefinition left_leg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create().texOffs(36, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, pCubeDeformation),
         PartPose.offset(1.9F, 12.0F, 0.0F)
      );
      partdefinition.addOrReplaceChild(
         "hat",
         CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, pCubeDeformation.extend(0.5F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      partdefinition.addOrReplaceChild(
         "ear", CubeListBuilder.create().texOffs(24, 0).addBox(-3.0F, -6.0F, -1.0F, 6.0F, 6.0F, 1.0F, pCubeDeformation), PartPose.ZERO
      );
      partdefinition.addOrReplaceChild(
         "cloak",
         CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, 0.0F, -1.0F, 10.0F, 16.0F, 1.0F, pCubeDeformation, 1.0F, 0.5F),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      partdefinition.addOrReplaceChild(
         "left_sleeve",
         CubeListBuilder.create().texOffs(16, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, pCubeDeformation.extend(0.25F)),
         PartPose.offset(5.0F, 2.0F, 0.0F)
      );
      partdefinition.addOrReplaceChild(
         "right_sleeve",
         CubeListBuilder.create().texOffs(52, 0).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, pCubeDeformation.extend(0.25F)),
         PartPose.offset(-5.0F, 2.0F, 0.0F)
      );
      partdefinition.addOrReplaceChild(
         "left_pants",
         CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, pCubeDeformation.extend(0.25F)),
         PartPose.offset(1.9F, 12.0F, 0.0F)
      );
      partdefinition.addOrReplaceChild(
         "right_pants",
         CubeListBuilder.create().texOffs(44, 44).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, pCubeDeformation.extend(0.25F)),
         PartPose.offset(-1.9F, 12.0F, 0.0F)
      );
      partdefinition.addOrReplaceChild(
         "jacket", CubeListBuilder.create().texOffs(0, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, pCubeDeformation.extend(0.25F)), PartPose.ZERO
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }

   public static LayerDefinition createBodyLayerT4(CubeDeformation pCubeDeformation) {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, pCubeDeformation), PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition Body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create()
            .texOffs(0, 32)
            .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, pCubeDeformation)
            .texOffs(16, 60)
            .addBox(-3.5F, 3.0F, -1.0F, 7.0F, 7.0F, 2.0F, pCubeDeformation)
            .texOffs(0, 0)
            .addBox(-3.25F, 6.0F, 0.0F, 2.0F, 1.0F, 1.0F, pCubeDeformation)
            .texOffs(0, 0)
            .addBox(1.25F, 6.0F, 0.0F, 2.0F, 1.0F, 1.0F, pCubeDeformation)
            .texOffs(0, 0)
            .addBox(0.75F, 8.0F, 0.0F, 2.0F, 1.0F, 1.0F, pCubeDeformation)
            .texOffs(14, 88)
            .addBox(-2.75F, 8.0F, 0.0F, 2.0F, 1.0F, 1.0F, pCubeDeformation)
            .texOffs(0, 0)
            .addBox(0.75F, 4.25F, 0.0F, 2.0F, 1.0F, 1.0F, pCubeDeformation)
            .texOffs(0, 0)
            .addBox(-2.75F, 4.25F, 0.0F, 2.0F, 1.0F, 1.0F, pCubeDeformation),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition right_arm = partdefinition.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create().texOffs(56, 0).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, pCubeDeformation),
         PartPose.offset(-5.0F, 2.0F, 0.0F)
      );
      PartDefinition left_arm = partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create().texOffs(52, 28).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, pCubeDeformation),
         PartPose.offset(5.0F, 2.0F, 0.0F)
      );
      PartDefinition right_leg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create().texOffs(36, 44).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, pCubeDeformation),
         PartPose.offset(-1.9F, 12.0F, 0.0F)
      );
      PartDefinition left_leg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create().texOffs(44, 12).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, pCubeDeformation),
         PartPose.offset(1.9F, 12.0F, 0.0F)
      );
      partdefinition.addOrReplaceChild(
         "hat",
         CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, pCubeDeformation.extend(0.5F)),
         PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1047F, 0.0873F, 0.0F)
      );
      partdefinition.addOrReplaceChild(
         "ear", CubeListBuilder.create().texOffs(24, 0).addBox(-3.0F, -6.0F, -1.0F, 6.0F, 6.0F, 1.0F, pCubeDeformation), PartPose.ZERO
      );
      partdefinition.addOrReplaceChild(
         "cloak",
         CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, 0.0F, -1.0F, 10.0F, 16.0F, 1.0F, pCubeDeformation, 1.0F, 0.5F),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      partdefinition.addOrReplaceChild(
         "left_sleeve",
         CubeListBuilder.create().texOffs(0, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, pCubeDeformation.extend(0.25F)),
         PartPose.offset(5.0F, 2.0F, 0.0F)
      );
      partdefinition.addOrReplaceChild(
         "right_sleeve",
         CubeListBuilder.create().texOffs(52, 44).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, pCubeDeformation.extend(0.25F)),
         PartPose.offset(-5.0F, 2.0F, 0.0F)
      );
      partdefinition.addOrReplaceChild(
         "left_pants",
         CubeListBuilder.create().texOffs(32, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, pCubeDeformation.extend(0.25F)),
         PartPose.offset(1.9F, 12.0F, 0.0F)
      );
      partdefinition.addOrReplaceChild(
         "right_pants",
         CubeListBuilder.create().texOffs(20, 44).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, pCubeDeformation.extend(0.25F)),
         PartPose.offset(-1.9F, 12.0F, 0.0F)
      );
      partdefinition.addOrReplaceChild(
         "jacket", CubeListBuilder.create().texOffs(28, 28).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, pCubeDeformation.extend(0.25F)), PartPose.ZERO
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }

   protected Iterable<ModelPart> bodyParts() {
      return Iterables.concat(super.bodyParts(), ImmutableList.of(this.leftPants, this.rightPants, this.leftSleeve, this.rightSleeve, this.jacket));
   }

   public void setupAnim(FighterEntity pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
      super.setupAnim(pEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
      this.leftPants.copyFrom(this.leftLeg);
      this.rightPants.copyFrom(this.rightLeg);
      this.leftSleeve.copyFrom(this.leftArm);
      this.rightSleeve.copyFrom(this.rightArm);
      this.jacket.copyFrom(this.body);
      this.cloak.visible = false;
      this.ear.visible = false;
      String path = pEntity.getType().getRegistryName().getPath();
      path = path.substring(path.length() - 1);
      ModelPart var10000 = this.hat;
      byte var9 = -1;
      switch (path.hashCode()) {
         case 51:
            if (path.equals("3")) {
               var9 = 0;
            }
         default:
            var10000.visible = switch (var9) {
               case 0 -> false;
               default -> true;
            };
            if (pEntity.getItemBySlot(EquipmentSlot.CHEST).isEmpty()) {
               if (pEntity.isCrouching()) {
                  this.cloak.z = 1.4F;
                  this.cloak.y = 1.85F;
               } else {
                  this.cloak.z = 0.0F;
                  this.cloak.y = 0.0F;
               }
            } else if (pEntity.isCrouching()) {
               this.cloak.z = 0.3F;
               this.cloak.y = 0.8F;
            } else {
               this.cloak.z = -1.1F;
               this.cloak.y = -0.85F;
            }
      }
   }

   public void setAllVisible(boolean pVisible) {
      super.setAllVisible(pVisible);
      this.leftSleeve.visible = pVisible;
      this.rightSleeve.visible = pVisible;
      this.leftPants.visible = pVisible;
      this.rightPants.visible = pVisible;
      this.jacket.visible = pVisible;
      this.cloak.visible = pVisible;
      this.ear.visible = pVisible;
   }

   public void translateToHand(HumanoidArm pSide, PoseStack pPoseStack) {
      ModelPart modelpart = this.getArm(pSide);
      if (this.slim) {
         float f = 0.5F * (pSide == HumanoidArm.RIGHT ? 1 : -1);
         modelpart.x += f;
         modelpart.translateAndRotate(pPoseStack);
         modelpart.x -= f;
      } else {
         modelpart.translateAndRotate(pPoseStack);
      }
   }

   public ModelPart getRandomModelPart(Random pRandom) {
      return this.parts.get(pRandom.nextInt(this.parts.size()));
   }
}
