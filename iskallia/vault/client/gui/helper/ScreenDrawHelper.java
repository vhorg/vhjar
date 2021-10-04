package iskallia.vault.client.gui.helper;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.awt.Color;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.math.vector.Matrix4f;

public class ScreenDrawHelper {
   public static void drawQuad(Consumer<BufferBuilder> fn) {
      draw(7, DefaultVertexFormats.field_227851_o_, fn);
   }

   public static void draw(int drawMode, VertexFormat format, Consumer<BufferBuilder> fn) {
      draw(drawMode, format, bufferBuilder -> {
         fn.accept(bufferBuilder);
         return null;
      });
   }

   public static <R> R draw(int drawMode, VertexFormat format, Function<BufferBuilder, R> fn) {
      BufferBuilder buf = Tessellator.func_178181_a().func_178180_c();
      buf.func_181668_a(drawMode, format);
      R result = fn.apply(buf);
      buf.func_178977_d();
      WorldVertexBufferUploader.func_181679_a(buf);
      return result;
   }

   public static ScreenDrawHelper.QuadBuilder rect(IVertexBuilder buf, MatrixStack renderStack) {
      return new ScreenDrawHelper.QuadBuilder(buf, renderStack);
   }

   public static ScreenDrawHelper.QuadBuilder rect(IVertexBuilder buf, MatrixStack renderStack, float width, float height) {
      return rect(buf, renderStack, 0.0F, 0.0F, 0.0F, width, height);
   }

   public static ScreenDrawHelper.QuadBuilder rect(
      IVertexBuilder buf, MatrixStack renderStack, float offsetX, float offsetY, float offsetZ, float width, float height
   ) {
      return new ScreenDrawHelper.QuadBuilder(buf, renderStack, offsetX, offsetY, offsetZ, width, height);
   }

   public static class QuadBuilder {
      private final IVertexBuilder buf;
      private final MatrixStack renderStack;
      private float offsetX;
      private float offsetY;
      private float offsetZ;
      private float width;
      private float height;
      private float u = 0.0F;
      private float v = 0.0F;
      private float uWidth = 1.0F;
      private float vWidth = 1.0F;
      private Color color = Color.WHITE;

      private QuadBuilder(IVertexBuilder buf, MatrixStack renderStack) {
         this.buf = buf;
         this.renderStack = renderStack;
      }

      private QuadBuilder(IVertexBuilder buf, MatrixStack renderStack, float offsetX, float offsetY, float offsetZ, float width, float height) {
         this.buf = buf;
         this.renderStack = renderStack;
         this.offsetX = offsetX;
         this.offsetY = offsetY;
         this.offsetZ = offsetZ;
         this.width = width;
         this.height = height;
      }

      public ScreenDrawHelper.QuadBuilder at(float offsetX, float offsetY) {
         this.offsetX = offsetX;
         this.offsetY = offsetY;
         return this;
      }

      public ScreenDrawHelper.QuadBuilder zLevel(float offsetZ) {
         this.offsetZ = offsetZ;
         return this;
      }

      public ScreenDrawHelper.QuadBuilder dim(float width, float height) {
         this.width = width;
         this.height = height;
         return this;
      }

      public ScreenDrawHelper.QuadBuilder tex(TextureAtlasSprite tas) {
         return this.tex(tas.func_94209_e(), tas.func_94206_g(), tas.func_94212_f() - tas.func_94209_e(), tas.func_94210_h() - tas.func_94206_g());
      }

      public ScreenDrawHelper.QuadBuilder texVanilla(float pxU, float pxV, float pxWidth, float pxHeight) {
         return this.texTexturePart(pxU, pxV, pxWidth, pxHeight, 256.0F, 256.0F);
      }

      public ScreenDrawHelper.QuadBuilder texTexturePart(float pxU, float pxV, float pxWidth, float pxHeight, float texPxWidth, float texPxHeight) {
         return this.tex(pxU / texPxWidth, pxV / texPxHeight, pxWidth / texPxWidth, pxHeight / texPxHeight);
      }

      public ScreenDrawHelper.QuadBuilder tex(float u, float v, float uWidth, float vWidth) {
         this.u = u;
         this.v = v;
         this.uWidth = uWidth;
         this.vWidth = vWidth;
         return this;
      }

      public ScreenDrawHelper.QuadBuilder color(Color color) {
         this.color = color;
         return this;
      }

      public ScreenDrawHelper.QuadBuilder color(int color) {
         return this.color(new Color(color, true));
      }

      public ScreenDrawHelper.QuadBuilder color(int r, int g, int b, int a) {
         return this.color(new Color(r, g, b, a));
      }

      public ScreenDrawHelper.QuadBuilder color(float r, float g, float b, float a) {
         return this.color(new Color(r, g, b, a));
      }

      public ScreenDrawHelper.QuadBuilder draw() {
         int r = this.color.getRed();
         int g = this.color.getGreen();
         int b = this.color.getBlue();
         int a = this.color.getAlpha();
         Matrix4f offset = this.renderStack.func_227866_c_().func_227870_a_();
         this.buf
            .func_227888_a_(offset, this.offsetX, this.offsetY + this.height, this.offsetZ)
            .func_225586_a_(r, g, b, a)
            .func_225583_a_(this.u, this.v + this.vWidth)
            .func_181675_d();
         this.buf
            .func_227888_a_(offset, this.offsetX + this.width, this.offsetY + this.height, this.offsetZ)
            .func_225586_a_(r, g, b, a)
            .func_225583_a_(this.u + this.uWidth, this.v + this.vWidth)
            .func_181675_d();
         this.buf
            .func_227888_a_(offset, this.offsetX + this.width, this.offsetY, this.offsetZ)
            .func_225586_a_(r, g, b, a)
            .func_225583_a_(this.u + this.uWidth, this.v)
            .func_181675_d();
         this.buf.func_227888_a_(offset, this.offsetX, this.offsetY, this.offsetZ).func_225586_a_(r, g, b, a).func_225583_a_(this.u, this.v).func_181675_d();
         return this;
      }
   }
}
