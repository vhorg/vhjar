package iskallia.vault.entity.model.bloodhorde;

import iskallia.vault.entity.entity.bloodhorde.Tier5BloodHordeEntity;
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

public class Tier5BloodHordeModel extends ZombieModel<Tier5BloodHordeEntity> {
   public Tier5BloodHordeModel(ModelPart root) {
      super(root);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition hat = partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -7.5F, -5.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(-0.6F)),
         PartPose.offsetAndRotation(1.0F, 8.0F, -8.0F, -0.1309F, 0.0F, 0.2182F)
      );
      PartDefinition cube_r1 = head.addOrReplaceChild(
         "cube_r1",
         CubeListBuilder.create().texOffs(13, 50).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.3F)),
         PartPose.offsetAndRotation(2.0F, -3.5F, -4.0F, 0.6581F, 0.1682F, 0.5364F)
      );
      PartDefinition cube_r2 = head.addOrReplaceChild(
         "cube_r2",
         CubeListBuilder.create().texOffs(18, 52).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 3.0F, new CubeDeformation(-0.2F)),
         PartPose.offsetAndRotation(4.2F, -3.0F, -3.0F, -0.3491F, 0.0F, 0.0F)
      );
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create()
            .texOffs(0, 24)
            .addBox(-7.0F, -6.0F, -6.0F, 14.0F, 12.0F, 12.0F, new CubeDeformation(0.0F))
            .texOffs(2, 26)
            .addBox(-8.0F, -5.0F, -5.0F, 16.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
            .texOffs(6, 28)
            .addBox(-5.0F, -8.0F, -4.0F, 10.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(18, 36)
            .addBox(-5.0F, -4.0F, -8.0F, 10.0F, 8.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(18, 36)
            .addBox(-5.0F, -4.0F, 5.0F, 10.0F, 8.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(8, 17)
            .addBox(-6.0F, 3.0F, -4.0F, 12.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 6.5F, 0.0F, 0.1745F, 0.0F, 0.0F)
      );
      PartDefinition cube_r3 = body.addOrReplaceChild(
         "cube_r3",
         CubeListBuilder.create().texOffs(29, 49).addBox(-1.0F, -5.0F, -1.0F, 5.0F, 5.0F, 5.0F, new CubeDeformation(-0.4F)),
         PartPose.offsetAndRotation(8.0F, 0.0F, 9.0F, 3.1347F, -0.2494F, 2.8261F)
      );
      PartDefinition cube_r4 = body.addOrReplaceChild(
         "cube_r4",
         CubeListBuilder.create().texOffs(11, 12).addBox(-6.0F, -7.0F, -5.0F, 7.0F, 7.0F, 6.0F, new CubeDeformation(-0.4F)),
         PartPose.offsetAndRotation(7.0F, -2.0F, -2.0F, -0.6327F, -1.0852F, 0.1763F)
      );
      PartDefinition cube_r5 = body.addOrReplaceChild(
         "cube_r5",
         CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -6.0F, -5.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(-0.4F)),
         PartPose.offsetAndRotation(-9.0F, -2.0F, -7.0F, -2.7047F, -0.6398F, 3.0587F)
      );
      PartDefinition cube_r6 = body.addOrReplaceChild(
         "cube_r6",
         CubeListBuilder.create().texOffs(44, 11).addBox(-1.0F, -4.0F, -4.0F, 5.0F, 4.0F, 5.0F, new CubeDeformation(-0.2F)),
         PartPose.offsetAndRotation(-3.5F, -7.0F, -4.0F, 0.746F, -0.0964F, -0.0887F)
      );
      PartDefinition cube_r7 = body.addOrReplaceChild(
         "cube_r7",
         CubeListBuilder.create().texOffs(44, 11).addBox(-1.0F, -4.0F, -4.0F, 5.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-5.0F, 6.0F, 4.0F, -2.0345F, -0.0343F, -0.4266F)
      );
      PartDefinition cube_r8 = body.addOrReplaceChild(
         "cube_r8",
         CubeListBuilder.create().texOffs(44, 11).addBox(-1.0F, -4.0F, -4.0F, 5.0F, 4.0F, 5.0F, new CubeDeformation(-0.6F)),
         PartPose.offsetAndRotation(5.0F, -5.0F, 5.5F, -0.4097F, -0.2815F, 1.2109F)
      );
      PartDefinition cube_r9 = body.addOrReplaceChild(
         "cube_r9",
         CubeListBuilder.create().texOffs(18, 52).addBox(-1.0F, -3.0F, -2.0F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-8.0F, -4.0F, -8.0F, -2.4946F, 0.8249F, -2.738F)
      );
      PartDefinition right_leg_r1 = body.addOrReplaceChild(
         "right_leg_r1",
         CubeListBuilder.create().texOffs(29, 13).mirror().addBox(-3.0F, 0.0F, -2.0F, 3.0F, 12.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(-3.0F, -0.5F, -1.0F, -1.0519F, 0.6046F, -0.4026F)
      );
      PartDefinition right_leg_r2 = body.addOrReplaceChild(
         "right_leg_r2",
         CubeListBuilder.create().texOffs(21, 11).mirror().addBox(-3.0F, -12.0F, -1.0F, 3.0F, 12.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(-3.0F, 0.5F, 1.0F, -0.9156F, -0.4896F, 0.476F)
      );
      PartDefinition left_leg_r1 = body.addOrReplaceChild(
         "left_leg_r1",
         CubeListBuilder.create().texOffs(25, 33).addBox(0.0F, -12.0F, -1.0F, 3.0F, 12.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(3.0F, 1.5F, 0.0F, -0.2059F, 0.1889F, 0.3103F)
      );
      PartDefinition cube_r10 = body.addOrReplaceChild(
         "cube_r10",
         CubeListBuilder.create().texOffs(13, 50).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.2F)),
         PartPose.offsetAndRotation(10.0F, -5.0F, -3.0F, -0.1745F, -0.2618F, 0.6109F)
      );
      PartDefinition cube_r11 = body.addOrReplaceChild(
         "cube_r11",
         CubeListBuilder.create().texOffs(0, 49).addBox(-1.0F, -3.0F, -2.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-5.0F, 3.25F, 6.5F, -1.3446F, -0.6855F, -0.1447F)
      );
      PartDefinition cube_r12 = body.addOrReplaceChild(
         "cube_r12",
         CubeListBuilder.create().texOffs(0, 49).addBox(-1.0F, -3.0F, -2.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(-0.2F)),
         PartPose.offsetAndRotation(5.0F, 3.0F, -5.0F, 1.1913F, -0.4826F, 0.5953F)
      );
      PartDefinition cube_r13 = body.addOrReplaceChild(
         "cube_r13",
         CubeListBuilder.create().texOffs(0, 49).addBox(-1.0F, -3.0F, -2.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -2.0F, 10.0F, 1.3605F, 0.1505F, -2.8612F)
      );
      PartDefinition cube_r14 = body.addOrReplaceChild(
         "cube_r14",
         CubeListBuilder.create().texOffs(4, 55).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(1.0F, -6.0F, 5.0F, 0.7904F, 0.4738F, 0.1533F)
      );
      PartDefinition body_r1 = body.addOrReplaceChild(
         "body_r1",
         CubeListBuilder.create().texOffs(0, 26).addBox(-5.0F, -2.5F, -4.0F, 8.0F, 5.0F, 8.0F, new CubeDeformation(-0.4F)),
         PartPose.offsetAndRotation(0.0F, -7.5F, 0.0F, 0.098F, 0.5414F, -0.1506F)
      );
      PartDefinition left_arm = partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create().texOffs(32, 0).addBox(-1.0F, -2.0F, -3.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(9.0F, 7.0F, 0.0F)
      );
      PartDefinition cube_r15 = left_arm.addOrReplaceChild(
         "cube_r15",
         CubeListBuilder.create().texOffs(13, 50).addBox(5.3386F, 2.207F, -0.1766F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.2F)),
         PartPose.offsetAndRotation(-1.0F, 5.0F, 3.5F, -1.2179F, 1.0404F, -0.5386F)
      );
      PartDefinition cube_r16 = left_arm.addOrReplaceChild(
         "cube_r16",
         CubeListBuilder.create().texOffs(13, 50).addBox(2.7F, 4.4378F, 1.5357F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.3F)),
         PartPose.offsetAndRotation(-1.0F, 2.0F, 4.75F, -1.309F, 0.0F, 0.0F)
      );
      PartDefinition cube_r17 = left_arm.addOrReplaceChild(
         "cube_r17",
         CubeListBuilder.create().texOffs(18, 52).addBox(-1.0F, -3.0F, -2.0F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(4.0F, 0.5F, 0.0F, 0.2597F, 0.0338F, -0.1265F)
      );
      PartDefinition right_arm = partdefinition.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create().texOffs(32, 16).addBox(-3.0F, -2.0F, -3.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-9.0F, 4.0F, 0.0F)
      );
      PartDefinition cube_r18 = right_arm.addOrReplaceChild(
         "cube_r18",
         CubeListBuilder.create().texOffs(0, 49).addBox(-1.0F, -1.0F, -2.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.3F)),
         PartPose.offsetAndRotation(-3.0F, -2.0F, 0.6F, -0.48F, 0.0F, -0.3927F)
      );
      PartDefinition cube_r19 = right_arm.addOrReplaceChild(
         "cube_r19",
         CubeListBuilder.create().texOffs(4, 55).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.3F)),
         PartPose.offsetAndRotation(-3.75F, 1.0F, 0.75F, 0.2182F, 0.0F, 0.0F)
      );
      PartDefinition cube_r20 = right_arm.addOrReplaceChild(
         "cube_r20",
         CubeListBuilder.create().texOffs(4, 55).addBox(-5.6471F, 1.4182F, 2.2165F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.2F)),
         PartPose.offsetAndRotation(1.0F, 2.0F, 0.5F, -0.9994F, -0.9747F, 0.8348F)
      );
      PartDefinition left_leg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create().texOffs(24, 32).addBox(-1.9F, 0.0F, -1.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(2.9F, 12.0F, 0.0F)
      );
      PartDefinition cube_r21 = left_leg.addOrReplaceChild(
         "cube_r21",
         CubeListBuilder.create().texOffs(13, 50).addBox(5.3386F, 2.207F, -0.1766F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(2.2F, 0.0F, 3.0F, 1.0644F, 1.202F, 1.8179F)
      );
      PartDefinition right_leg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create().texOffs(40, 32).addBox(-2.1F, 0.0F, -1.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-2.9F, 12.0F, 0.0F)
      );
      PartDefinition cube_r22 = right_leg.addOrReplaceChild(
         "cube_r22",
         CubeListBuilder.create().texOffs(18, 52).addBox(-1.0F, -3.0F, -2.0F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(1.9F, 8.5F, 0.0F, 0.3054F, 0.6109F, 0.0F)
      );
      PartDefinition cube_r23 = right_leg.addOrReplaceChild(
         "cube_r23",
         CubeListBuilder.create().texOffs(13, 50).mirror().addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(-2.05F, 6.0F, 1.0F, -0.4363F, 0.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 64, 64);
   }

   public void setupAnim(
      @NotNull Tier5BloodHordeEntity entity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch
   ) {
      super.setupAnim(entity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
      this.body.y = 4.0F;
      this.rightArm.x = -10.0F;
      this.leftArm.x = 9.0F;
      this.leftArm.y = 4.0F;
      this.leftArm.xRot = 0.0F;
   }
}
