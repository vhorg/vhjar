package iskallia.vault.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.VaultThrownJavelin;
import javax.annotation.Nonnull;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class SightJavelinModel extends EntityModel<VaultThrownJavelin> {
   public static final ModelLayerLocation MODEL_LOCATION = new ModelLayerLocation(VaultMod.id("sight_javelin"), "main");
   protected final ModelPart bone;

   public SightJavelinModel(ModelPart root) {
      this.bone = root.getChild("bone");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition bone = partdefinition.addOrReplaceChild(
         "bone",
         CubeListBuilder.create()
            .texOffs(24, 9)
            .addBox(-1.0F, 5.0F, -1.0019F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(3, 31)
            .addBox(-1.0F, 3.0F, -1.0019F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(25, 26)
            .addBox(-1.0F, -12.5F, -1.0019F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(16, 26)
            .addBox(-1.0F, -23.0F, -1.0019F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(5, 26)
            .addBox(-1.5F, -15.0F, -1.0019F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(24, 0)
            .addBox(-2.0F, -13.5F, -1.5019F, 4.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(24, 5)
            .addBox(-2.0F, 4.5F, -1.2519F, 4.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(20, 18)
            .addBox(-2.0F, -19.0F, -1.5019F, 4.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(12, 33)
            .addBox(1.0F, -18.0F, -0.5019F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(35, 35)
            .addBox(-1.5F, -24.0F, -0.4769F, 1.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
            .texOffs(5, 0)
            .addBox(-4.5F, -29.75F, -0.0019F, 9.0F, 17.0F, 0.0F, new CubeDeformation(0.0F))
            .texOffs(35, 30)
            .addBox(0.5F, -24.0F, -0.4769F, 1.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
            .texOffs(24, 33)
            .addBox(-1.5F, 8.0F, -0.5019F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(19, 33)
            .addBox(0.5F, 8.0F, -0.5019F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(33, 9)
            .addBox(-3.0F, -18.0F, -0.5019F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-0.5F, -26.0F, -0.5019F, 1.0F, 31.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(33, 15)
            .addBox(-0.5F, 10.0F, -0.5019F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(5, 18)
            .addBox(-3.5F, 9.0F, -0.0019F, 7.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 24.0F, 0.0019F)
      );
      PartDefinition cube_r1 = bone.addOrReplaceChild(
         "cube_r1",
         CubeListBuilder.create().texOffs(35, 21).addBox(-0.5F, -7.6241F, 3.9103F, 1.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -4.75F, -0.0019F, 0.3927F, 0.0F, 0.0F)
      );
      PartDefinition cube_r2 = bone.addOrReplaceChild(
         "cube_r2",
         CubeListBuilder.create().texOffs(0, 33).addBox(3.9103F, -6.8741F, -0.5F, 0.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -4.75F, -0.0019F, 0.0F, 0.0F, -0.3927F)
      );
      PartDefinition cube_r3 = bone.addOrReplaceChild(
         "cube_r3",
         CubeListBuilder.create().texOffs(3, 36).addBox(1.974F, -8.1543F, -0.8367F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-0.125F, -4.75F, -0.1769F, 0.0F, -0.7854F, 0.0F)
      );
      PartDefinition cube_r4 = bone.addOrReplaceChild(
         "cube_r4",
         CubeListBuilder.create().texOffs(20, 18).addBox(-0.1902F, -8.1543F, -1.8544F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-0.1F, -4.75F, 0.1231F, 0.0F, -0.7854F, 0.0F)
      );
      PartDefinition cube_r5 = bone.addOrReplaceChild(
         "cube_r5",
         CubeListBuilder.create().texOffs(6, 36).addBox(-0.5F, -7.6241F, -3.9103F, 1.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -4.75F, -0.0019F, -0.3927F, 0.0F, 0.0F)
      );
      PartDefinition cube_r6 = bone.addOrReplaceChild(
         "cube_r6",
         CubeListBuilder.create()
            .texOffs(9, 36)
            .addBox(-1.8275F, -8.1543F, -0.9402F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(37, 5)
            .addBox(-1.6775F, 9.8457F, -0.9652F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.1F, -4.75F, -0.0019F, 0.0F, 0.7854F, 0.0F)
      );
      PartDefinition cube_r7 = bone.addOrReplaceChild(
         "cube_r7",
         CubeListBuilder.create()
            .texOffs(24, 0)
            .addBox(-0.9098F, -8.1543F, -1.5294F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
            .texOffs(38, 19)
            .addBox(-0.8098F, -26.1543F, -1.8544F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 13.25F, -0.0019F, 0.0F, 0.7854F, 0.0F)
      );
      PartDefinition cube_r8 = bone.addOrReplaceChild(
         "cube_r8",
         CubeListBuilder.create().texOffs(29, 33).addBox(-3.9603F, -6.8741F, -0.5F, 0.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 13.25F, -0.0019F, 0.0F, 0.0F, 0.3927F)
      );
      PartDefinition cube_r9 = bone.addOrReplaceChild(
         "cube_r9",
         CubeListBuilder.create().texOffs(37, 25).addBox(-0.5F, -7.6241F, -3.9103F, 1.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 13.25F, -0.0019F, -0.3927F, 0.0F, 0.0F)
      );
      PartDefinition cube_r10 = bone.addOrReplaceChild(
         "cube_r10",
         CubeListBuilder.create().texOffs(36, 0).addBox(-0.0652F, -8.1543F, -1.7545F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-0.15F, 13.25F, 0.1231F, 0.0F, -0.7854F, 0.0F)
      );
      PartDefinition cube_r11 = bone.addOrReplaceChild(
         "cube_r11",
         CubeListBuilder.create().texOffs(32, 33).addBox(3.8603F, -6.8741F, -0.5F, 0.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 13.25F, -0.0019F, 0.0F, 0.0F, -0.3927F)
      );
      PartDefinition cube_r12 = bone.addOrReplaceChild(
         "cube_r12",
         CubeListBuilder.create().texOffs(0, 38).addBox(1.749F, -8.1543F, -0.9117F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-0.175F, 13.25F, -0.1769F, 0.0F, -0.7854F, 0.0F)
      );
      PartDefinition cube_r13 = bone.addOrReplaceChild(
         "cube_r13",
         CubeListBuilder.create().texOffs(38, 15).addBox(-0.5F, -7.6241F, 3.9103F, 1.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 13.25F, -0.0019F, 0.3927F, 0.0F, 0.0F)
      );
      PartDefinition cube_r14 = bone.addOrReplaceChild(
         "cube_r14",
         CubeListBuilder.create().texOffs(34, 25).addBox(-3.9103F, -6.8741F, -0.5F, 0.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -4.75F, -0.0019F, 0.0F, 0.0F, 0.3927F)
      );
      return LayerDefinition.create(meshdefinition, 64, 64);
   }

   public void setupAnim(@Nonnull VaultThrownJavelin entity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
   }

   public void renderToBuffer(
      PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha
   ) {
      this.bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
   }
}
