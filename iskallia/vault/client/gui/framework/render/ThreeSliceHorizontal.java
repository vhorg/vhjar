package iskallia.vault.client.gui.framework.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Matrix4f;
import iskallia.vault.client.atlas.IMultiBuffer;
import iskallia.vault.client.atlas.ITextureAtlas;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import java.util.function.Supplier;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class ThreeSliceHorizontal {
   public static ThreeSliceHorizontal.TextureRegion region(
      Supplier<ITextureAtlas> atlasSupplier, ResourceLocation resourceLocation, ThreeSliceHorizontal.Slices slices
   ) {
      return ThreeSliceHorizontal.TextureRegion.of(atlasSupplier, resourceLocation, slices);
   }

   public static ThreeSliceHorizontal.TextureRegion region(
      Supplier<ITextureAtlas> atlasSupplier, ResourceLocation resourceLocation, ThreeSliceHorizontal.Slices slices, ThreeSliceHorizontal.DrawMode drawMode
   ) {
      return ThreeSliceHorizontal.TextureRegion.of(atlasSupplier, resourceLocation, slices, drawMode);
   }

   public static ThreeSliceHorizontal.Slices slice(int left, int right) {
      return ThreeSliceHorizontal.Slices.of(left, right);
   }

   public static void buffer(
      IMultiBuffer buffer, PoseStack poseStack, int posX, int posY, int posZ, int width, int height, ThreeSliceHorizontal.TextureRegion textureRegion
   ) {
      buffer(buffer.getFor(textureRegion.getSprite().atlas().location()), poseStack, posX, posY, posZ, width, height, textureRegion);
   }

   public static void buffer(
      IMultiBuffer buffer,
      PoseStack poseStack,
      int posX,
      int posY,
      int posZ,
      int width,
      int height,
      ThreeSliceHorizontal.Slices slices,
      TextureAtlasSprite sprite,
      ThreeSliceHorizontal.DrawMode drawMode
   ) {
      buffer(buffer.getFor(sprite.atlas().location()), poseStack, posX, posY, posZ, width, height, slices, sprite, drawMode);
   }

   public static void buffer(
      VertexConsumer vertexConsumer, PoseStack poseStack, int posX, int posY, int posZ, int width, int height, ThreeSliceHorizontal.TextureRegion textureRegion
   ) {
      TextureAtlasSprite sprite = textureRegion.getSprite();
      buffer(
         vertexConsumer,
         poseStack,
         posX,
         posY,
         posZ,
         width,
         height,
         textureRegion.slices.left,
         textureRegion.slices.right,
         sprite.getWidth(),
         sprite.getHeight(),
         sprite.getU0(),
         sprite.getU1(),
         sprite.getV0(),
         sprite.getV1(),
         textureRegion.drawMode()
      );
   }

   public static void buffer(
      VertexConsumer vertexConsumer,
      PoseStack poseStack,
      int posX,
      int posY,
      int posZ,
      int width,
      int height,
      ThreeSliceHorizontal.Slices slices,
      iskallia.vault.client.render.TextureRegion region,
      int atlasWidth,
      int atlasHeight,
      ThreeSliceHorizontal.DrawMode drawMode
   ) {
      buffer(
         vertexConsumer,
         poseStack,
         posX,
         posY,
         posZ,
         width,
         height,
         slices.left,
         slices.right,
         region.width(),
         region.height(),
         (float)region.x() / atlasWidth,
         (float)(region.x() + region.width()) / atlasWidth,
         (float)region.y() / atlasWidth,
         (float)(region.y() + region.height()) / atlasHeight,
         drawMode
      );
   }

   public static void buffer(
      VertexConsumer vertexConsumer,
      PoseStack poseStack,
      int posX,
      int posY,
      int posZ,
      int width,
      int height,
      ThreeSliceHorizontal.Slices slices,
      TextureAtlasSprite sprite,
      ThreeSliceHorizontal.DrawMode drawMode
   ) {
      buffer(
         vertexConsumer,
         poseStack,
         posX,
         posY,
         posZ,
         width,
         height,
         slices.left,
         slices.right,
         sprite.getWidth(),
         sprite.getHeight(),
         sprite.getU0(),
         sprite.getU1(),
         sprite.getV0(),
         sprite.getV1(),
         drawMode
      );
   }

   public static void buffer(
      VertexConsumer vertexConsumer,
      PoseStack poseStack,
      int posX,
      int posY,
      int posZ,
      int width,
      int height,
      int sliceLeftWidth,
      int sliceRightWidth,
      int spriteWidth,
      int spriteHeight,
      float u0,
      float u1,
      float v0,
      float v1,
      ThreeSliceHorizontal.DrawMode drawMode
   ) {
      float pU = (u1 - u0) / spriteWidth;
      float pV = (v1 - v0) / spriteHeight;
      float leftU1 = u0 + sliceLeftWidth * pU;
      float rightU0 = u1 - sliceRightWidth * pU;
      float middleU0 = u0 + (sliceLeftWidth + 1) * pU;
      float middleU1 = u1 - (sliceRightWidth + 1) * pU;
      float leftX = posX + sliceLeftWidth;
      float rightX = posX + width - sliceRightWidth;
      float middleWidth = rightX - leftX;
      int spriteMiddleWidth = spriteWidth - sliceLeftWidth - sliceRightWidth - 2;
      bufferQuad(vertexConsumer, poseStack, posX, posY, posZ, sliceLeftWidth, height, u0, leftU1, v0, v1);
      bufferQuad(vertexConsumer, poseStack, rightX, posY, posZ, sliceRightWidth, height, rightU0, u1, v0, v1);
      if (drawMode == ThreeSliceHorizontal.DrawMode.Stretched) {
         bufferQuad(vertexConsumer, poseStack, leftX, posY, posZ, middleWidth, height, middleU0, middleU1, v0, v1);
      } else if (drawMode != ThreeSliceHorizontal.DrawMode.Tiled) {
         throw new UnsupportedOperationException("Unsupported three-horizontal-slice frame draw mode: " + drawMode.toString());
      }
   }

   private static void bufferQuad(
      VertexConsumer vertexConsumer, PoseStack poseStack, float x, float y, float z, float width, float height, float u0, float u1, float v0, float v1
   ) {
      Matrix4f matrix = poseStack.last().pose();
      vertexConsumer.vertex(matrix, x, y + height, z).uv(u0, v1).endVertex();
      vertexConsumer.vertex(matrix, x + width, y + height, z).uv(u1, v1).endVertex();
      vertexConsumer.vertex(matrix, x + width, y, z).uv(u1, v0).endVertex();
      vertexConsumer.vertex(matrix, x, y, z).uv(u0, v0).endVertex();
   }

   private static void bufferQuadBounded(
      VertexConsumer vertexConsumer,
      PoseStack poseStack,
      float x,
      float y,
      float z,
      float width,
      float height,
      float u0,
      float u1,
      float v0,
      float v1,
      float xMin,
      float xMax,
      float yMin,
      float yMax
   ) {
      if (!Mth.equal(xMin, xMax) && !Mth.equal(yMin, yMax)) {
         if (!(x > xMax) && !(x + width < xMin) && !(y > yMax) && !(y + height < yMin)) {
            float du = u1 - u0;
            float dv = v1 - v0;
            float x0 = Math.max(x, xMin);
            float x1 = Math.min(x + width, xMax);
            float y0 = Math.max(y, yMin);
            float y1 = Math.min(y + height, yMax);
            float cu0 = (x0 - x) / width * du + u0;
            float cu1 = cu0 + (x1 - x0) / width * du;
            float cv0 = (y0 - y) / height * dv + v0;
            float cv1 = cv0 + (y1 - y0) / height * dv;
            Matrix4f matrix = poseStack.last().pose();
            vertexConsumer.vertex(matrix, x0, y1, z).uv(cu0, cv1).endVertex();
            vertexConsumer.vertex(matrix, x1, y1, z).uv(cu1, cv1).endVertex();
            vertexConsumer.vertex(matrix, x1, y0, z).uv(cu1, cv0).endVertex();
            vertexConsumer.vertex(matrix, x0, y0, z).uv(cu0, cv0).endVertex();
         }
      }
   }

   private ThreeSliceHorizontal() {
   }

   public static enum DrawMode {
      Stretched,
      Tiled;
   }

   public record Slices(int left, int right) {
      public static ThreeSliceHorizontal.Slices of(int left, int right) {
         return new ThreeSliceHorizontal.Slices(left, right);
      }
   }

   public record TextureRegion(
      Supplier<ITextureAtlas> atlas, ResourceLocation resourceLocation, ThreeSliceHorizontal.Slices slices, ThreeSliceHorizontal.DrawMode drawMode
   ) {
      public static ThreeSliceHorizontal.TextureRegion of(
         Supplier<ITextureAtlas> atlasSupplier, ResourceLocation resourceLocation, ThreeSliceHorizontal.Slices slices
      ) {
         return of(atlasSupplier, resourceLocation, slices, ThreeSliceHorizontal.DrawMode.Stretched);
      }

      public static ThreeSliceHorizontal.TextureRegion of(
         Supplier<ITextureAtlas> atlasSupplier, ResourceLocation resourceLocation, ThreeSliceHorizontal.Slices slices, ThreeSliceHorizontal.DrawMode drawMode
      ) {
         return new ThreeSliceHorizontal.TextureRegion(atlasSupplier, resourceLocation, slices, drawMode);
      }

      public TextureAtlasSprite getSprite() {
         return this.atlas.get().getSprite(this.resourceLocation);
      }

      public void blit(PoseStack poseStack, int posX, int posY, int posZ, int width, int height) {
         ITextureAtlas atlas = this.atlas.get();
         RenderSystem.setShaderTexture(0, atlas.getAtlasResourceLocation());
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
         bufferBuilder.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
         ThreeSliceHorizontal.buffer(bufferBuilder, poseStack, posX, posY, posZ, width, height, this);
         bufferBuilder.end();
         BufferUploader.end(bufferBuilder);
      }

      public void buffer(VertexConsumer vertexConsumer, PoseStack poseStack, int posX, int posY, int posZ, int width, int height) {
         ThreeSliceHorizontal.buffer(vertexConsumer, poseStack, posX, posY, posZ, width, height, this);
      }

      public void buffer(VertexConsumer vertexConsumer, PoseStack poseStack, ISpatial spatial) {
         ThreeSliceHorizontal.buffer(vertexConsumer, poseStack, spatial.x(), spatial.y(), spatial.z(), spatial.width(), spatial.height(), this);
      }

      public void buffer(IMultiBuffer buffer, PoseStack poseStack, int posX, int posY, int posZ, int width, int height) {
         ThreeSliceHorizontal.buffer(buffer.getFor(this.atlas().get().getAtlasResourceLocation()), poseStack, posX, posY, posZ, width, height, this);
      }

      public void buffer(IMultiBuffer buffer, PoseStack poseStack, ISpatial spatial) {
         ThreeSliceHorizontal.buffer(
            buffer.getFor(this.atlas().get().getAtlasResourceLocation()),
            poseStack,
            spatial.x(),
            spatial.y(),
            spatial.z(),
            spatial.width(),
            spatial.height(),
            this
         );
      }
   }
}
