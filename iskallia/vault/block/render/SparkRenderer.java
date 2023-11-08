package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import iskallia.vault.VaultMod;
import iskallia.vault.block.entity.SparkTileEntity;
import iskallia.vault.block.model.WendarrSparkSourceModel;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ForgeHooksClient;

public class SparkRenderer implements BlockEntityRenderer<SparkTileEntity> {
   public static final float CORNERS = 0.9375F;
   public static final float HEIGHT = 1.0F;
   public static final float MIN_Y = 0.0F;
   public static final float MAX_Y = 0.9375F;
   WendarrSparkSourceModel source;
   public static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("block/wendarr_sand");

   public SparkRenderer(Context context) {
      this.source = new WendarrSparkSourceModel(context.bakeLayer(WendarrSparkSourceModel.LAYER_LOCATION));
   }

   public void render(
      SparkTileEntity tile, float partialTicks, @Nonnull PoseStack matrixStack, @Nonnull MultiBufferSource buffer, int combinedLight, int combinedOverlay
   ) {
      Level world = tile.getLevel();
      if (world != null) {
         float t = tile.getLifeTimePercentage();
         float t2 = tile.getLifeTimePercentageOld();
         float t3 = Mth.lerp(partialTicks, t2, t);
         if (t3 != 0.0F) {
            matrixStack.pushPose();
            matrixStack.translate(0.5, 0.5, 0.5);
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(3600.0F * (1.0F - t3) * (1.0F - t3) + tile.getOffsetTicks()));
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(2400.0F * (1.0F - t3) * (1.0F - t3) + tile.getOffsetTicks()));
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(1600.0F * (1.0F - t3) * (1.0F - t3) + tile.getOffsetTicks()));
            matrixStack.translate(-0.5, -0.5, -0.5);
            VertexConsumer vertexConsumer = WendarrSparkSourceModel.MATERIAL.buffer(buffer, RenderType::entityTranslucent);
            this.source.renderToBuffer(matrixStack, vertexConsumer, 15728880, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStack.popPose();
            VertexConsumer vertexBuilder = buffer.getBuffer(RenderType.translucentNoCrumbling());
            TextureAtlasSprite sprite = (TextureAtlasSprite)Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(TEXTURE_LOCATION);
            float time = t3 * 14.0F / 16.0F;
            float height = 0.9375F * time;
            float minU = sprite.getU(15.0);
            float maxU = sprite.getU(1.0);
            float minV = sprite.getV(15.0);
            float maxV = sprite.getV(1.0);
            float minUHeight = sprite.getU(1.0F * time * 16.0F);
            float maxUHeight = sprite.getU(0.0F * time * 16.0F);
            float minVHeight = sprite.getV(15.0);
            float maxVHeight = sprite.getV(1.0);
            Matrix4f matrix = matrixStack.last().pose();
            float shading = 1.0F;
            vertexBuilder.vertex(matrix, 0.0625F, height + 0.0625F, 0.0625F)
               .color(1.0F * shading, 1.0F * shading, 1.0F * shading, 1.0F)
               .uv(minU, minV)
               .uv2(combinedLight)
               .normal(-1.0F, 0.0F, 0.0F)
               .endVertex();
            vertexBuilder.vertex(matrix, 0.0625F, height + 0.0625F, 0.9375F)
               .color(1.0F * shading, 1.0F * shading, 1.0F * shading, 1.0F)
               .uv(minU, maxV)
               .uv2(combinedLight)
               .normal(-1.0F, 0.0F, 0.0F)
               .endVertex();
            vertexBuilder.vertex(matrix, 0.9375F, height + 0.0625F, 0.9375F)
               .color(1.0F * shading, 1.0F * shading, 1.0F * shading, 1.0F)
               .uv(maxU, maxV)
               .uv2(combinedLight)
               .normal(-1.0F, 0.0F, 0.0F)
               .endVertex();
            vertexBuilder.vertex(matrix, 0.9375F, height + 0.0625F, 0.0625F)
               .color(1.0F * shading, 1.0F * shading, 1.0F * shading, 1.0F)
               .uv(maxU, minV)
               .uv2(combinedLight)
               .normal(-1.0F, 0.0F, 0.0F)
               .endVertex();
            vertexBuilder.vertex(matrix, 0.9375F, 0.0625F, 0.0625F)
               .color(1.0F * shading, 1.0F * shading, 1.0F * shading, 1.0F)
               .uv(minU, minV)
               .uv2(combinedLight)
               .normal(-1.0F, 0.0F, 0.0F)
               .endVertex();
            vertexBuilder.vertex(matrix, 0.9375F, 0.0625F, 0.9375F)
               .color(1.0F * shading, 1.0F * shading, 1.0F * shading, 1.0F)
               .uv(minU, maxV)
               .uv2(combinedLight)
               .normal(-1.0F, 0.0F, 0.0F)
               .endVertex();
            vertexBuilder.vertex(matrix, 0.0625F, 0.0625F, 0.9375F)
               .color(1.0F * shading, 1.0F * shading, 1.0F * shading, 1.0F)
               .uv(maxU, maxV)
               .uv2(combinedLight)
               .normal(-1.0F, 0.0F, 0.0F)
               .endVertex();
            vertexBuilder.vertex(matrix, 0.0625F, 0.0625F, 0.0625F)
               .color(1.0F * shading, 1.0F * shading, 1.0F * shading, 1.0F)
               .uv(maxU, minV)
               .uv2(combinedLight)
               .normal(-1.0F, 0.0F, 0.0F)
               .endVertex();
            vertexBuilder.vertex(matrix, 0.9375F, height + 0.0625F, 0.0625F)
               .color(1.0F * shading, 1.0F * shading, 1.0F * shading, 1.0F)
               .uv(minUHeight, minVHeight)
               .uv2(combinedLight)
               .normal(-1.0F, 0.0F, 0.0F)
               .endVertex();
            vertexBuilder.vertex(matrix, 0.9375F, height + 0.0625F, 0.9375F)
               .color(1.0F * shading, 1.0F * shading, 1.0F * shading, 1.0F)
               .uv(minUHeight, maxVHeight)
               .uv2(combinedLight)
               .normal(-1.0F, 0.0F, 0.0F)
               .endVertex();
            vertexBuilder.vertex(matrix, 0.9375F, 0.0625F, 0.9375F)
               .color(1.0F * shading, 1.0F * shading, 1.0F * shading, 1.0F)
               .uv(maxUHeight, maxVHeight)
               .uv2(combinedLight)
               .normal(-1.0F, 0.0F, 0.0F)
               .endVertex();
            vertexBuilder.vertex(matrix, 0.9375F, 0.0625F, 0.0625F)
               .color(1.0F * shading, 1.0F * shading, 1.0F * shading, 1.0F)
               .uv(maxUHeight, minVHeight)
               .uv2(combinedLight)
               .normal(-1.0F, 0.0F, 0.0F)
               .endVertex();
            vertexBuilder.vertex(matrix, 0.0625F, height + 0.0625F, 0.0625F)
               .color(1.0F * shading, 1.0F * shading, 1.0F * shading, 1.0F)
               .uv(minUHeight, minVHeight)
               .uv2(combinedLight)
               .normal(0.0F, 0.0F, -1.0F)
               .endVertex();
            vertexBuilder.vertex(matrix, 0.9375F, height + 0.0625F, 0.0625F)
               .color(1.0F * shading, 1.0F * shading, 1.0F * shading, 1.0F)
               .uv(minUHeight, maxVHeight)
               .uv2(combinedLight)
               .normal(0.0F, 0.0F, -1.0F)
               .endVertex();
            vertexBuilder.vertex(matrix, 0.9375F, 0.0625F, 0.0625F)
               .color(1.0F * shading, 1.0F * shading, 1.0F * shading, 1.0F)
               .uv(maxUHeight, maxVHeight)
               .uv2(combinedLight)
               .normal(0.0F, 0.0F, -1.0F)
               .endVertex();
            vertexBuilder.vertex(matrix, 0.0625F, 0.0625F, 0.0625F)
               .color(1.0F * shading, 1.0F * shading, 1.0F * shading, 1.0F)
               .uv(maxUHeight, minVHeight)
               .uv2(combinedLight)
               .normal(0.0F, 0.0F, -1.0F)
               .endVertex();
            vertexBuilder.vertex(matrix, 0.0625F, height + 0.0625F, 0.9375F)
               .color(1.0F * shading, 1.0F * shading, 1.0F * shading, 1.0F)
               .uv(minUHeight, minVHeight)
               .uv2(combinedLight)
               .normal(-1.0F, 0.0F, 0.0F)
               .endVertex();
            vertexBuilder.vertex(matrix, 0.0625F, height + 0.0625F, 0.0625F)
               .color(1.0F * shading, 1.0F * shading, 1.0F * shading, 1.0F)
               .uv(minUHeight, maxVHeight)
               .uv2(combinedLight)
               .normal(-1.0F, 0.0F, 0.0F)
               .endVertex();
            vertexBuilder.vertex(matrix, 0.0625F, 0.0625F, 0.0625F)
               .color(1.0F * shading, 1.0F * shading, 1.0F * shading, 1.0F)
               .uv(maxUHeight, maxVHeight)
               .uv2(combinedLight)
               .normal(-1.0F, 0.0F, 0.0F)
               .endVertex();
            vertexBuilder.vertex(matrix, 0.0625F, 0.0625F, 0.9375F)
               .color(1.0F * shading, 1.0F * shading, 1.0F * shading, 1.0F)
               .uv(maxUHeight, minVHeight)
               .uv2(combinedLight)
               .normal(-1.0F, 0.0F, 0.0F)
               .endVertex();
            vertexBuilder.vertex(matrix, 0.9375F, height + 0.0625F, 0.9375F)
               .color(1.0F * shading, 1.0F * shading, 1.0F * shading, 1.0F)
               .uv(minUHeight, minVHeight)
               .uv2(combinedLight)
               .normal(0.0F, 0.0F, -1.0F)
               .endVertex();
            vertexBuilder.vertex(matrix, 0.0625F, height + 0.0625F, 0.9375F)
               .color(1.0F * shading, 1.0F * shading, 1.0F * shading, 1.0F)
               .uv(minUHeight, maxVHeight)
               .uv2(combinedLight)
               .normal(0.0F, 0.0F, -1.0F)
               .endVertex();
            vertexBuilder.vertex(matrix, 0.0625F, 0.0625F, 0.9375F)
               .color(1.0F * shading, 1.0F * shading, 1.0F * shading, 1.0F)
               .uv(maxUHeight, maxVHeight)
               .uv2(combinedLight)
               .normal(0.0F, 0.0F, -1.0F)
               .endVertex();
            vertexBuilder.vertex(matrix, 0.9375F, 0.0625F, 0.9375F)
               .color(1.0F * shading, 1.0F * shading, 1.0F * shading, 1.0F)
               .uv(maxUHeight, minVHeight)
               .uv2(combinedLight)
               .normal(0.0F, 0.0F, -1.0F)
               .endVertex();
         }
      }
   }

   private static void renderBlockState(
      BlockState state, PoseStack matrixStack, MultiBufferSource buffer, BlockRenderDispatcher blockRenderer, Level world, BlockPos pos
   ) {
      try {
         for (RenderType type : RenderType.chunkBufferLayers()) {
            if (ItemBlockRenderTypes.canRenderInLayer(state, type)) {
               renderBlockState(state, matrixStack, buffer, blockRenderer, world, pos, type);
            }
         }
      } catch (Exception var8) {
      }
   }

   public static void renderBlockState(
      BlockState state, PoseStack matrixStack, MultiBufferSource buffer, BlockRenderDispatcher blockRenderer, Level world, BlockPos pos, RenderType type
   ) {
      ForgeHooksClient.setRenderType(type);
      blockRenderer.getModelRenderer()
         .tesselateBlock(
            world, blockRenderer.getBlockModel(state), state, pos, matrixStack, buffer.getBuffer(type), false, world.random, 0L, OverlayTexture.NO_OVERLAY
         );
      ForgeHooksClient.setRenderType(null);
   }
}
