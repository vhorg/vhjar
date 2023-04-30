package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import iskallia.vault.block.entity.TotemTileEntity;
import iskallia.vault.init.ModRenderTypes;
import java.util.Random;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

public abstract class TotemRenderer<T extends TotemTileEntity> implements BlockEntityRenderer<T> {
   private static final int GLYPH_COUNT = 16;
   private static final float GLYPH_ALPHA = 0.8F;
   private static final float GLYPH_RING_RADIUS = 1.0F;
   private static final float GLYPH_QUAD_SIZE_X = 0.125F;
   private static final float GLYPH_QUAD_SIZE_Y = 0.125F;
   private static final int GLYPH_TEXTURE_GLYPH_WIDTH = 8;
   private static final int GLYPH_TEXTURE_GLYPH_HEIGHT = 8;
   private static final int GLYPH_TEXTURE_WIDTH = 128;
   private static final int GLYPH_TEXTURE_HEIGHT = 40;
   private static final int GLYPH_TEXTURE_COLUMNS = 16;
   private static final float GLYPH_TEXTURE_GLYPH_U = 0.0625F;
   private static final float GLYPH_TEXTURE_GLYPH_V = 0.2F;
   private static final int GLYPH_INDEX_MIN = 3;
   private static final int GLYPH_INDEX_MAX = 60;
   private static final float[] GLYPH_RING_VERTICES = new float[192];

   public TotemRenderer(Context context) {
   }

   @ParametersAreNonnullByDefault
   public void render(T blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
      VertexConsumer buffer = bufferSource.getBuffer(ModRenderTypes.TOTEM_GLYPH_EFFECT);
      TotemTileEntity.RenderContext renderContext = blockEntity.getRenderContext();
      poseStack.pushPose();
      poseStack.translate(0.5, 0.5, 0.5);
      if (!Minecraft.getInstance().isPaused()) {
         renderContext.rotationAngleRadians += partialTick * 0.01F;
      }

      poseStack.mulPose(Quaternion.fromXYZ(0.0F, renderContext.rotationAngleRadians, 0.0F));
      if (renderContext.glyphIndices == null || renderContext.glyphIndices.length != 16) {
         renderContext.glyphIndices = this.generateGlyphIndices(blockEntity.getBlockPos());
      }

      Vector3f glyphColor = blockEntity.getParticleEffectColor();

      for (int i = 0; i < 16; i++) {
         float u0 = renderContext.glyphIndices[i] % 16 * 0.0625F;
         float v0 = renderContext.glyphIndices[i] / 16 * 0.2F;
         float u1 = (renderContext.glyphIndices[i] % 16 + 1) * 0.0625F;
         float v1 = (renderContext.glyphIndices[i] / 16 + 1) * 0.2F;
         buffer.vertex(poseStack.last().pose(), GLYPH_RING_VERTICES[i * 3 * 4 + 0], GLYPH_RING_VERTICES[i * 3 * 4 + 1], GLYPH_RING_VERTICES[i * 3 * 4 + 2])
            .color(glyphColor.x(), glyphColor.y(), glyphColor.z(), 0.8F)
            .uv(u0, v0)
            .uv2(15728880)
            .endVertex();
         buffer.vertex(poseStack.last().pose(), GLYPH_RING_VERTICES[i * 3 * 4 + 3], GLYPH_RING_VERTICES[i * 3 * 4 + 4], GLYPH_RING_VERTICES[i * 3 * 4 + 5])
            .color(glyphColor.x(), glyphColor.y(), glyphColor.z(), 0.8F)
            .uv(u0, v1)
            .uv2(15728880)
            .endVertex();
         buffer.vertex(poseStack.last().pose(), GLYPH_RING_VERTICES[i * 3 * 4 + 6], GLYPH_RING_VERTICES[i * 3 * 4 + 7], GLYPH_RING_VERTICES[i * 3 * 4 + 8])
            .color(glyphColor.x(), glyphColor.y(), glyphColor.z(), 0.8F)
            .uv(u1, v1)
            .uv2(15728880)
            .endVertex();
         buffer.vertex(poseStack.last().pose(), GLYPH_RING_VERTICES[i * 3 * 4 + 9], GLYPH_RING_VERTICES[i * 3 * 4 + 10], GLYPH_RING_VERTICES[i * 3 * 4 + 11])
            .color(glyphColor.x(), glyphColor.y(), glyphColor.z(), 0.8F)
            .uv(u1, v0)
            .uv2(15728880)
            .endVertex();
      }

      poseStack.popPose();
   }

   private int[] generateGlyphIndices(BlockPos blockPos) {
      Random random = new Random(this.trashHash(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
      int[] result = new int[16];

      for (int i = 0; i < 16; i++) {
         result[i] = Mth.randomBetweenInclusive(random, 3, 60);
      }

      return result;
   }

   private long trashHash(int x, int y, int z) {
      long h = x * 374761393L + y * 668265263L + z * 7446411127L;
      h = (h ^ h >> 13) * 1274126177L;
      return h ^ h >> 16;
   }

   static {
      for (int i = 0; i < 16; i++) {
         double radians = (Math.PI / 8) * i;
         float xCenter = (float)Math.cos(radians) * 1.0F;
         float zCenter = (float)Math.sin(radians) * 1.0F;
         Vec2 center = new Vec2(xCenter, zCenter);
         Vec2 perpendicular = new Vec2(zCenter, -xCenter).normalized().scale(0.125F);
         Vec2 c0 = center.add(perpendicular);
         Vec2 c1 = center.add(perpendicular.negated());
         GLYPH_RING_VERTICES[i * 3 * 4 + 0] = c0.x;
         GLYPH_RING_VERTICES[i * 3 * 4 + 1] = 0.125F;
         GLYPH_RING_VERTICES[i * 3 * 4 + 2] = c0.y;
         GLYPH_RING_VERTICES[i * 3 * 4 + 3] = c0.x;
         GLYPH_RING_VERTICES[i * 3 * 4 + 4] = -0.125F;
         GLYPH_RING_VERTICES[i * 3 * 4 + 5] = c0.y;
         GLYPH_RING_VERTICES[i * 3 * 4 + 6] = c1.x;
         GLYPH_RING_VERTICES[i * 3 * 4 + 7] = -0.125F;
         GLYPH_RING_VERTICES[i * 3 * 4 + 8] = c1.y;
         GLYPH_RING_VERTICES[i * 3 * 4 + 9] = c1.x;
         GLYPH_RING_VERTICES[i * 3 * 4 + 10] = 0.125F;
         GLYPH_RING_VERTICES[i * 3 * 4 + 11] = c1.y;
      }
   }
}
