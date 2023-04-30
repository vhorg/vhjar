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

public class ScatterJavelinModel extends EntityModel<VaultThrownJavelin> {
   public static final ModelLayerLocation MODEL_LOCATION = new ModelLayerLocation(VaultMod.id("scatter_javelin"), "main");
   protected final ModelPart bone;

   public ScatterJavelinModel(ModelPart root) {
      this.bone = root.getChild("bone");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition bone = partdefinition.addOrReplaceChild(
         "bone",
         CubeListBuilder.create()
            .texOffs(27, 0)
            .addBox(-9.0F, 5.0F, 7.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(14, 30)
            .addBox(-9.0F, -12.5F, 7.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(27, 8)
            .addBox(-9.5F, -15.0F, 7.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(17, 25)
            .addBox(-10.0F, -13.5F, 6.5F, 4.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(33, 29)
            .addBox(-10.5F, -14.0F, 7.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(33, 19)
            .addBox(-6.5F, -14.0F, 7.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(32, 24)
            .addBox(-8.5F, 8.0F, 7.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 18)
            .addBox(-8.5F, -23.0F, 7.5F, 1.0F, 28.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-14.5F, -29.75F, 8.0F, 13.0F, 17.0F, 0.0F, new CubeDeformation(0.0F))
            .texOffs(5, 18)
            .addBox(-11.5F, 9.0F, 8.0F, 7.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offset(8.0F, 24.0F, -8.0F)
      );
      PartDefinition cube_r1 = bone.addOrReplaceChild(
         "cube_r1",
         CubeListBuilder.create()
            .texOffs(23, 30)
            .addBox(0.0F, -3.5F, -0.55F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(5, 32)
            .addBox(0.0F, -3.5F, -0.55F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-8.0F, -14.75F, 8.0F, 0.0F, 0.0F, 0.3927F)
      );
      PartDefinition cube_r2 = bone.addOrReplaceChild(
         "cube_r2",
         CubeListBuilder.create()
            .texOffs(28, 30)
            .addBox(-1.0F, -3.5F, -0.55F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(31, 13)
            .addBox(-1.0F, -3.5F, -0.55F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-8.0F, -14.75F, 8.0F, 0.0F, 0.0F, -0.3927F)
      );
      PartDefinition cube_r3 = bone.addOrReplaceChild(
         "cube_r3",
         CubeListBuilder.create().texOffs(5, 26).addBox(-2.0F, -2.0F, -0.75F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-8.0F, -20.75F, 8.0F, 0.0F, 0.0F, -0.7854F)
      );
      PartDefinition cube_r4 = bone.addOrReplaceChild(
         "cube_r4",
         CubeListBuilder.create().texOffs(20, 18).addBox(-2.0F, -2.0F, -1.5F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-8.0F, -18.0F, 8.5F, 0.0F, 0.0F, -0.7854F)
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
