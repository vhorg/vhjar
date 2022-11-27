package iskallia.vault.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;

public final class LineRenderUtil {
   private static final LineRenderUtil INSTANCE = new LineRenderUtil();
   private final LineRenderUtil.LineVector startLinePoint = new LineRenderUtil.LineVector();
   private final LineRenderUtil.LineVector endLinePoint = new LineRenderUtil.LineVector();
   private final LineRenderUtil.LineVector lineVector = new LineRenderUtil.LineVector();
   private final LineRenderUtil.LineVector perpendicularVector = new LineRenderUtil.LineVector();
   private final LineRenderUtil.LineVector quadPoint0 = new LineRenderUtil.LineVector();
   private final LineRenderUtil.LineVector quadPoint1 = new LineRenderUtil.LineVector();
   private final LineRenderUtil.LineVector quadPoint2 = new LineRenderUtil.LineVector();
   private final LineRenderUtil.LineVector quadPoint3 = new LineRenderUtil.LineVector();

   public static LineRenderUtil getInstance() {
      return INSTANCE;
   }

   public void drawLine(VertexConsumer vertexConsumer, PoseStack poseStack, double x0, double y0, double x1, double y1, double width, int color) {
      Matrix4f offset = poseStack.last().pose();
      this.startLinePoint.set(x0, y0);
      this.endLinePoint.set(x1, y1);
      this.lineVector.set(this.endLinePoint).subtract(this.startLinePoint).normalize();
      this.perpendicularVector.set(-this.lineVector.y, this.lineVector.x);
      this.quadPoint0.set(this.perpendicularVector).scale(width).negate().add(this.startLinePoint);
      this.quadPoint1.set(this.perpendicularVector).scale(width).negate().add(this.endLinePoint);
      this.quadPoint2.set(this.perpendicularVector).scale(width).add(this.endLinePoint);
      this.quadPoint3.set(this.perpendicularVector).scale(width).add(this.startLinePoint);
      vertexConsumer.vertex(offset, (float)this.quadPoint3.x, (float)this.quadPoint3.y, 0.0F)
         .color(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, 255)
         .endVertex();
      vertexConsumer.vertex(offset, (float)this.quadPoint2.x, (float)this.quadPoint2.y, 0.0F)
         .color(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, 255)
         .endVertex();
      vertexConsumer.vertex(offset, (float)this.quadPoint1.x, (float)this.quadPoint1.y, 0.0F)
         .color(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, 255)
         .endVertex();
      vertexConsumer.vertex(offset, (float)this.quadPoint0.x, (float)this.quadPoint0.y, 0.0F)
         .color(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, 255)
         .endVertex();
   }

   private LineRenderUtil() {
   }

   private static class LineVector {
      private static final float EPSILON = 1.0E-4F;
      double x;
      double y;

      public LineRenderUtil.LineVector set(double x, double y) {
         this.x = x;
         this.y = y;
         return this;
      }

      public LineRenderUtil.LineVector set(LineRenderUtil.LineVector lineVector) {
         return this.set(lineVector.x, lineVector.y);
      }

      public LineRenderUtil.LineVector add(LineRenderUtil.LineVector lineVector) {
         return this.set(this.x + lineVector.x, this.y + lineVector.y);
      }

      public LineRenderUtil.LineVector subtract(LineRenderUtil.LineVector lineVector) {
         return this.set(this.x - lineVector.x, this.y - lineVector.y);
      }

      public LineRenderUtil.LineVector negate() {
         return this.set(-this.x, -this.y);
      }

      public LineRenderUtil.LineVector scale(double value) {
         return this.set(this.x * value, this.y * value);
      }

      public LineRenderUtil.LineVector normalize() {
         double f = Math.sqrt(this.x * this.x + this.y * this.y);
         return f < 1.0E-4F ? this.set(0.0, 0.0) : this.set(this.x / f, this.y / f);
      }
   }
}
