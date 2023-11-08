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

public class WendarrSparkSourceModel extends Model {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(VaultMod.id("spark_source"), "main");
   public static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("block/spark");
   public static final Material MATERIAL = new Material(TextureAtlas.LOCATION_BLOCKS, TEXTURE_LOCATION);
   private final ModelPart source;

   public WendarrSparkSourceModel(ModelPart root) {
      super(RenderType::entityTranslucent);
      this.source = root.getChild("source");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition source = partdefinition.addOrReplaceChild(
         "source",
         CubeListBuilder.create()
            .texOffs(0, 15)
            .addBox(6.0F, -3.5F, -10.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(5.0F, -4.5F, -11.5F, 7.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-0.5F, 9.0F, 16.0F)
      );
      return LayerDefinition.create(meshdefinition, 32, 32);
   }

   public void renderToBuffer(
      PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha
   ) {
      this.source.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
   }
}
