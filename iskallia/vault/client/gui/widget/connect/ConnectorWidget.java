package iskallia.vault.client.gui.widget.connect;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import iskallia.vault.client.gui.helper.ScreenDrawHelper;
import iskallia.vault.util.VectorHelper;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D.Double;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.opengl.GL11;

public class ConnectorWidget extends Widget {
   private final ConnectorWidget.ConnectorType type;
   private final ConnectorWidget.Connector connector;
   private final ConnectableWidget source;
   private final ConnectableWidget target;
   private Color color = new Color(11184810);

   public ConnectorWidget(ConnectableWidget source, ConnectableWidget target, ConnectorWidget.ConnectorType type) {
      this(buildWidgetBox(source, target), source, target, type);
   }

   private ConnectorWidget(ConnectorWidget.Connector connector, ConnectableWidget source, ConnectableWidget target, ConnectorWidget.ConnectorType type) {
      super(connector.rct.x, connector.rct.y, connector.rct.width, connector.rct.height, new StringTextComponent("Connector"));
      this.type = type;
      this.connector = connector;
      this.source = source;
      this.target = target;
   }

   public ConnectorWidget setColor(Color color) {
      this.color = color;
      return this;
   }

   private static ConnectorWidget.Connector buildWidgetBox(ConnectableWidget source, ConnectableWidget target) {
      Double from = source.getRenderPosition();
      Double to = target.getRenderPosition();
      Vector2f dir = new Vector2f((float)(to.x - from.x), (float)(to.y - from.y));
      float angle = (float)Math.atan2(dir.field_189982_i, dir.field_189983_j);
      double angleDeg = Math.toDegrees(angle) - 90.0;
      from = source.getPointOnEdge(angleDeg);
      from.x = from.x + source.getRenderWidth() / 2.0;
      from.y = from.y + source.getRenderHeight() / 2.0;
      Vector2f fromV = new Vector2f((float)from.x, (float)from.y);
      to = target.getPointOnEdge(angleDeg - 180.0);
      to.x = to.x + source.getRenderWidth() / 2.0;
      to.y = to.y + source.getRenderHeight() / 2.0;
      Vector2f toV = new Vector2f((float)to.x, (float)to.y);
      Double min = new Double(Math.min(from.x, to.x), Math.min(from.y, to.y));
      Double max = new Double(Math.max(from.x, to.x), Math.max(from.y, to.y));
      return new ConnectorWidget.Connector(
         angleDeg,
         fromV,
         toV,
         new Rectangle(MathHelper.func_76128_c(min.x), MathHelper.func_76128_c(min.y), MathHelper.func_76143_f(max.x), MathHelper.func_76143_f(max.y))
      );
   }

   public void renderConnection(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, float viewportScale) {
      int drawColor = this.color.getRGB();
      RenderSystem.disableTexture();
      GL11.glEnable(2848);
      GL11.glHint(3154, 4354);
      RenderSystem.lineWidth(6.0F * viewportScale);
      ScreenDrawHelper.draw(
         3,
         DefaultVertexFormats.field_181706_f,
         buf -> {
            this.drawLine(
               buf,
               matrixStack,
               this.connector.from.field_189982_i,
               this.connector.from.field_189983_j,
               this.connector.to.field_189982_i,
               this.connector.to.field_189983_j,
               drawColor
            );
            if (this.type == ConnectorWidget.ConnectorType.ARROW || this.type == ConnectorWidget.ConnectorType.DOUBLE_ARROW) {
               Vector2f arrowP1 = VectorHelper.rotateDegrees(this.connector.dir, 35.0F);
               this.drawLine(
                  buf,
                  matrixStack,
                  -arrowP1.field_189982_i * 10.0F + this.connector.to.field_189982_i,
                  -arrowP1.field_189983_j * 10.0F + this.connector.to.field_189983_j,
                  this.connector.to.field_189982_i,
                  this.connector.to.field_189983_j,
                  drawColor
               );
               Vector2f arrowP2 = VectorHelper.rotateDegrees(this.connector.dir, -35.0F);
               this.drawLine(
                  buf,
                  matrixStack,
                  -arrowP2.field_189982_i * 10.0F + this.connector.to.field_189982_i,
                  -arrowP2.field_189983_j * 10.0F + this.connector.to.field_189983_j,
                  this.connector.to.field_189982_i,
                  this.connector.to.field_189983_j,
                  drawColor
               );
            }

            if (this.type == ConnectorWidget.ConnectorType.DOUBLE_ARROW) {
               Vector2f arrowP1 = VectorHelper.rotateDegrees(this.connector.dir, 35.0F);
               this.drawLine(
                  buf,
                  matrixStack,
                  this.connector.from.field_189982_i,
                  this.connector.from.field_189983_j,
                  arrowP1.field_189982_i * 10.0F + this.connector.from.field_189982_i,
                  arrowP1.field_189983_j * 10.0F + this.connector.from.field_189983_j,
                  drawColor
               );
               Vector2f arrowP2 = VectorHelper.rotateDegrees(this.connector.dir, -35.0F);
               this.drawLine(
                  buf,
                  matrixStack,
                  this.connector.from.field_189982_i,
                  this.connector.from.field_189983_j,
                  arrowP2.field_189982_i * 10.0F + this.connector.from.field_189982_i,
                  arrowP2.field_189983_j * 10.0F + this.connector.from.field_189983_j,
                  drawColor
               );
            }
         }
      );
      RenderSystem.lineWidth(2.0F);
      GL11.glDisable(2848);
      RenderSystem.enableTexture();
   }

   private void drawLine(IVertexBuilder buf, MatrixStack renderStack, double lx, double ly, double hx, double hy, int color) {
      Matrix4f offset = renderStack.func_227866_c_().func_227870_a_();
      buf.func_227888_a_(offset, (float)lx, (float)ly, 0.0F).func_225586_a_(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, 255).func_181675_d();
      buf.func_227888_a_(offset, (float)hx, (float)hy, 0.0F).func_225586_a_(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, 255).func_181675_d();
   }

   private static class Connector {
      private final float angleDeg;
      private final Vector2f from;
      private final Vector2f to;
      private final Vector2f dir;
      private final Rectangle rct;

      private Connector(double angleDeg, Vector2f from, Vector2f to, Rectangle rectangle) {
         this.angleDeg = (float)angleDeg;
         this.from = from;
         this.to = to;
         this.rct = rectangle;
         this.dir = VectorHelper.normalize(new Vector2f(this.to.field_189982_i - this.from.field_189982_i, this.to.field_189983_j - this.from.field_189983_j));
      }
   }

   public static enum ConnectorType {
      LINE,
      ARROW,
      DOUBLE_ARROW;
   }
}
