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

public class PiercingJavelinModel extends EntityModel<VaultThrownJavelin> {
   public static final ModelLayerLocation MODEL_LOCATION = new ModelLayerLocation(VaultMod.id("piercing_javelin"), "main");
   protected final ModelPart bone;

   public PiercingJavelinModel(ModelPart root) {
      this.bone = root.getChild("bone");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition bone = partdefinition.addOrReplaceChild(
         "bone",
         CubeListBuilder.create()
            .texOffs(24, 0)
            .addBox(-9.0F, 5.0F, 7.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(27, 24)
            .addBox(-9.0F, -12.5F, 7.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(29, 15)
            .addBox(-9.0F, -20.0F, 7.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(5, 27)
            .addBox(-9.5F, -15.0F, 7.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(20, 19)
            .addBox(-10.0F, -13.5F, 6.5F, 4.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(5, 19)
            .addBox(-10.0F, -19.0F, 6.5F, 4.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(20, 24)
            .addBox(-7.0F, -21.0F, 7.5F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(10, 32)
            .addBox(-7.0F, -25.0F, 7.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(5, 0)
            .addBox(-12.5F, -30.75F, 8.0F, 9.0F, 18.0F, 0.0F, new CubeDeformation(0.0F))
            .texOffs(5, 32)
            .addBox(-10.0F, -25.0F, 7.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(32, 19)
            .addBox(-10.5F, -14.0F, 7.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(17, 19)
            .addBox(-6.5F, -14.0F, 7.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(27, 31)
            .addBox(-9.5F, 10.0F, 7.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(15, 31)
            .addBox(-7.5F, 10.0F, 7.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(24, 8)
            .addBox(-11.0F, -21.0F, 7.5F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-8.5F, -27.0F, 7.5F, 1.0F, 32.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(31, 8)
            .addBox(-8.5F, 11.0F, 7.5F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offset(8.0F, 24.0F, -8.0F)
      );
      PartDefinition cube_r1 = bone.addOrReplaceChild(
         "cube_r1",
         CubeListBuilder.create().texOffs(16, 27).addBox(-0.5F, -7.6241F, 3.9103F, 1.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-8.0F, -4.75F, 8.0F, 0.3927F, 0.0F, 0.0F)
      );
      PartDefinition cube_r2 = bone.addOrReplaceChild(
         "cube_r2",
         CubeListBuilder.create().texOffs(32, 31).addBox(3.9103F, -7.8741F, -0.5F, 0.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-8.0F, -4.75F, 8.0F, 0.0F, 0.0F, -0.3927F)
      );
      PartDefinition cube_r3 = bone.addOrReplaceChild(
         "cube_r3",
         CubeListBuilder.create()
            .texOffs(20, 33)
            .addBox(1.974F, -8.1543F, -0.8367F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(5, 19)
            .addBox(-0.1902F, -8.1543F, -1.8544F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-8.0F, -4.75F, 8.0F, 0.0F, -0.7854F, 0.0F)
      );
      PartDefinition cube_r4 = bone.addOrReplaceChild(
         "cube_r4",
         CubeListBuilder.create().texOffs(23, 33).addBox(-0.5F, -7.6241F, -3.9103F, 1.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-8.0F, -4.75F, 8.0F, -0.3927F, 0.0F, 0.0F)
      );
      PartDefinition cube_r5 = bone.addOrReplaceChild(
         "cube_r5",
         CubeListBuilder.create()
            .texOffs(0, 34)
            .addBox(-1.8275F, -8.1543F, -0.9402F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(35, 6)
            .addBox(-0.8098F, -8.1543F, -1.8544F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-8.0F, -4.75F, 8.0F, 0.0F, 0.7854F, 0.0F)
      );
      PartDefinition cube_r6 = bone.addOrReplaceChild(
         "cube_r6",
         CubeListBuilder.create().texOffs(33, 0).addBox(-3.9103F, -7.8741F, -0.5F, 0.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-8.0F, -4.75F, 8.0F, 0.0F, 0.0F, 0.3927F)
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
