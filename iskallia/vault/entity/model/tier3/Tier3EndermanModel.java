package iskallia.vault.entity.model.tier3;

import iskallia.vault.entity.entity.tier3.Tier3EndermanEntity;
import javax.annotation.Nonnull;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class Tier3EndermanModel extends EndermanModel<Tier3EndermanEntity> {
   protected ModelPart heart;

   public Tier3EndermanModel(ModelPart root) {
      super(root);
      ModelPart body = root.getChild("body");
      this.heart = body.getChild("heart");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create()
            .texOffs(0, 16)
            .addBox(-4.0F, -26.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(-0.5F))
            .texOffs(0, 0)
            .addBox(-4.0F, -24.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(-0.5F))
            .texOffs(24, 18)
            .addBox(-14.0F, -24.0F, -1.0F, 11.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(24, 0)
            .addBox(3.0F, -24.0F, -1.0F, 11.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(24, 32)
            .addBox(12.0F, -32.0F, -0.5F, 2.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(24, 32)
            .addBox(-14.0F, -32.0F, -0.5F, 2.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(7.0F, -28.0F, -0.5F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-9.0F, -28.0F, -0.5F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, -13.0F, 0.0F)
      );
      partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create()
            .texOffs(37, 4)
            .addBox(-4.0F, 4.0F, -5.0F, 1.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(0, 5)
            .addBox(-3.0F, 4.0F, -5.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 5)
            .addBox(1.0F, 4.0F, -5.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 5)
            .addBox(1.0F, 7.0F, -3.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 5)
            .addBox(-3.0F, 7.0F, -3.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(37, 4)
            .addBox(-4.0F, 7.0F, -3.0F, 1.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(37, 4)
            .addBox(3.0F, 7.0F, -3.0F, 1.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(37, 4)
            .addBox(3.0F, 4.0F, -5.0F, 1.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, -14.0F, 0.0F)
      );
      PartDefinition Body_r1 = body.addOrReplaceChild(
         "Body_r1",
         CubeListBuilder.create().texOffs(0, 32).addBox(-4.0F, -6.0F, 0.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, 0.3927F, 0.0F, 0.0F)
      );
      PartDefinition heart = body.addOrReplaceChild(
         "heart",
         CubeListBuilder.create().texOffs(45, 4).addBox(-2.0F, -2.0F, -1.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 6.0F, 0.0F)
      );
      PartDefinition right_arm = partdefinition.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create().texOffs(30, 30).addBox(-3.0F, -2.0F, -1.0F, 2.0F, 30.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-5.0F, -12.0F, 0.0F)
      );
      PartDefinition left_arm = partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create().texOffs(30, 30).mirror().addBox(-1.0F, -2.0F, -1.0F, 2.0F, 30.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offset(5.0F, -12.0F, 0.0F)
      );
      PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-2.0F, -2.0F, 0.0F));
      PartDefinition RightLeg_r1 = right_leg.addOrReplaceChild(
         "RightLeg_r1",
         CubeListBuilder.create()
            .texOffs(38, 37)
            .addBox(-3.0F, -11.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(24, 7)
            .addBox(-3.0F, -13.0F, -8.0F, 2.0F, 2.0F, 9.0F, new CubeDeformation(0.0F))
            .texOffs(38, 22)
            .addBox(-3.0F, -26.0F, -8.0F, 2.0F, 13.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(2.0F, 26.0F, 0.0F, -0.3927F, 0.0F, 0.0F)
      );
      PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(2.0F, -2.0F, 0.0F));
      PartDefinition LeftLeg_r1 = left_leg.addOrReplaceChild(
         "LeftLeg_r1",
         CubeListBuilder.create()
            .texOffs(38, 37)
            .mirror()
            .addBox(1.0F, -11.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F))
            .mirror(false)
            .texOffs(24, 7)
            .mirror()
            .addBox(1.0F, -13.0F, -8.0F, 2.0F, 2.0F, 9.0F, new CubeDeformation(0.0F))
            .mirror(false)
            .texOffs(38, 22)
            .mirror()
            .addBox(1.0F, -26.0F, -8.0F, 2.0F, 13.0F, 2.0F, new CubeDeformation(0.0F))
            .mirror(false),
         PartPose.offsetAndRotation(-2.0F, 26.0F, 0.0F, -0.3927F, 0.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 64, 64);
   }

   public void setupAnim(@Nonnull Tier3EndermanEntity entity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
      super.setupAnim(entity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
      this.heart.yRot = 0.2F;
      this.heart.xRot = 0.2F;
      this.heart.zRot = entity.tickCount * 0.034906585F;
      this.head.y = 2.0F;
      this.rightArm.x = -3.0F;
   }
}
