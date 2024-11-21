package iskallia.vault.entity.model.tank;

import iskallia.vault.entity.entity.tank.BloodTankEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class BloodTankModel extends HierarchicalModel<BloodTankEntity> {
   protected Map<Float, List<ModelPart>> breakParts = new HashMap<>();
   private final ModelPart root;
   private final ModelPart head;
   private final ModelPart body;
   private final ModelPart rightArm;
   private final ModelPart leftArm;
   private final ModelPart rightLeg;
   private final ModelPart leftLeg;

   public BloodTankModel(ModelPart root) {
      this.root = root;
      this.head = root.getChild("head");
      this.body = root.getChild("body");
      this.rightArm = root.getChild("right_arm");
      this.leftArm = root.getChild("left_arm");
      this.rightLeg = root.getChild("right_leg");
      this.leftLeg = root.getChild("left_leg");

      for (float threshold = 0.75F; threshold > 0.0F; threshold -= 0.25F) {
         int i = (int)(4.0F - threshold / 0.25F);
         List<ModelPart> parts = this.breakParts.computeIfAbsent(threshold, v -> new ArrayList<>());
         parts.add(this.head.getChild("headbreak" + i));
         parts.add(this.body.getChild("bodybreak" + i));
         parts.add(this.rightArm.getChild("rarmbreak" + i));
         parts.add(this.leftArm.getChild("larmbreak" + i));
         parts.add(this.rightLeg.getChild("rlegbreak" + i));
         parts.add(this.leftLeg.getChild("llegbreak" + i));
      }
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create()
            .texOffs(16, 72)
            .addBox(-2.5F, 9.0F, -2.0F, 5.0F, 0.25F, 4.0F, new CubeDeformation(0.5F))
            .texOffs(18, 78)
            .addBox(-7.5F, 3.0F, 2.0F, 5.0F, 0.25F, 0.0F, new CubeDeformation(0.5F))
            .texOffs(17, 82)
            .addBox(-7.5F, 3.0F, -4.0F, 6.0F, 0.25F, 0.0F, new CubeDeformation(0.5F))
            .texOffs(45, 11)
            .addBox(-7.5F, 3.0F, -3.0F, 0.0F, 0.25F, 4.0F, new CubeDeformation(0.5F))
            .texOffs(18, 78)
            .addBox(-7.5F, 5.0F, 2.0F, 5.0F, 0.25F, 0.0F, new CubeDeformation(0.5F))
            .texOffs(45, 11)
            .addBox(-7.5F, 5.0F, -3.0F, 0.0F, 0.25F, 4.0F, new CubeDeformation(0.5F))
            .texOffs(29, 80)
            .addBox(-7.5F, 5.0F, -4.0F, 5.0F, 0.25F, 0.0F, new CubeDeformation(0.5F))
            .texOffs(18, 78)
            .addBox(-7.5F, 7.0F, 2.0F, 5.0F, 0.25F, 0.0F, new CubeDeformation(0.5F))
            .texOffs(45, 11)
            .addBox(-7.5F, 7.0F, -3.0F, 0.0F, 0.25F, 4.0F, new CubeDeformation(0.5F))
            .texOffs(17, 80)
            .addBox(-7.5F, 7.0F, -4.0F, 4.0F, 0.25F, 0.0F, new CubeDeformation(0.5F))
            .texOffs(18, 78)
            .mirror()
            .addBox(2.5F, 3.0F, 2.0F, 5.0F, 0.25F, 0.0F, new CubeDeformation(0.5F))
            .mirror(false)
            .texOffs(45, 11)
            .mirror()
            .addBox(7.5F, 3.0F, -3.0F, 0.0F, 0.25F, 4.0F, new CubeDeformation(0.5F))
            .mirror(false)
            .texOffs(17, 82)
            .mirror()
            .addBox(1.5F, 3.0F, -4.0F, 6.0F, 0.25F, 0.0F, new CubeDeformation(0.5F))
            .mirror(false)
            .texOffs(29, 80)
            .mirror()
            .addBox(2.5F, 5.0F, -4.0F, 5.0F, 0.25F, 0.0F, new CubeDeformation(0.5F))
            .mirror(false)
            .texOffs(45, 11)
            .mirror()
            .addBox(7.5F, 5.0F, -3.0F, 0.0F, 0.25F, 4.0F, new CubeDeformation(0.5F))
            .mirror(false)
            .texOffs(18, 78)
            .mirror()
            .addBox(2.5F, 5.0F, 2.0F, 5.0F, 0.25F, 0.0F, new CubeDeformation(0.5F))
            .mirror(false)
            .texOffs(45, 11)
            .mirror()
            .addBox(7.5F, 7.0F, -3.0F, 0.0F, 0.25F, 4.0F, new CubeDeformation(0.5F))
            .mirror(false)
            .texOffs(18, 78)
            .mirror()
            .addBox(2.5F, 7.0F, 2.0F, 5.0F, 0.25F, 0.0F, new CubeDeformation(0.5F))
            .mirror(false)
            .texOffs(17, 80)
            .mirror()
            .addBox(3.5F, 7.0F, -4.0F, 4.0F, 0.25F, 0.0F, new CubeDeformation(0.5F))
            .mirror(false)
            .texOffs(0, 72)
            .addBox(-2.0F, 2.0F, -1.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.5F))
            .texOffs(0, 64)
            .addBox(6.5F, -1.75F, -3.0F, 2.5F, 1.75F, 6.0F, new CubeDeformation(0.5F))
            .texOffs(0, 64)
            .mirror()
            .addBox(-9.0F, -1.75F, -3.0F, 2.5F, 1.75F, 6.0F, new CubeDeformation(0.5F))
            .mirror(false)
            .texOffs(0, 51)
            .addBox(-5.5F, -1.75F, -5.0F, 11.0F, 3.0F, 10.0F, new CubeDeformation(0.5F))
            .texOffs(30, 73)
            .addBox(2.0F, 15.0F, -3.0F, 4.5F, 0.25F, 5.0F, new CubeDeformation(0.5F))
            .texOffs(33, 53)
            .addBox(-6.5F, 15.0F, -3.0F, 4.5F, 0.25F, 5.0F, new CubeDeformation(0.5F))
            .texOffs(0, 83)
            .addBox(-6.5F, 10.25F, -3.0F, 13.0F, 3.75F, 5.0F, new CubeDeformation(0.5F))
            .texOffs(0, 51)
            .addBox(-7.0F, 10.25F, -3.0F, -0.5F, 3.75F, 5.0F, new CubeDeformation(0.5F))
            .texOffs(0, 51)
            .mirror()
            .addBox(7.5F, 10.25F, -3.0F, -0.5F, 3.75F, 5.0F, new CubeDeformation(0.5F))
            .mirror(false),
         PartPose.offset(0.0F, -7.0F, 0.0F)
      );
      PartDefinition bodybreak1 = body.addOrReplaceChild(
         "bodybreak1",
         CubeListBuilder.create().texOffs(84, 41).addBox(-8.1F, 4.0F, -5.0F, 7.0F, 4.0F, 10.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition bodybreak2 = body.addOrReplaceChild(
         "bodybreak2",
         CubeListBuilder.create()
            .texOffs(84, 41)
            .mirror()
            .addBox(1.1F, 4.0F, -5.0F, 7.0F, 4.0F, 10.0F, new CubeDeformation(0.0F))
            .mirror(false)
            .texOffs(84, 19)
            .addBox(-8.25F, -2.3F, -6.0F, 8.0F, 7.0F, 12.0F, new CubeDeformation(0.0F))
            .texOffs(53, 56)
            .addBox(1.0F, 13.0F, -4.0F, 7.0F, 3.0F, 7.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition bodybreak3 = body.addOrReplaceChild(
         "bodybreak3",
         CubeListBuilder.create()
            .texOffs(53, 46)
            .addBox(1.0F, 10.0F, -4.0F, 7.0F, 3.0F, 7.0F, new CubeDeformation(0.0F))
            .texOffs(49, 33)
            .addBox(-8.0F, 10.0F, -4.0F, 9.0F, 6.0F, 7.0F, new CubeDeformation(0.0F))
            .texOffs(84, 19)
            .mirror()
            .addBox(0.25F, -2.3F, -6.0F, 8.0F, 7.0F, 12.0F, new CubeDeformation(0.0F))
            .mirror(false)
            .texOffs(89, 24)
            .mirror()
            .addBox(-3.75F, -2.3F, -1.0F, 8.0F, 7.0F, 7.0F, new CubeDeformation(-0.01F))
            .mirror(false),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition cube_r1 = bodybreak3.addOrReplaceChild(
         "cube_r1",
         CubeListBuilder.create().texOffs(29, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 12.0F, -4.0F, 0.0F, 0.0F, 0.7854F)
      );
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-4.0F, -12.0F, -5.5F, 8.0F, 7.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(64, 0)
            .addBox(-4.0F, -12.0F, -5.5F, 8.0F, 9.2F, 8.0F, new CubeDeformation(-0.2F)),
         PartPose.offset(0.0F, -7.0F, -2.0F)
      );
      PartDefinition head_r1 = head.addOrReplaceChild(
         "head_r1",
         CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -3.0F, -7.5F, 8.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -2.7F, 2.2F, 0.1309F, 0.0F, 0.0F)
      );
      PartDefinition headbreak1 = head.addOrReplaceChild(
         "headbreak1",
         CubeListBuilder.create()
            .texOffs(64, 101)
            .addBox(3.0F, -12.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(59, 93)
            .addBox(-5.25F, -8.75F, -6.0F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(93, 92)
            .addBox(1.48F, -11.1F, -4.2F, 2.77F, 5.0F, 7.0F, new CubeDeformation(-0.01F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition headbreak2 = head.addOrReplaceChild(
         "headbreak2",
         CubeListBuilder.create()
            .texOffs(73, 117)
            .addBox(-4.75F, -12.35F, -6.2F, 9.0F, 2.0F, 9.0F, new CubeDeformation(-0.02F))
            .texOffs(65, 116)
            .addBox(-0.5F, -11.0F, -6.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(72, 93)
            .addBox(2.1F, -14.0F, -6.1F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition headbreak3 = head.addOrReplaceChild(
         "headbreak3",
         CubeListBuilder.create()
            .texOffs(72, 104)
            .addBox(-3.9F, -13.0F, -3.1F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(86, 109)
            .addBox(-5.9F, -11.0F, 0.9F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(99, 109)
            .addBox(3.1F, -11.0F, 0.9F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(45, 102)
            .addBox(-0.75F, -12.1F, -4.2F, 5.0F, 4.0F, 7.0F, new CubeDeformation(0.0F))
            .texOffs(44, 116)
            .addBox(-4.75F, -11.1F, -4.2F, 6.25F, 5.0F, 7.0F, new CubeDeformation(-0.01F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition right_arm = partdefinition.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create().texOffs(0, 15).addBox(-15.0F, -2.5F, -3.0F, 6.0F, 30.0F, 6.0F, new CubeDeformation(-0.4F)),
         PartPose.offset(0.0F, -7.0F, 0.0F)
      );
      PartDefinition rarmbreak1 = right_arm.addOrReplaceChild(
         "rarmbreak1",
         CubeListBuilder.create().texOffs(67, 67).addBox(-15.0F, 1.0F, -3.0F, 6.0F, 7.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition rarmbreak2 = right_arm.addOrReplaceChild(
         "rarmbreak2",
         CubeListBuilder.create().texOffs(20, 66).addBox(-18.0F, -4.0F, -1.0F, 7.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition rarmbreak3 = right_arm.addOrReplaceChild(
         "rarmbreak3",
         CubeListBuilder.create()
            .texOffs(47, 18)
            .mirror()
            .addBox(-15.0F, 19.5F, -3.0F, 6.0F, 8.0F, 6.0F, new CubeDeformation(0.0F))
            .mirror(false)
            .texOffs(43, 72)
            .addBox(-16.0F, -2.5F, -4.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition left_arm = partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create().texOffs(0, 15).mirror().addBox(9.0F, -2.5F, -3.0F, 6.0F, 30.0F, 6.0F, new CubeDeformation(-0.4F)).mirror(false),
         PartPose.offset(0.0F, -7.0F, 0.0F)
      );
      PartDefinition larmbreak1 = left_arm.addOrReplaceChild(
         "larmbreak1",
         CubeListBuilder.create().texOffs(20, 66).mirror().addBox(11.0F, -4.0F, -1.0F, 7.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition larmbreak2 = left_arm.addOrReplaceChild(
         "larmbreak2",
         CubeListBuilder.create().texOffs(47, 18).addBox(9.0F, 19.5F, -3.0F, 6.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition larmbreak3 = left_arm.addOrReplaceChild(
         "larmbreak3",
         CubeListBuilder.create()
            .texOffs(43, 72)
            .mirror()
            .addBox(8.0F, -2.5F, -4.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
            .mirror(false)
            .texOffs(67, 67)
            .mirror()
            .addBox(9.0F, 1.0F, -3.0F, 6.0F, 7.0F, 6.0F, new CubeDeformation(0.0F))
            .mirror(false),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition right_leg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create().texOffs(24, 15).mirror().addBox(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F, new CubeDeformation(-0.4F)).mirror(false),
         PartPose.offset(-4.0F, 11.0F, 0.0F)
      );
      PartDefinition rlegbreak1 = right_leg.addOrReplaceChild(
         "rlegbreak1",
         CubeListBuilder.create().texOffs(25, 37).addBox(-3.5F, 6.0F, -3.0F, 6.0F, 7.0F, 5.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition rlegbreak2 = right_leg.addOrReplaceChild(
         "rlegbreak2",
         CubeListBuilder.create().texOffs(71, 19).mirror().addBox(-3.5F, -2.0F, -3.0F, 6.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition rlegbreak3 = right_leg.addOrReplaceChild("rlegbreak3", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      PartDefinition left_leg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create().texOffs(24, 15).addBox(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F, new CubeDeformation(-0.4F)),
         PartPose.offset(5.0F, 11.0F, 0.0F)
      );
      PartDefinition llegbreak1 = left_leg.addOrReplaceChild(
         "llegbreak1",
         CubeListBuilder.create().texOffs(71, 19).addBox(-3.5F, -2.0F, -3.0F, 6.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition llegbreak2 = left_leg.addOrReplaceChild(
         "llegbreak2",
         CubeListBuilder.create().texOffs(25, 37).mirror().addBox(-3.5F, 6.0F, -3.0F, 6.0F, 7.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition llegbreak3 = left_leg.addOrReplaceChild("llegbreak3", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      return LayerDefinition.create(meshdefinition, 128, 128);
   }

   @NotNull
   public ModelPart root() {
      return this.root;
   }

   public void setupAnim(@NotNull BloodTankEntity entity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
      this.head.yRot = pNetHeadYaw * (float) (Math.PI / 180.0);
      this.head.xRot = pHeadPitch * (float) (Math.PI / 180.0);
      this.rightLeg.xRot = -1.5F * Mth.triangleWave(pLimbSwing, 13.0F) * pLimbSwingAmount;
      this.leftLeg.xRot = 1.5F * Mth.triangleWave(pLimbSwing, 13.0F) * pLimbSwingAmount;
      this.rightLeg.yRot = 0.0F;
      this.leftLeg.yRot = 0.0F;
      float healthPercentage = entity.getHealth() / entity.getMaxHealth();
      this.breakParts.forEach((threshold, modelParts) -> modelParts.forEach(part -> part.visible = healthPercentage > threshold));
   }

   public void prepareMobModel(BloodTankEntity pEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick) {
      int i = pEntity.getAttackAnimationTick();
      if (i > 0) {
         this.rightArm.xRot = -2.0F + 1.5F * Mth.triangleWave(i - pPartialTick, 10.0F);
         this.leftArm.xRot = -2.0F + 1.5F * Mth.triangleWave(i - pPartialTick, 10.0F);
      }
   }
}
