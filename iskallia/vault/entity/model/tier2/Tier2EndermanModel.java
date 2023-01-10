package iskallia.vault.entity.model.tier2;

import iskallia.vault.entity.entity.tier2.Tier2EndermanEntity;
import javax.annotation.Nonnull;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class Tier2EndermanModel extends EndermanModel<Tier2EndermanEntity> {
   protected ModelPart heart;

   public Tier2EndermanModel(ModelPart root) {
      super(root);
      ModelPart body = root.getChild("body");
      this.heart = body.getChild("heart");
   }

   public static LayerDefinition createBodyLayer() {
      float f = -14.0F;
      MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, -14.0F);
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartPose partpose = PartPose.offset(0.0F, -13.0F, 0.0F);
      partdefinition.addOrReplaceChild(
         "hat", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(-0.5F)), partpose
      );
      partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F), partpose);
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body", CubeListBuilder.create().texOffs(32, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F), PartPose.offset(0.0F, -14.0F, 0.0F)
      );
      PartDefinition heart = body.addOrReplaceChild(
         "heart",
         CubeListBuilder.create().texOffs(38, 3).addBox(-2.0F, -2.0F, -1.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 6.0F, 0.0F)
      );
      partdefinition.addOrReplaceChild(
         "right_arm", CubeListBuilder.create().texOffs(56, 0).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 30.0F, 2.0F), PartPose.offset(-5.0F, -12.0F, 0.0F)
      );
      partdefinition.addOrReplaceChild(
         "left_arm", CubeListBuilder.create().texOffs(56, 0).mirror().addBox(-1.0F, -2.0F, -1.0F, 2.0F, 30.0F, 2.0F), PartPose.offset(5.0F, -12.0F, 0.0F)
      );
      partdefinition.addOrReplaceChild(
         "right_leg", CubeListBuilder.create().texOffs(56, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 30.0F, 2.0F), PartPose.offset(-2.0F, -5.0F, 0.0F)
      );
      partdefinition.addOrReplaceChild(
         "left_leg", CubeListBuilder.create().texOffs(56, 0).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 30.0F, 2.0F), PartPose.offset(2.0F, -5.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 64, 32);
   }

   public void setupAnim(@Nonnull Tier2EndermanEntity entity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
      super.setupAnim(entity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
      this.heart.yRot = 0.2F;
      this.heart.xRot = 0.2F;
      this.heart.zRot = entity.tickCount * 0.034906585F;
   }
}
