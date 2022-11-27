package iskallia.vault.client.atlas;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;

public class AtlasBufferPosColorTex {
   private final BufferBuilder builder;

   public AtlasBufferPosColorTex(BufferBuilder builder) {
      this.builder = builder;
   }

   public BufferBuilder getBuilder() {
      return this.builder;
   }

   public void begin(Mode mode, VertexFormat format) {
      this.builder.begin(mode, format);
   }

   public void end() {
      this.builder.end();
      BufferUploader.end(this.builder);
   }

   public void add(Matrix4f matrix4f, int x, int y, TextureAtlasSprite sprite) {
      this.add(matrix4f, x, y, 0, sprite.getWidth(), sprite.getHeight(), 1.0F, 1.0F, 1.0F, 1.0F, sprite);
   }

   public void add(Matrix4f matrix4f, int x, int y, int z, TextureAtlasSprite sprite) {
      this.add(matrix4f, x, y, z, sprite.getWidth(), sprite.getHeight(), 1.0F, 1.0F, 1.0F, 1.0F, sprite);
   }

   public void add(Matrix4f matrix4f, int x, int y, float r, float g, float b, float a, TextureAtlasSprite sprite) {
      this.add(matrix4f, x, y, 0, sprite.getWidth(), sprite.getHeight(), r, g, b, a, sprite);
   }

   public void add(Matrix4f matrix4f, int x, int y, int z, float r, float g, float b, float a, TextureAtlasSprite sprite) {
      this.add(matrix4f, x, y, z, sprite.getWidth(), sprite.getHeight(), r, g, b, a, sprite);
   }

   public void add(Matrix4f matrix4f, int x, int y, int z, int width, int height, float r, float g, float b, float a, TextureAtlasSprite sprite) {
      this.builder.vertex(matrix4f, x, y + height, z).color(r, g, b, a).uv(sprite.getU0(), sprite.getV1()).endVertex();
      this.builder.vertex(matrix4f, x + width, y + height, z).color(r, g, b, a).uv(sprite.getU1(), sprite.getV1()).endVertex();
      this.builder.vertex(matrix4f, x + width, y, z).color(r, g, b, a).uv(sprite.getU1(), sprite.getV0()).endVertex();
      this.builder.vertex(matrix4f, x, y, z).color(r, g, b, a).uv(sprite.getU0(), sprite.getV0()).endVertex();
   }

   public void addBounded(
      Matrix4f matrix4f,
      int x,
      int y,
      int z,
      int width,
      int height,
      float r,
      float g,
      float b,
      float a,
      TextureAtlasSprite sprite,
      AtlasBufferPosColorTex.Bounds bounds
   ) {
      if (!Mth.equal(bounds.minX, bounds.maxX) && !Mth.equal(bounds.minY, bounds.maxY)) {
         if (!(x > bounds.maxX) && !(x + width < bounds.minX) && !(y > bounds.maxY) && !(y + height < bounds.minY)) {
            float du = sprite.getU1() - sprite.getU0();
            float dv = sprite.getV1() - sprite.getV0();
            float x0 = Math.max((float)x, bounds.minX);
            float x1 = Math.min((float)(x + width), bounds.maxX);
            float y0 = Math.max((float)y, bounds.minY);
            float y1 = Math.min((float)(y + height), bounds.maxY);
            float u0 = (x0 - x) / width * du + sprite.getU0();
            float u1 = u0 + (x1 - x0) / width * du;
            float v0 = (y0 - y) / height * dv + sprite.getV0();
            float v1 = v0 + (y1 - y0) / height * dv;
            this.builder.vertex(matrix4f, x0, y1, z).color(r, g, b, a).uv(u0, v1).endVertex();
            this.builder.vertex(matrix4f, x1, y1, z).color(r, g, b, a).uv(u1, v1).endVertex();
            this.builder.vertex(matrix4f, x1, y0, z).color(r, g, b, a).uv(u1, v0).endVertex();
            this.builder.vertex(matrix4f, x0, y0, z).color(r, g, b, a).uv(u0, v0).endVertex();
         }
      }
   }

   public static final class Bounds {
      float minX;
      float minY;
      float maxX;
      float maxY;

      public Bounds() {
         this.set(0.0F, 0.0F, 0.0F, 0.0F);
      }

      public Bounds(float minX, float minY, float maxX, float maxY) {
         this.set(minX, minY, maxX, maxY);
      }

      public AtlasBufferPosColorTex.Bounds set(float minX, float minY, float maxX, float maxY) {
         this.minX = minX;
         this.minY = minY;
         this.maxX = maxX;
         this.maxY = maxY;
         return this;
      }
   }
}
