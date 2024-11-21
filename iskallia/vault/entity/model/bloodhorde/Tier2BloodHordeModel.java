package iskallia.vault.entity.model.bloodhorde;

import iskallia.vault.entity.entity.bloodhorde.Tier2BloodHordeEntity;
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

public class Tier2BloodHordeModel extends ZombieModel<Tier2BloodHordeEntity> {
   public Tier2BloodHordeModel(ModelPart root) {
      super(root);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition hat = partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -7.5F, -5.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(2.0F, 3.0F, -7.0F, 0.0F, 0.0F, 0.2618F)
      );
      PartDefinition cube_r1 = head.addOrReplaceChild(
         "cube_r1",
         CubeListBuilder.create().texOffs(4, 55).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.2F)),
         PartPose.offsetAndRotation(-1.75F, -4.0F, 7.25F, 0.5214F, 0.3843F, 0.2121F)
      );
      PartDefinition cube_r2 = head.addOrReplaceChild(
         "cube_r2",
         CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.5F, -4.0F, 5.6F, 0.5664F, 0.5903F, 0.3269F)
      );
      PartDefinition cube_r3 = head.addOrReplaceChild(
         "cube_r3",
         CubeListBuilder.create().texOffs(29, 49).addBox(-4.0F, -5.0F, -4.0F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-2.0F, -2.0F, 3.0F, -0.5093F, 0.5733F, -0.8248F)
      );
      PartDefinition cube_r4 = head.addOrReplaceChild(
         "cube_r4",
         CubeListBuilder.create().texOffs(18, 52).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.2F)),
         PartPose.offsetAndRotation(4.5F, -3.0F, -3.0F, -0.3491F, 0.0F, 0.0F)
      );
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create().texOffs(0, 30).addBox(-4.0F, -6.0F, -3.4F, 8.0F, 12.0F, 6.0F, new CubeDeformation(-0.01F)),
         PartPose.offsetAndRotation(0.0F, 7.25F, -1.25F, 0.5672F, 0.0F, 0.0F)
      );
      PartDefinition cube_r5 = body.addOrReplaceChild(
         "cube_r5",
         CubeListBuilder.create().texOffs(0, 49).addBox(-1.0F, -1.0F, -2.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(-0.3F)),
         PartPose.offsetAndRotation(-2.0F, 1.75F, -3.65F, 1.306F, -0.158F, -0.3065F)
      );
      PartDefinition left_arm = partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create().texOffs(32, 0).addBox(-1.0F, 0.0F, -4.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(5.0F, 5.0F, 0.0F)
      );
      PartDefinition cube_r6 = left_arm.addOrReplaceChild(
         "cube_r6",
         CubeListBuilder.create().texOffs(13, 50).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.3F)),
         PartPose.offsetAndRotation(2.7F, 8.0F, -1.0F, -0.4363F, 0.0F, 0.0F)
      );
      PartDefinition right_arm = partdefinition.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create().texOffs(32, 16).addBox(-3.0F, -2.0F, -3.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-5.0F, 4.0F, -2.5F)
      );
      PartDefinition cube_r7 = right_arm.addOrReplaceChild(
         "cube_r7",
         CubeListBuilder.create().texOffs(0, 49).addBox(-1.0F, -1.0F, -2.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.2F)),
         PartPose.offsetAndRotation(-3.0F, -2.0F, 0.6F, -0.48F, 0.0F, -0.3927F)
      );
      PartDefinition cube_r8 = right_arm.addOrReplaceChild(
         "cube_r8",
         CubeListBuilder.create().texOffs(4, 55).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.2F)),
         PartPose.offsetAndRotation(-3.75F, 1.0F, 0.75F, 0.2182F, 0.0F, 0.0F)
      );
      PartDefinition left_leg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create().texOffs(24, 32).addBox(-1.9F, 0.0F, -1.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(1.9F, 12.0F, 0.0F)
      );
      PartDefinition right_leg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create().texOffs(40, 32).addBox(-2.1F, 0.0F, -1.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-1.9F, 12.0F, 0.0F)
      );
      PartDefinition cube_r9 = right_leg.addOrReplaceChild(
         "cube_r9",
         CubeListBuilder.create().texOffs(13, 50).mirror().addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(-2.05F, 6.0F, 1.0F, -0.4363F, 0.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 64, 64);
   }

   public void setupAnim(
      @NotNull Tier2BloodHordeEntity entity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch
   ) {
      super.setupAnim(entity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
      this.head.y = 1.0F;
      this.head.z = -6.0F;
      this.body.y = 6.0F;
      this.body.xRot = (float) (Math.PI / 8);
      this.leftArm.setRotation(0.0F, 0.0F, 0.0F);
      this.leftArm.y = 5.0F;
      this.rightArm.z = -6.0F;
   }
}
