package iskallia.vault.entity.model.bloodhorde;

import iskallia.vault.entity.entity.bloodhorde.Tier4BloodHordeEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import org.jetbrains.annotations.NotNull;

public class Tier4BloodHordeModel extends ZombieModel<Tier4BloodHordeEntity> {
   public Tier4BloodHordeModel(ModelPart root) {
      super(root);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition hat = partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create().texOffs(1, 19).addBox(-3.4726F, -8.3322F, -3.6205F, 8.0F, 8.0F, 7.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(2.0F, -8.0F, -1.0F, 0.0F, 0.0F, 0.0F)
      );
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create().texOffs(0, 27).addBox(-5.0F, -6.0F, -3.4F, 10.0F, 12.0F, 9.0F, new CubeDeformation(-0.01F)),
         PartPose.offsetAndRotation(0.0F, 7.25F, -1.25F, 0.829F, 0.0F, 0.0F)
      );
      PartDefinition cube_r1 = body.addOrReplaceChild(
         "cube_r1",
         CubeListBuilder.create().texOffs(44, 11).addBox(-2.0F, -2.0F, -1.0F, 5.0F, 4.0F, 5.0F, new CubeDeformation(-0.3F)),
         PartPose.offsetAndRotation(-5.0F, -7.0F, 9.0F, -1.4024F, 0.3179F, -0.7206F)
      );
      PartDefinition cube_r2 = body.addOrReplaceChild(
         "cube_r2",
         CubeListBuilder.create().texOffs(4, 55).addBox(-0.3475F, -0.2738F, -0.6933F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.2F)),
         PartPose.offsetAndRotation(-3.1F, -9.85F, -2.75F, 1.7715F, 0.129F, -0.272F)
      );
      PartDefinition cube_r3 = body.addOrReplaceChild(
         "cube_r3",
         CubeListBuilder.create().texOffs(4, 55).addBox(-4.6709F, -0.0011F, 3.9146F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.2F)),
         PartPose.offsetAndRotation(1.5F, -10.25F, -1.75F, 1.4563F, 0.2898F, 0.7109F)
      );
      PartDefinition cube_r4 = body.addOrReplaceChild(
         "cube_r4",
         CubeListBuilder.create().texOffs(0, 0).addBox(-3.6982F, -5.2447F, 1.1993F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(1.5F, -10.25F, -1.75F, -1.4782F, 1.0219F, -2.0976F)
      );
      PartDefinition cube_r5 = body.addOrReplaceChild(
         "cube_r5",
         CubeListBuilder.create().texOffs(29, 49).addBox(-4.5608F, -7.426F, -5.0248F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(1.5F, -10.25F, -1.75F, -2.1542F, 0.7081F, -2.5371F)
      );
      PartDefinition cube_r6 = body.addOrReplaceChild(
         "cube_r6",
         CubeListBuilder.create().texOffs(13, 50).addBox(-1.3F, -1.1622F, -0.9643F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.2F)),
         PartPose.offsetAndRotation(-12.4F, -8.05F, 9.95F, -2.2742F, 0.2849F, -2.5138F)
      );
      PartDefinition right_arm_r1 = body.addOrReplaceChild(
         "right_arm_r1",
         CubeListBuilder.create().texOffs(35, 4).addBox(1.0F, 1.0F, -2.0F, 3.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-13.0F, -9.25F, 10.25F, -0.4363F, 0.0F, 0.0F)
      );
      PartDefinition head_r1 = body.addOrReplaceChild(
         "head_r1",
         CubeListBuilder.create().texOffs(0, 16).addBox(-3.5F, -4.5F, -6.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(1.5F, -10.25F, -1.75F, 0.8637F, 0.2144F, 0.2668F)
      );
      PartDefinition cube_r7 = body.addOrReplaceChild(
         "cube_r7",
         CubeListBuilder.create().texOffs(18, 52).addBox(4.0F, -1.6319F, -4.7588F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-2.5F, 1.75F, 1.75F, 0.192F, -1.43F, 0.0296F)
      );
      PartDefinition cube_r8 = body.addOrReplaceChild(
         "cube_r8",
         CubeListBuilder.create().texOffs(18, 52).addBox(4.0F, -1.6319F, -4.7588F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.2F)),
         PartPose.offsetAndRotation(1.5F, -10.25F, -1.75F, 0.5146F, 0.2144F, 0.2668F)
      );
      PartDefinition cube_r9 = body.addOrReplaceChild(
         "cube_r9",
         CubeListBuilder.create().texOffs(4, 55).addBox(-5.6471F, 1.4182F, 2.2165F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.2F)),
         PartPose.offsetAndRotation(-5.0F, -1.25F, 0.75F, -1.1082F, -0.8227F, 0.9733F)
      );
      PartDefinition cube_r10 = body.addOrReplaceChild(
         "cube_r10",
         CubeListBuilder.create().texOffs(0, 49).addBox(-3.9302F, -4.2738F, -3.0278F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.2F)),
         PartPose.offsetAndRotation(-14.25F, -16.5F, 6.25F, 1.1177F, 0.5821F, -1.5573F)
      );
      PartDefinition cube_r11 = body.addOrReplaceChild(
         "cube_r11",
         CubeListBuilder.create().texOffs(0, 49).addBox(-3.9302F, -4.2738F, -3.0278F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.2F)),
         PartPose.offsetAndRotation(-5.0F, -1.25F, 0.75F, -1.3134F, -0.2975F, -0.2602F)
      );
      PartDefinition cube_r12 = body.addOrReplaceChild(
         "cube_r12",
         CubeListBuilder.create().texOffs(4, 55).addBox(-5.75F, -0.8614F, -0.4842F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.2F)),
         PartPose.offsetAndRotation(-5.0F, -1.25F, 0.75F, -0.6545F, 0.0F, 0.0F)
      );
      PartDefinition right_arm_r2 = body.addOrReplaceChild(
         "right_arm_r2",
         CubeListBuilder.create().texOffs(32, 16).addBox(-4.0F, -2.0F, -3.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-5.0F, -1.25F, 0.75F, -0.8727F, 0.0F, 0.0F)
      );
      PartDefinition left_arm_r1 = body.addOrReplaceChild(
         "left_arm_r1",
         CubeListBuilder.create().texOffs(32, 0).addBox(0.0F, -2.0F, -3.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(5.0F, 1.75F, 0.25F, -0.8727F, 0.0F, 0.0F)
      );
      PartDefinition cube_r13 = body.addOrReplaceChild(
         "cube_r13",
         CubeListBuilder.create().texOffs(13, 50).addBox(5.3386F, 2.207F, -0.1766F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.2F)),
         PartPose.offsetAndRotation(5.0F, 1.75F, 0.25F, -1.2179F, 1.0404F, -0.5386F)
      );
      PartDefinition cube_r14 = body.addOrReplaceChild(
         "cube_r14",
         CubeListBuilder.create().texOffs(13, 50).addBox(2.7F, 4.4378F, 1.5357F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.3F)),
         PartPose.offsetAndRotation(5.0F, 1.75F, 0.25F, -1.309F, 0.0F, 0.0F)
      );
      PartDefinition cube_r15 = body.addOrReplaceChild(
         "cube_r15",
         CubeListBuilder.create().texOffs(0, 49).addBox(-1.0F, -1.0F, -2.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(-0.3F)),
         PartPose.offsetAndRotation(-3.0F, -1.25F, -3.65F, 1.306F, -0.158F, -0.3065F)
      );
      PartDefinition body_r1 = body.addOrReplaceChild(
         "body_r1",
         CubeListBuilder.create().texOffs(2, 31).addBox(-3.0F, -23.5F, -3.0F, 8.0F, 12.0F, 5.0F, new CubeDeformation(-0.1F)),
         PartPose.offsetAndRotation(0.0F, 5.5F, -5.4F, -0.8284F, -0.1809F, 0.2526F)
      );
      PartDefinition cube_r16 = body.addOrReplaceChild(
         "cube_r16",
         CubeListBuilder.create().texOffs(10, 11).addBox(-5.5608F, -9.426F, -5.0248F, 7.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-4.9118F, -11.1562F, 5.6542F, -0.2762F, 0.2359F, -0.7463F)
      );
      PartDefinition body_r2 = body.addOrReplaceChild(
         "body_r2",
         CubeListBuilder.create().texOffs(2, 31).addBox(-3.0F, -23.5F, -3.0F, 8.0F, 12.0F, 5.0F, new CubeDeformation(-0.1F)),
         PartPose.offsetAndRotation(5.0F, 6.5F, 5.6F, -0.0369F, 0.4347F, -0.6835F)
      );
      PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(6.0F, -6.0F, 1.0F));
      PartDefinition cube_r17 = left_arm.addOrReplaceChild(
         "cube_r17",
         CubeListBuilder.create().texOffs(0, 49).mirror().addBox(0.9302F, -4.2738F, -3.0278F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.2F)).mirror(false),
         PartPose.offsetAndRotation(0.0F, 0.0F, -1.5F, -1.3134F, 0.2975F, 0.2602F)
      );
      PartDefinition cube_r18 = left_arm.addOrReplaceChild(
         "cube_r18",
         CubeListBuilder.create().texOffs(4, 55).mirror().addBox(3.75F, -0.8614F, -0.4842F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.2F)).mirror(false),
         PartPose.offsetAndRotation(0.0F, 0.0F, -1.5F, -0.6545F, 0.0F, 0.0F)
      );
      PartDefinition left_arm_r2 = left_arm.addOrReplaceChild(
         "left_arm_r2",
         CubeListBuilder.create().texOffs(32, 0).mirror().addBox(0.0F, 0.0F, -3.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(-0.9F, -1.0F, 0.0F, -0.2521F, -0.432F, -0.0869F)
      );
      PartDefinition right_arm = partdefinition.addOrReplaceChild(
         "right_arm", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.0F, -8.0F, -2.5F, 0.0F, 0.4363F, 0.0F)
      );
      PartDefinition cube_r19 = right_arm.addOrReplaceChild(
         "cube_r19",
         CubeListBuilder.create().texOffs(13, 50).addBox(-1.3F, -1.1622F, -0.9643F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.2F)),
         PartPose.offsetAndRotation(-1.9642F, -0.8F, 1.7448F, -1.1154F, -0.444F, -1.2761F)
      );
      PartDefinition right_arm_r3 = right_arm.addOrReplaceChild(
         "right_arm_r3",
         CubeListBuilder.create().texOffs(35, 4).addBox(1.0F, 1.0F, -2.0F, 3.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-2.5642F, -2.0F, 2.0448F, -1.7782F, -0.0701F, 1.4046F)
      );
      PartDefinition left_leg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create().texOffs(24, 32).addBox(-1.4F, 0.0F, -1.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(1.9F, 12.0F, 0.0F)
      );
      PartDefinition cube_r20 = left_leg.addOrReplaceChild(
         "cube_r20",
         CubeListBuilder.create().texOffs(13, 50).addBox(5.3386F, 2.207F, -0.1766F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(2.7F, 0.0F, 3.0F, 1.0644F, 1.202F, 1.8179F)
      );
      PartDefinition right_leg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create().texOffs(40, 32).addBox(-2.1F, 0.0F, -1.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-2.4F, 12.0F, 0.0F)
      );
      PartDefinition cube_r21 = right_leg.addOrReplaceChild(
         "cube_r21",
         CubeListBuilder.create().texOffs(13, 50).mirror().addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(-2.05F, 6.0F, 1.0F, -0.4363F, 0.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 64, 64);
   }

   public void setupAnim(
      @NotNull Tier4BloodHordeEntity entity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch
   ) {
      super.setupAnim(entity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
      this.head.y = -10.0F;
      this.head.z = 0.0F;
      this.body.y = 6.0F;
      this.body.xRot = (float)Math.toRadians(47.0);
      this.leftArm.xRot = (float)(this.leftArm.xRot + Math.toRadians(80.0));
      this.rightArm.xRot = (float)(this.rightArm.xRot + Math.toRadians(90.0));
      this.rightArm.yRot = (float)(this.rightArm.yRot + Math.toRadians(20.0));
      this.leftArm.y = -8.0F;
      this.leftArm.x = 4.0F;
      this.rightArm.y = -10.0F;
      this.rightArm.x = -3.0F;
      this.rightArm.z = -2.0F;
   }
}
