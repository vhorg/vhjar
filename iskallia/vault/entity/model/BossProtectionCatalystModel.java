package iskallia.vault.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.boss.BossProtectionCatalystEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class BossProtectionCatalystModel extends EntityModel<BossProtectionCatalystEntity> {
   public static final ModelLayerLocation MODEL_LOCATION = new ModelLayerLocation(VaultMod.id("boss_protection_catalyst"), "main");
   private final ModelPart bone;

   public BossProtectionCatalystModel(ModelPart root) {
      this.bone = root.getChild("bone");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild(
         "bone",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-14.0F, -14.0F, 2.0F, 12.0F, 12.0F, 12.0F, new CubeDeformation(0.0F))
            .texOffs(0, 24)
            .addBox(-9.0F, -16.0F, 0.0F, 2.0F, 16.0F, 16.0F, new CubeDeformation(0.0F))
            .texOffs(54, 42)
            .addBox(-16.0F, -16.0F, 7.0F, 7.0F, 16.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(41, 17)
            .addBox(-16.0F, -9.0F, 0.0F, 7.0F, 2.0F, 7.0F, new CubeDeformation(0.0F))
            .texOffs(36, 33)
            .addBox(-7.0F, -9.0F, 0.0F, 7.0F, 2.0F, 7.0F, new CubeDeformation(0.0F))
            .texOffs(20, 24)
            .addBox(-7.0F, -9.0F, 9.0F, 7.0F, 2.0F, 7.0F, new CubeDeformation(0.0F))
            .texOffs(36, 0)
            .addBox(-16.0F, -9.0F, 9.0F, 7.0F, 2.0F, 7.0F, new CubeDeformation(0.0F))
            .texOffs(36, 42)
            .addBox(-7.0F, -16.0F, 7.0F, 7.0F, 16.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(8.0F, 24.0F, -8.0F)
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }

   public void setupAnim(BossProtectionCatalystEntity pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
   }

   public void renderToBuffer(
      PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha
   ) {
      this.bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
   }
}
