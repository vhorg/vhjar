package iskallia.vault.entity.model.overgrown_woodman;

import iskallia.vault.entity.entity.overgrown_woodman.Tier1OvergrownWoodmanEntity;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import org.jetbrains.annotations.NotNull;

public class Tier1OvergrownWoodmanModel extends EndermanModel<Tier1OvergrownWoodmanEntity> {
   public Tier1OvergrownWoodmanModel(ModelPart root) {
      super(root);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = EndermanModel.createMesh(CubeDeformation.NONE, 0.0F);
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create()
            .texOffs(8, 0)
            .addBox(0.0F, 0.0F, -2.0F, 4.0F, 22.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-4.0F, 1.0F, -2.0F, 4.0F, 22.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(41, 0)
            .addBox(0.0F, 0.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.2F))
            .texOffs(41, 6)
            .addBox(-4.0F, 1.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.22F)),
         PartPose.offset(0.0F, -15.0F, 0.0F)
      );
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create().texOffs(0, 26).addBox(-2.0F, -7.0F, -4.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, -3.0F, -2.0F)
      );
      PartDefinition hat = partdefinition.addOrReplaceChild(
         "hat",
         CubeListBuilder.create().texOffs(22, 29).addBox(-2.0F, -7.0F, -4.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(-0.5F)),
         PartPose.offset(0.0F, -3.0F, -2.0F)
      );
      PartDefinition right_arm = partdefinition.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create()
            .texOffs(32, 0)
            .addBox(-1.0F, 0.0F, -1.0F, 2.0F, 18.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(8, 40)
            .addBox(-2.0F, 10.0F, 0.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.2F))
            .texOffs(33, 21)
            .addBox(-1.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.2F)),
         PartPose.offset(-5.0F, -9.0F, 0.0F)
      );
      PartDefinition cube_r1 = right_arm.addOrReplaceChild(
         "cube_r1",
         CubeListBuilder.create().texOffs(8, 47).addBox(0.0F, -2.0F, -1.0F, 1.0F, 6.0F, 1.0F, new CubeDeformation(-0.21F)),
         PartPose.offsetAndRotation(-1.5633F, 21.6296F, 1.0F, 0.0F, 0.0F, -0.0436F)
      );
      PartDefinition cube_r2 = right_arm.addOrReplaceChild(
         "cube_r2",
         CubeListBuilder.create().texOffs(8, 43).addBox(0.0F, -2.0F, -1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(-0.2F)),
         PartPose.offsetAndRotation(-1.5F, 19.0F, 1.0F, 0.0F, 0.0F, 0.1745F)
      );
      PartDefinition cube_r3 = right_arm.addOrReplaceChild(
         "cube_r3",
         CubeListBuilder.create().texOffs(8, 47).addBox(0.0F, -2.0F, -1.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(-0.21F)),
         PartPose.offsetAndRotation(0.0F, 21.6296F, 1.5633F, -0.0436F, 0.0F, 0.0F)
      );
      PartDefinition cube_r4 = right_arm.addOrReplaceChild(
         "cube_r4",
         CubeListBuilder.create().texOffs(8, 43).addBox(0.0F, -2.0F, -1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(-0.2F)),
         PartPose.offsetAndRotation(0.0F, 19.0F, 1.5F, 0.1745F, 0.0F, 0.0F)
      );
      PartDefinition cube_r5 = right_arm.addOrReplaceChild(
         "cube_r5",
         CubeListBuilder.create().texOffs(8, 47).addBox(0.0F, -2.0F, 0.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(-0.21F)),
         PartPose.offsetAndRotation(0.0F, 21.6296F, -1.5633F, 0.0436F, 0.0F, 0.0F)
      );
      PartDefinition cube_r6 = right_arm.addOrReplaceChild(
         "cube_r6",
         CubeListBuilder.create().texOffs(8, 43).addBox(0.0F, -2.0F, 0.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(-0.2F)),
         PartPose.offsetAndRotation(0.0F, 19.0F, -1.5F, -0.1745F, 0.0F, 0.0F)
      );
      PartDefinition left_arm = partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create()
            .texOffs(24, 0)
            .addBox(-1.0F, 0.0F, -1.0F, 2.0F, 24.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(8, 40)
            .addBox(-1.0F, 8.0F, -2.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.2F))
            .texOffs(8, 40)
            .addBox(0.0F, 7.0F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.2F))
            .texOffs(8, 40)
            .addBox(0.0F, 5.0F, -2.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(33, 21)
            .addBox(-1.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.2F)),
         PartPose.offset(5.0F, -12.0F, 0.0F)
      );
      PartDefinition cube_r7 = left_arm.addOrReplaceChild(
         "cube_r7",
         CubeListBuilder.create().texOffs(8, 47).mirror().addBox(-1.0F, -2.0F, -1.0F, 1.0F, 6.0F, 1.0F, new CubeDeformation(-0.21F)).mirror(false),
         PartPose.offsetAndRotation(1.5633F, 27.6296F, 1.0F, 0.0F, 0.0F, 0.0436F)
      );
      PartDefinition cube_r8 = left_arm.addOrReplaceChild(
         "cube_r8",
         CubeListBuilder.create().texOffs(8, 43).mirror().addBox(-1.0F, -2.0F, -1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(-0.2F)).mirror(false),
         PartPose.offsetAndRotation(1.5F, 25.0F, 1.0F, 0.0F, 0.0F, -0.1745F)
      );
      PartDefinition cube_r9 = left_arm.addOrReplaceChild(
         "cube_r9",
         CubeListBuilder.create().texOffs(8, 47).mirror().addBox(-1.0F, -2.0F, -1.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(-0.21F)).mirror(false),
         PartPose.offsetAndRotation(0.0F, 27.6296F, 1.5633F, -0.0436F, 0.0F, 0.0F)
      );
      PartDefinition cube_r10 = left_arm.addOrReplaceChild(
         "cube_r10",
         CubeListBuilder.create().texOffs(8, 43).mirror().addBox(-1.0F, -2.0F, -1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(-0.2F)).mirror(false),
         PartPose.offsetAndRotation(0.0F, 25.0F, 1.5F, 0.1745F, 0.0F, 0.0F)
      );
      PartDefinition cube_r11 = left_arm.addOrReplaceChild(
         "cube_r11",
         CubeListBuilder.create().texOffs(8, 47).mirror().addBox(-1.0F, -2.0F, 0.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(-0.21F)).mirror(false),
         PartPose.offsetAndRotation(0.0F, 27.6296F, -1.5633F, 0.0436F, 0.0F, 0.0F)
      );
      PartDefinition cube_r12 = left_arm.addOrReplaceChild(
         "cube_r12",
         CubeListBuilder.create().texOffs(8, 43).mirror().addBox(-1.0F, -2.0F, 0.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(-0.2F)).mirror(false),
         PartPose.offsetAndRotation(0.0F, 25.0F, -1.5F, -0.1745F, 0.0F, 0.0F)
      );
      PartDefinition right_leg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create()
            .texOffs(0, 40)
            .addBox(-1.0F, 0.0F, -1.0F, 2.0F, 17.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(8, 40)
            .addBox(-2.0F, 6.0F, -2.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(8, 40)
            .addBox(-1.0F, 8.0F, -2.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.2F)),
         PartPose.offset(-2.0F, 7.0F, 0.0F)
      );
      PartDefinition left_leg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create().texOffs(0, 40).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 17.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offset(2.0F, 7.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 64, 64);
   }

   public void setupAnim(
      @NotNull Tier1OvergrownWoodmanEntity entity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch
   ) {
      super.setupAnim(entity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
      this.head.y = -2.0F;
      this.head.z = -2.0F;
      this.hat.y = -2.0F;
      this.hat.z = -2.0F;
      this.rightLeg.y = 8.0F;
      this.leftLeg.y = 8.0F;
   }
}
