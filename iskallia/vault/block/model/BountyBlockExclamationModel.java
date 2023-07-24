package iskallia.vault.block.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import iskallia.vault.VaultMod;
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

public class BountyBlockExclamationModel extends Model {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(VaultMod.id("exclamation_point"), "main");
   public static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("block/exclamation_point");
   public static final Material MATERIAL = new Material(TextureAtlas.LOCATION_BLOCKS, TEXTURE_LOCATION);
   private final ModelPart exclamation_point;

   public BountyBlockExclamationModel(ModelPart root) {
      super(RenderType::entityTranslucent);
      this.exclamation_point = root.getChild("exclamation_point");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition exclamation_point = partdefinition.addOrReplaceChild(
         "exclamation_point",
         CubeListBuilder.create()
            .texOffs(8, 11)
            .addBox(-1.0F, -4.0F, -0.5F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-1.0F, -14.0F, -0.5F, 2.0F, 10.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(10, 0)
            .addBox(-2.0F, -14.0F, -0.5F, 1.0F, 10.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(6, 0)
            .addBox(1.0F, -14.0F, -0.5F, 1.0F, 10.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(14, 3)
            .addBox(-2.0F, -3.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(14, 0)
            .addBox(1.0F, -3.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(4, 11)
            .addBox(-3.0F, -13.0F, -0.5F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 11)
            .addBox(2.0F, -13.0F, -0.5F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 24.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 32, 32);
   }

   public void renderToBuffer(
      PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha
   ) {
      this.exclamation_point.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
   }
}
