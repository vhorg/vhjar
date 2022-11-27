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

public final class NineSlice {
   public static NineSlice.TextureRegion region(Supplier<ITextureAtlas> atlasSupplier, ResourceLocation resourceLocation, NineSlice.Slices slices) {
      return NineSlice.TextureRegion.of(atlasSupplier, resourceLocation, slices);
   }

   public static NineSlice.TextureRegion region(
      Supplier<ITextureAtlas> atlasSupplier, ResourceLocation resourceLocation, NineSlice.Slices slices, NineSlice.DrawMode drawMode
   ) {
      return NineSlice.TextureRegion.of(atlasSupplier, resourceLocation, slices, drawMode);
   }

   public static NineSlice.Slices slice(int left, int right, int top, int bottom) {
      return NineSlice.Slices.of(left, right, top, bottom);
   }

   public static void buffer(
      IMultiBuffer buffer, PoseStack poseStack, int posX, int posY, int posZ, int width, int height, NineSlice.TextureRegion textureRegion
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
      NineSlice.Slices slices,
      TextureAtlasSprite sprite,
      NineSlice.DrawMode drawMode
   ) {
      buffer(buffer.getFor(sprite.atlas().location()), poseStack, posX, posY, posZ, width, height, slices, sprite, drawMode);
   }

   public static void buffer(
      VertexConsumer vertexConsumer, PoseStack poseStack, int posX, int posY, int posZ, int width, int height, NineSlice.TextureRegion textureRegion
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
         textureRegion.slices.top,
         textureRegion.slices.bottom,
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
      NineSlice.Slices slices,
      iskallia.vault.client.render.TextureRegion region,
      int atlasWidth,
      int atlasHeight,
      NineSlice.DrawMode drawMode
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
         slices.top,
         slices.bottom,
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
      NineSlice.Slices slices,
      TextureAtlasSprite sprite,
      NineSlice.DrawMode drawMode
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
         slices.top,
         slices.bottom,
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
      int sliceTopHeight,
      int sliceBottomHeight,
      int spriteWidth,
      int spriteHeight,
      float u0,
      float u1,
      float v0,
      float v1,
      NineSlice.DrawMode drawMode
   ) {
      float pU = (u1 - u0) / spriteWidth;
      float pV = (v1 - v0) / spriteHeight;
      float topV1 = v0 + sliceTopHeight * pV;
      float leftU1 = u0 + sliceLeftWidth * pU;
      float rightU0 = u1 - sliceRightWidth * pU;
      float bottomV0 = v1 - sliceBottomHeight * pV;
      float middleU0 = u0 + (sliceLeftWidth + 1) * pU;
      float middleU1 = u1 - (sliceRightWidth + 1) * pU;
      float middleV0 = v0 + (sliceTopHeight + 1) * pV;
      float middleV1 = v1 - (sliceBottomHeight + 1) * pV;
      float leftX = posX + sliceLeftWidth;
      float rightX = posX + width - sliceRightWidth;
      float topY = posY + sliceTopHeight;
      float bottomY = posY + height - sliceBottomHeight;
      float middleWidth = rightX - leftX;
      float middleHeight = bottomY - topY;
      bufferQuad(vertexConsumer, poseStack, posX, posY, posZ, sliceLeftWidth, sliceTopHeight, u0, leftU1, v0, topV1);
      bufferQuad(vertexConsumer, poseStack, rightX, posY, posZ, sliceRightWidth, sliceTopHeight, rightU0, u1, v0, topV1);
      bufferQuad(vertexConsumer, poseStack, posX, bottomY, posZ, sliceLeftWidth, sliceBottomHeight, u0, leftU1, bottomV0, v1);
      bufferQuad(vertexConsumer, poseStack, rightX, bottomY, posZ, sliceRightWidth, sliceBottomHeight, rightU0, u1, bottomV0, v1);
      bufferQuad(vertexConsumer, poseStack, leftX, posY, posZ, middleWidth, sliceTopHeight, middleU0, middleU1, v0, topV1);
      bufferQuad(vertexConsumer, poseStack, leftX, bottomY, posZ, middleWidth, sliceBottomHeight, middleU0, middleU1, bottomV0, v1);
      bufferQuad(vertexConsumer, poseStack, posX, topY, posZ, sliceLeftWidth, middleHeight, u0, leftU1, middleV0, middleV1);
      bufferQuad(vertexConsumer, poseStack, rightX, topY, posZ, sliceRightWidth, middleHeight, rightU0, u1, middleV0, middleV1);
      if (drawMode == NineSlice.DrawMode.Stretched) {
         bufferQuad(vertexConsumer, poseStack, leftX, topY, posZ, middleWidth, middleHeight, middleU0, middleU1, middleV0, middleV1);
      } else {
         if (drawMode != NineSlice.DrawMode.Tiled) {
            throw new UnsupportedOperationException("Unsupported nine-slice draw mode: " + drawMode.toString());
         }

         int spriteMiddleWidth = spriteWidth - sliceLeftWidth - sliceRightWidth;
         int spriteMiddleHeight = spriteHeight - sliceTopHeight - sliceBottomHeight;
         int cols = Mth.ceil(middleWidth / spriteMiddleWidth);
         int rows = Mth.ceil(middleHeight / spriteMiddleHeight);

         for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
               float mX = leftX + x * spriteMiddleWidth;
               float mY = topY + y * spriteMiddleHeight;
               bufferQuadBounded(
                  vertexConsumer,
                  poseStack,
                  mX,
                  mY,
                  posZ,
                  spriteMiddleWidth,
                  spriteMiddleHeight,
                  middleU0,
                  middleU1,
                  middleV0,
                  middleV1,
                  leftX,
                  leftX + middleWidth,
                  topY,
                  topY + middleHeight
               );
            }
         }
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

   private NineSlice() {
   }

   public static enum DrawMode {
      Stretched,
      Tiled;
   }

   public record Slices(int left, int right, int top, int bottom) {
      public static NineSlice.Slices of(int left, int right, int top, int bottom) {
         return new NineSlice.Slices(left, right, top, bottom);
      }
   }

   public record TextureRegion(Supplier<ITextureAtlas> atlas, ResourceLocation resourceLocation, NineSlice.Slices slices, NineSlice.DrawMode drawMode) {
      public static NineSlice.TextureRegion of(Supplier<ITextureAtlas> atlasSupplier, ResourceLocation resourceLocation, NineSlice.Slices slices) {
         return of(atlasSupplier, resourceLocation, slices, NineSlice.DrawMode.Stretched);
      }

      public static NineSlice.TextureRegion of(
         Supplier<ITextureAtlas> atlasSupplier, ResourceLocation resourceLocation, NineSlice.Slices slices, NineSlice.DrawMode drawMode
      ) {
         return new NineSlice.TextureRegion(atlasSupplier, resourceLocation, slices, drawMode);
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
         NineSlice.buffer(bufferBuilder, poseStack, posX, posY, posZ, width, height, this);
         bufferBuilder.end();
         BufferUploader.end(bufferBuilder);
      }

      public void buffer(VertexConsumer vertexConsumer, PoseStack poseStack, int posX, int posY, int posZ, int width, int height) {
         NineSlice.buffer(vertexConsumer, poseStack, posX, posY, posZ, width, height, this);
      }

      public void buffer(VertexConsumer vertexConsumer, PoseStack poseStack, ISpatial spatial) {
         NineSlice.buffer(vertexConsumer, poseStack, spatial.x(), spatial.y(), spatial.z(), spatial.width(), spatial.height(), this);
      }

      public void buffer(IMultiBuffer buffer, PoseStack poseStack, int posX, int posY, int posZ, int width, int height) {
         NineSlice.buffer(buffer.getFor(this.atlas().get().getAtlasResourceLocation()), poseStack, posX, posY, posZ, width, height, this);
      }

      public void buffer(IMultiBuffer buffer, PoseStack poseStack, ISpatial spatial) {
         NineSlice.buffer(
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
