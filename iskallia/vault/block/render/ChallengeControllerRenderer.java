package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector4f;
import iskallia.vault.VaultMod;
import iskallia.vault.block.entity.challenge.ChallengeControllerBlockEntity;
import iskallia.vault.client.util.ClientScheduler;
import iskallia.vault.client.util.color.ColorUtil;
import iskallia.vault.core.random.ChunkRandom;
import iskallia.vault.init.ModRenderTypes;
import java.awt.Color;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

public class ChallengeControllerRenderer implements BlockEntityRenderer<ChallengeControllerBlockEntity> {
   private final Font font;
   private static final int GLYPH_COUNT = 16;
   private static final int GLYPH_ALPHA = 204;
   private static final float GLYPH_RING_RADIUS = 0.45F;
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

   public ChallengeControllerRenderer(Context context) {
      this.font = context.getFont();
   }

   public void render(
      ChallengeControllerBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay
   ) {
      double time = (float)ClientScheduler.INSTANCE.getTick() + partialTick;
      if (entity.getState() == ChallengeControllerBlockEntity.State.IDLE) {
         double offsetTime = entity.animationTick;

         for (int i = 0; i < 3; i++) {
            double var17 = offsetTime + 60.0;
            offsetTime = var17 * 1.2;
            RenderType renderType = RenderType.beaconBeam(VaultMod.id("textures/particle/challenge_cube.png"), true);
            VertexConsumer buffer = bufferSource.getBuffer(renderType);
            poseStack.pushPose();
            poseStack.translate(0.5, 1.65, 0.5);
            poseStack.scale(7.0F, 7.0F, 7.0F);
            poseStack.mulPose(Quaternion.fromXYZ(0.0F, (float)(offsetTime / 10.0), 0.0F));
            poseStack.mulPose(Quaternion.fromXYZ((float)Math.sin(offsetTime / 20.0), (float)Math.sin(offsetTime / 40.0), (float)Math.sin(offsetTime / 20.0)));
            this.renderCube(buffer, poseStack, -1269804976, combinedOverlay);
            poseStack.popPose();
         }
      } else if (entity.getState() == ChallengeControllerBlockEntity.State.GENERATING) {
         double offsetTime = entity.animationTick + partialTick;

         for (int i = 0; i < 3; i++) {
            double var19 = offsetTime + 60.0;
            offsetTime = var19 * 1.2;
            RenderType renderType = RenderType.beaconBeam(VaultMod.id("textures/particle/challenge_cube.png"), true);
            VertexConsumer buffer = bufferSource.getBuffer(renderType);
            double progress = Mth.clamp((entity.animationTick + partialTick) / 50.0F, 0.0, 1.0);
            Color color = new Color(ColorUtil.blendColors(entity.getRenderer().getCoreColor(), 5263440, (float)progress));
            poseStack.pushPose();
            poseStack.translate(0.5, 1.65 + 0.5 * progress, 0.5);
            poseStack.scale(7.0F, 7.0F, 7.0F);
            poseStack.mulPose(Quaternion.fromXYZ(0.0F, (float)(offsetTime / 10.0), 0.0F));
            poseStack.mulPose(Quaternion.fromXYZ((float)Math.sin(offsetTime / 20.0), (float)Math.sin(offsetTime / 40.0), (float)Math.sin(offsetTime / 20.0)));
            this.renderCube(buffer, poseStack, -1275068416 | color.getRGB(), combinedOverlay);
            poseStack.popPose();
         }
      } else if (entity.getState() == ChallengeControllerBlockEntity.State.ACTIVE) {
         double offsetTime = entity.animationTick + partialTick;

         for (int i = 0; i < 3; i++) {
            double var21 = offsetTime + 60.0;
            offsetTime = var21 * 1.2;
            RenderType renderType = RenderType.beaconBeam(VaultMod.id("textures/particle/challenge_cube.png"), true);
            VertexConsumer buffer = bufferSource.getBuffer(renderType);
            Color color = new Color(entity.getRenderer().getCoreColor());
            poseStack.pushPose();
            poseStack.translate(0.5, 2.15, 0.5);
            poseStack.scale(7.0F, 7.0F, 7.0F);
            poseStack.mulPose(Quaternion.fromXYZ(0.0F, (float)(offsetTime / 10.0), 0.0F));
            poseStack.mulPose(Quaternion.fromXYZ((float)Math.sin(offsetTime / 20.0), (float)Math.sin(offsetTime / 40.0), (float)Math.sin(offsetTime / 20.0)));
            this.renderCube(buffer, poseStack, -1275068416 | color.getRGB(), combinedOverlay);
            poseStack.popPose();
         }
      }

      if (entity.getState() == ChallengeControllerBlockEntity.State.ACTIVE || entity.getState() == ChallengeControllerBlockEntity.State.GENERATING) {
         poseStack.pushPose();
         double progress = entity.animationTick == 0 ? 0.0 : Mth.clamp((entity.animationTick + partialTick) / 50.0F, 0.0, 1.0);
         Color color = new Color(ColorUtil.blendColors(entity.getRenderer().getGlyphColor(), 5263440, (float)progress));
         poseStack.translate(0.5, 1.65 + 0.5 * progress, 0.5);
         this.renderGlyphs(entity, poseStack, bufferSource, entity.animationTick == 0 ? 0.0 : entity.animationTick + partialTick, color.getRGB());
         poseStack.popPose();
      }
   }

   private void renderCube(VertexConsumer buffer, PoseStack poseStack, int color, int overlay) {
      float minU = 0.0F;
      float minV = 0.0F;
      float maxU = 1.0F;
      float maxV = 1.0F;
      this.putVertex(poseStack, buffer, -0.5F, -0.5F, -0.5F, color, minU, minV, overlay);
      this.putVertex(poseStack, buffer, 0.5F, -0.5F, -0.5F, color, maxU, minV, overlay);
      this.putVertex(poseStack, buffer, 0.5F, -0.5F, 0.5F, color, maxU, maxV, overlay);
      this.putVertex(poseStack, buffer, -0.5F, -0.5F, 0.5F, color, minU, maxV, overlay);
      this.putVertex(poseStack, buffer, -0.5F, 0.5F, 0.5F, color, minU, minV, overlay);
      this.putVertex(poseStack, buffer, 0.5F, 0.5F, 0.5F, color, maxU, minV, overlay);
      this.putVertex(poseStack, buffer, 0.5F, 0.5F, -0.5F, color, maxU, maxV, overlay);
      this.putVertex(poseStack, buffer, -0.5F, 0.5F, -0.5F, color, minU, maxV, overlay);
      this.putVertex(poseStack, buffer, -0.5F, -0.5F, 0.5F, color, maxU, minV, overlay);
      this.putVertex(poseStack, buffer, -0.5F, 0.5F, 0.5F, color, maxU, maxV, overlay);
      this.putVertex(poseStack, buffer, -0.5F, 0.5F, -0.5F, color, minU, maxV, overlay);
      this.putVertex(poseStack, buffer, -0.5F, -0.5F, -0.5F, color, minU, minV, overlay);
      this.putVertex(poseStack, buffer, 0.5F, -0.5F, -0.5F, color, maxU, minV, overlay);
      this.putVertex(poseStack, buffer, 0.5F, 0.5F, -0.5F, color, maxU, maxV, overlay);
      this.putVertex(poseStack, buffer, 0.5F, 0.5F, 0.5F, color, minU, maxV, overlay);
      this.putVertex(poseStack, buffer, 0.5F, -0.5F, 0.5F, color, minU, minV, overlay);
      this.putVertex(poseStack, buffer, 0.5F, -0.5F, -0.5F, color, minU, minV, overlay);
      this.putVertex(poseStack, buffer, -0.5F, -0.5F, -0.5F, color, maxU, minV, overlay);
      this.putVertex(poseStack, buffer, -0.5F, 0.5F, -0.5F, color, maxU, maxV, overlay);
      this.putVertex(poseStack, buffer, 0.5F, 0.5F, -0.5F, color, minU, maxV, overlay);
      this.putVertex(poseStack, buffer, -0.5F, -0.5F, 0.5F, color, minU, minV, overlay);
      this.putVertex(poseStack, buffer, 0.5F, -0.5F, 0.5F, color, maxU, minV, overlay);
      this.putVertex(poseStack, buffer, 0.5F, 0.5F, 0.5F, color, maxU, maxV, overlay);
      this.putVertex(poseStack, buffer, -0.5F, 0.5F, 0.5F, color, minU, maxV, overlay);
   }

   public void putVertex(PoseStack matrices, VertexConsumer buffer, float x, float y, float z, int color, float u, float v, int overlay) {
      Matrix4f view = matrices.last().pose();
      Vector4f pos = new Vector4f(x / 16.0F, y / 16.0F, z / 16.0F, 1.0F);
      pos.transform(view);
      float alpha = (color >>> 24) / 255.0F;
      float red = (color >>> 16 & 0xFF) / 255.0F;
      float green = (color >>> 8 & 0xFF) / 255.0F;
      float blue = (color & 0xFF) / 255.0F;
      buffer.vertex(pos.x(), pos.y(), pos.z(), red, green, blue, alpha, u, v, overlay, 0, 0.0F, 0.0F, 0.0F);
   }

   private void renderGlyphs(ChallengeControllerBlockEntity entity, PoseStack poseStack, MultiBufferSource bufferSource, double time, int color) {
      VertexConsumer buffer = bufferSource.getBuffer(ModRenderTypes.TOTEM_GLYPH_EFFECT);
      poseStack.pushPose();
      poseStack.mulPose(Quaternion.fromXYZ(0.0F, (float)(time / 10.0), 0.0F));
      poseStack.mulPose(Quaternion.fromXYZ((float)Math.sin(time / 20.0), (float)Math.sin(time / 40.0), (float)Math.sin(time / 20.0)));
      int[] glyphIndices = this.getGlyphIndices(entity.getBlockPos());
      int alpha = color >>> 24;
      int red = color >>> 16 & 0xFF;
      int green = color >>> 8 & 0xFF;
      int blue = color & 0xFF;

      for (int i = 0; i < 16; i++) {
         float u0 = glyphIndices[i] % 16 * 0.0625F;
         float v0 = glyphIndices[i] / 16 * 0.2F;
         float u1 = (glyphIndices[i] % 16 + 1) * 0.0625F;
         float v1 = (glyphIndices[i] / 16 + 1) * 0.2F;
         buffer.vertex(poseStack.last().pose(), GLYPH_RING_VERTICES[i * 3 * 4 + 0], GLYPH_RING_VERTICES[i * 3 * 4 + 1], GLYPH_RING_VERTICES[i * 3 * 4 + 2])
            .color(red, green, blue, alpha)
            .uv(u0, v0)
            .uv2(15728880)
            .endVertex();
         buffer.vertex(poseStack.last().pose(), GLYPH_RING_VERTICES[i * 3 * 4 + 3], GLYPH_RING_VERTICES[i * 3 * 4 + 4], GLYPH_RING_VERTICES[i * 3 * 4 + 5])
            .color(red, green, blue, alpha)
            .uv(u0, v1)
            .uv2(15728880)
            .endVertex();
         buffer.vertex(poseStack.last().pose(), GLYPH_RING_VERTICES[i * 3 * 4 + 6], GLYPH_RING_VERTICES[i * 3 * 4 + 7], GLYPH_RING_VERTICES[i * 3 * 4 + 8])
            .color(red, green, blue, alpha)
            .uv(u1, v1)
            .uv2(15728880)
            .endVertex();
         buffer.vertex(poseStack.last().pose(), GLYPH_RING_VERTICES[i * 3 * 4 + 9], GLYPH_RING_VERTICES[i * 3 * 4 + 10], GLYPH_RING_VERTICES[i * 3 * 4 + 11])
            .color(red, green, blue, alpha)
            .uv(u1, v0)
            .uv2(15728880)
            .endVertex();
      }

      poseStack.popPose();
   }

   private int[] getGlyphIndices(BlockPos pos) {
      ChunkRandom random = ChunkRandom.any();
      random.setBlockSeed(0L, pos, 374761393L);
      int[] result = new int[16];

      for (int i = 0; i < 16; i++) {
         result[i] = random.nextInt(3, 61);
      }

      return result;
   }

   static {
      for (int i = 0; i < 16; i++) {
         double radians = (Math.PI / 8) * i;
         float xCenter = (float)Math.cos(radians) * 0.45F;
         float zCenter = (float)Math.sin(radians) * 0.45F;
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
