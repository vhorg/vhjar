package iskallia.vault.block.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import iskallia.vault.VaultMod;
import javax.annotation.Nonnull;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;

public class PylonCrystalModel extends Model {
   public static final ModelLayerLocation MODEL_LOCATION = new ModelLayerLocation(VaultMod.id("pylon_crystal"), "main");
   public static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("block/pylon_crystal");
   public static final Material MATERIAL = new Material(TextureAtlas.LOCATION_BLOCKS, TEXTURE_LOCATION);
   protected ModelPart root;

   public PylonCrystalModel(ModelPart root) {
      super(RenderType::entityTranslucent);
      this.root = root;
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition crystal = partdefinition.addOrReplaceChild(
         "crystal",
         CubeListBuilder.create()
            .texOffs(0, 25)
            .addBox(-4.5F, -6.0F, -4.5F, 9.0F, 12.0F, 9.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-3.5F, -9.0F, -3.5F, 7.0F, 18.0F, 7.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 16.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 64, 64);
   }

   public void renderToBuffer(
      @Nonnull PoseStack poseStack, @Nonnull VertexConsumer vertexConsumer, int packedLight, int packetOverlay, float red, float green, float blue, float alpha
   ) {
      this.root.render(poseStack, vertexConsumer, packedLight, packetOverlay, red, green, blue, alpha);
   }

   public void setupAnimations() {
      this.root.yRot = (float)(10.0 * (System.currentTimeMillis() / 1000.0) % 360.0 * (Math.PI / 180.0));
      this.root.y = (float)(2.0 * Math.sin(30.0 * (System.currentTimeMillis() / 1000.0) % 360.0 * (Math.PI / 180.0)));
   }
}
