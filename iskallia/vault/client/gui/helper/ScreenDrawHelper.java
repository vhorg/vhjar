package iskallia.vault.client.gui.helper;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Matrix4f;
import iskallia.vault.init.ModShaders;
import java.awt.Color;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class ScreenDrawHelper {
   public static void drawTexturedQuads(Consumer<BufferBuilder> fn) {
      draw(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX, fn);
   }

   public static void draw(Mode drawMode, VertexFormat format, Consumer<BufferBuilder> fn) {
      draw(drawMode, format, bufferBuilder -> {
         fn.accept(bufferBuilder);
         return null;
      });
   }

   public static <R> R draw(Mode drawMode, VertexFormat format, Function<BufferBuilder, R> fn) {
      BufferBuilder buf = Tesselator.getInstance().getBuilder();
      buf.begin(drawMode, format);
      R result = fn.apply(buf);
      buf.end();
      BufferUploader.end(buf);
      return result;
   }

   public static ScreenDrawHelper.QuadBuilder rect(VertexConsumer buf, PoseStack renderStack) {
      return new ScreenDrawHelper.QuadBuilder(buf, renderStack);
   }

   public static ScreenDrawHelper.QuadBuilder rect(VertexConsumer buf, PoseStack renderStack, float width, float height) {
      return rect(buf, renderStack, 0.0F, 0.0F, 0.0F, width, height);
   }

   public static ScreenDrawHelper.QuadBuilder rect(
      VertexConsumer buf, PoseStack renderStack, float offsetX, float offsetY, float offsetZ, float width, float height
   ) {
      return new ScreenDrawHelper.QuadBuilder(buf, renderStack, offsetX, offsetY, offsetZ, width, height);
   }

   public static class QuadBuilder {
      private final VertexConsumer buf;
      private final PoseStack renderStack;
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

      private QuadBuilder(VertexConsumer buf, PoseStack renderStack) {
         this.buf = buf;
         this.renderStack = renderStack;
      }

      private QuadBuilder(VertexConsumer buf, PoseStack renderStack, float offsetX, float offsetY, float offsetZ, float width, float height) {
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
         return this.tex(tas.getU0(), tas.getV0(), tas.getU1() - tas.getU0(), tas.getV1() - tas.getV0());
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

      public ScreenDrawHelper.QuadBuilder drawColored() {
         RenderSystem.setShader(GameRenderer::getPositionColorShader);
         int r = this.color.getRed();
         int g = this.color.getGreen();
         int b = this.color.getBlue();
         int a = this.color.getAlpha();
         Matrix4f offset = this.renderStack.last().pose();
         this.buf.vertex(offset, this.offsetX, this.offsetY + this.height, this.offsetZ).color(r, g, b, a).endVertex();
         this.buf.vertex(offset, this.offsetX + this.width, this.offsetY + this.height, this.offsetZ).color(r, g, b, a).endVertex();
         this.buf.vertex(offset, this.offsetX + this.width, this.offsetY, this.offsetZ).color(r, g, b, a).endVertex();
         this.buf.vertex(offset, this.offsetX, this.offsetY, this.offsetZ).color(r, g, b, a).endVertex();
         return this;
      }

      public ScreenDrawHelper.QuadBuilder draw() {
         RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
         int r = this.color.getRed();
         int g = this.color.getGreen();
         int b = this.color.getBlue();
         int a = this.color.getAlpha();
         Matrix4f offset = this.renderStack.last().pose();
         this.buf.vertex(offset, this.offsetX, this.offsetY + this.height, this.offsetZ).color(r, g, b, a).uv(this.u, this.v + this.vWidth).endVertex();
         this.buf
            .vertex(offset, this.offsetX + this.width, this.offsetY + this.height, this.offsetZ)
            .color(r, g, b, a)
            .uv(this.u + this.uWidth, this.v + this.vWidth)
            .endVertex();
         this.buf.vertex(offset, this.offsetX + this.width, this.offsetY, this.offsetZ).color(r, g, b, a).uv(this.u + this.uWidth, this.v).endVertex();
         this.buf.vertex(offset, this.offsetX, this.offsetY, this.offsetZ).color(r, g, b, a).uv(this.u, this.v).endVertex();
         return this;
      }

      public ScreenDrawHelper.QuadBuilder drawGrayscale(float grayscale, float brightness) {
         ModShaders.getGrayscalePositionTexShader().withGrayscale(grayscale).withBrightness(brightness).enable();
         Matrix4f offset = this.renderStack.last().pose();
         this.buf.vertex(offset, this.offsetX, this.offsetY + this.height, this.offsetZ).uv(this.u, this.v + this.vWidth).endVertex();
         this.buf
            .vertex(offset, this.offsetX + this.width, this.offsetY + this.height, this.offsetZ)
            .uv(this.u + this.uWidth, this.v + this.vWidth)
            .endVertex();
         this.buf.vertex(offset, this.offsetX + this.width, this.offsetY, this.offsetZ).uv(this.u + this.uWidth, this.v).endVertex();
         this.buf.vertex(offset, this.offsetX, this.offsetY, this.offsetZ).uv(this.u, this.v).endVertex();
         return this;
      }
   }
}
