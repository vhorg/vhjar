package iskallia.vault.entity.model.elite;

import iskallia.vault.client.gui.helper.Easing;
import iskallia.vault.util.IScalablePart;
import javax.annotation.Nonnull;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Monster;

public class EliteSpiderModel<T extends Monster> extends SpiderModel<T> {
   private final ModelPart body1;
   private final ModelPart egg1;
   private final ModelPart egg2;

   public EliteSpiderModel(ModelPart root) {
      super(root);
      this.body1 = root.getChild("body1");
      this.egg1 = this.body1.getChild("egg1");
      this.egg2 = this.body1.getChild("egg2");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create().texOffs(0, 30).addBox(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 15.0F, -3.0F)
      );
      PartDefinition body0 = partdefinition.addOrReplaceChild(
         "body0",
         CubeListBuilder.create().texOffs(32, 34).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 15.0F, 0.0F)
      );
      PartDefinition body1 = partdefinition.addOrReplaceChild("body1", CubeListBuilder.create(), PartPose.offset(0.0F, 15.0F, 9.0F));
      PartDefinition body4_r1 = body1.addOrReplaceChild(
         "body4_r1",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-2.0F, -21.0F, 14.0F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-7.0F, -20.0F, 4.0F, 14.0F, 15.0F, 15.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 9.0F, -9.0F, 0.2182F, 0.0F, 0.0F)
      );
      PartDefinition egg1 = body1.addOrReplaceChild("egg1", CubeListBuilder.create(), PartPose.offset(-1.5F, -14.8041F, -0.9616F));
      PartDefinition egg1_r1 = egg1.addOrReplaceChild(
         "egg1_r1",
         CubeListBuilder.create().texOffs(43, 0).addBox(-3.5F, -1.5F, -3.0F, 7.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2182F, 0.0F, 0.0F)
      );
      PartDefinition egg2 = body1.addOrReplaceChild("egg2", CubeListBuilder.create(), PartPose.offset(0.5F, -13.5584F, -4.2704F));
      PartDefinition egg2_r1 = egg2.addOrReplaceChild(
         "egg2_r1",
         CubeListBuilder.create().texOffs(0, 46).addBox(-2.5F, -1.0F, -2.5F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2182F, 0.0F, 0.0F)
      );
      PartDefinition right_hind_leg = partdefinition.addOrReplaceChild(
         "right_hind_leg",
         CubeListBuilder.create().texOffs(24, 30).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-4.0F, 15.0F, 2.0F, 0.0F, 0.7854F, -0.7854F)
      );
      PartDefinition left_hind_leg = partdefinition.addOrReplaceChild(
         "left_hind_leg",
         CubeListBuilder.create().texOffs(24, 30).addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(4.0F, 15.0F, 2.0F, 0.0F, -0.7854F, 0.7854F)
      );
      PartDefinition right_middle_hind_leg = partdefinition.addOrReplaceChild(
         "right_middle_hind_leg",
         CubeListBuilder.create().texOffs(24, 30).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-4.0F, 15.0F, 1.0F, 0.0F, 0.2618F, -0.6109F)
      );
      PartDefinition left_middle_hind_leg = partdefinition.addOrReplaceChild(
         "left_middle_hind_leg",
         CubeListBuilder.create().texOffs(24, 30).addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(4.0F, 15.0F, 1.0F, 0.0F, -0.2618F, 0.6109F)
      );
      PartDefinition right_middle_front_leg = partdefinition.addOrReplaceChild(
         "right_middle_front_leg",
         CubeListBuilder.create().texOffs(24, 30).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-4.0F, 15.0F, 0.0F, 0.0F, -0.2618F, -0.6109F)
      );
      PartDefinition left_middle_front_leg = partdefinition.addOrReplaceChild(
         "left_middle_front_leg",
         CubeListBuilder.create().texOffs(24, 30).addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(4.0F, 15.0F, 0.0F, 0.0F, 0.2618F, 0.6109F)
      );
      PartDefinition right_front_leg = partdefinition.addOrReplaceChild(
         "right_front_leg",
         CubeListBuilder.create().texOffs(24, 30).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-4.0F, 15.0F, -1.0F, 0.0F, -0.7854F, -0.7854F)
      );
      PartDefinition left_front_leg = partdefinition.addOrReplaceChild(
         "left_front_leg",
         CubeListBuilder.create().texOffs(24, 30).addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(4.0F, 15.0F, -1.0F, 0.0F, 0.7854F, 0.7854F)
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }

   public void setupAnim(@Nonnull T pEntity, float limbSwing, float pLimbSwingAmount, float ageInTicks, float pNetHeadYaw, float pHeadPitch) {
      super.setupAnim(pEntity, limbSwing, pLimbSwingAmount, ageInTicks, pNetHeadYaw, pHeadPitch);
      this.body1.xRot = -(Mth.cos(limbSwing * 0.6662F) * 0.15F) * pLimbSwingAmount;
      this.body1.zRot = -(Mth.cos(limbSwing * 0.6662F) * 0.15F) * pLimbSwingAmount;
      float egg1Scale = (Easing.EASE_IN_OUT_SINE.calc(ageInTicks / 20.0F) + 3.5F) / 4.0F;
      float egg1Scale2 = (Easing.EASE_IN_OUT_SINE.calc(ageInTicks / 5.0F) + 4.5F) / 4.0F;
      ((IScalablePart)this.egg1).getScale().set(egg1Scale, egg1Scale2, egg1Scale);
      float egg2Scale = (Easing.EASE_IN_OUT_SINE.calc(ageInTicks / 10.0F + pEntity.getId()) + 4.0F) / 4.0F;
      float egg2Scale2 = (Easing.EASE_IN_OUT_SINE.calc(ageInTicks / 3.0F + pEntity.getId()) + 4.0F) / 4.0F;
      ((IScalablePart)this.egg2).getScale().set(egg2Scale, egg2Scale2, egg2Scale);
   }
}
