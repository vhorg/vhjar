package iskallia.vault.client.gui.screen.player.legacy.widget.connect;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import iskallia.vault.client.gui.helper.ScreenDrawHelper;
import iskallia.vault.client.util.LineRenderUtil;
import iskallia.vault.util.VectorHelper;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D.Double;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

public class ConnectorWidget extends AbstractWidget {
   private final ConnectorWidget.ConnectorType type;
   private final ConnectorWidget.Connector connector;
   private Color color = new Color(11184810);

   public ConnectorWidget(ConnectableWidget source, ConnectableWidget target, ConnectorWidget.ConnectorType type) {
      this(buildWidgetBox(source, target), type);
   }

   private ConnectorWidget(ConnectorWidget.Connector connector, ConnectorWidget.ConnectorType type) {
      super(connector.rectangle.x, connector.rectangle.y, connector.rectangle.width, connector.rectangle.height, new TextComponent("Connector"));
      this.type = type;
      this.connector = connector;
   }

   public ConnectorWidget setColor(Color color) {
      this.color = color;
      return this;
   }

   private static ConnectorWidget.Connector buildWidgetBox(ConnectableWidget source, ConnectableWidget target) {
      Double from = source.getRenderPosition();
      Double to = target.getRenderPosition();
      Vec2 dir = new Vec2((float)(to.x - from.x), (float)(to.y - from.y));
      float angle = (float)Math.atan2(dir.x, dir.y);
      double angleDeg = Math.toDegrees(angle) - 90.0;
      from = source.getPointOnEdge(angleDeg);
      from.x = from.x + source.getRenderWidth() / 2.0;
      from.y = from.y + source.getRenderHeight() / 2.0;
      Vec2 fromV = new Vec2((float)from.x, (float)from.y);
      to = target.getPointOnEdge(angleDeg - 180.0);
      to.x = to.x + source.getRenderWidth() / 2.0;
      to.y = to.y + source.getRenderHeight() / 2.0;
      Vec2 toV = new Vec2((float)to.x, (float)to.y);
      return new ConnectorWidget.Connector(
         fromV,
         toV,
         new Rectangle(Mth.floor(Math.min(from.x, to.x)), Mth.floor(Math.min(from.y, to.y)), Mth.ceil(Math.max(from.x, to.x)), Mth.ceil(Math.max(from.y, to.y)))
      );
   }

   public void renderConnection(PoseStack matrixStack) {
      int drawColor = this.color.getRGB();
      RenderSystem.disableTexture();
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      ScreenDrawHelper.draw(
         Mode.QUADS,
         DefaultVertexFormat.POSITION_COLOR,
         buffer -> {
            LineRenderUtil.getInstance()
               .drawLine(buffer, matrixStack, this.connector.from.x, this.connector.from.y, this.connector.to.x, this.connector.to.y, 1.0, drawColor);
            if (this.type == ConnectorWidget.ConnectorType.ARROW || this.type == ConnectorWidget.ConnectorType.DOUBLE_ARROW) {
               Vec2 arrowP1 = VectorHelper.rotateDegrees(this.connector.dir, 35.0F);
               LineRenderUtil.getInstance()
                  .drawLine(
                     buffer,
                     matrixStack,
                     -arrowP1.x * 10.0F + this.connector.to.x,
                     -arrowP1.y * 10.0F + this.connector.to.y,
                     this.connector.to.x,
                     this.connector.to.y,
                     1.0,
                     drawColor
                  );
               Vec2 arrowP2 = VectorHelper.rotateDegrees(this.connector.dir, -35.0F);
               LineRenderUtil.getInstance()
                  .drawLine(
                     buffer,
                     matrixStack,
                     -arrowP2.x * 10.0F + this.connector.to.x,
                     -arrowP2.y * 10.0F + this.connector.to.y,
                     this.connector.to.x,
                     this.connector.to.y,
                     1.0,
                     drawColor
                  );
            }

            if (this.type == ConnectorWidget.ConnectorType.DOUBLE_ARROW) {
               Vec2 arrowP1 = VectorHelper.rotateDegrees(this.connector.dir, 35.0F);
               LineRenderUtil.getInstance()
                  .drawLine(
                     buffer,
                     matrixStack,
                     this.connector.from.x,
                     this.connector.from.y,
                     arrowP1.x * 10.0F + this.connector.from.x,
                     arrowP1.y * 10.0F + this.connector.from.y,
                     1.0,
                     drawColor
                  );
               Vec2 arrowP2 = VectorHelper.rotateDegrees(this.connector.dir, -35.0F);
               LineRenderUtil.getInstance()
                  .drawLine(
                     buffer,
                     matrixStack,
                     this.connector.from.x,
                     this.connector.from.y,
                     arrowP2.x * 10.0F + this.connector.from.x,
                     arrowP2.y * 10.0F + this.connector.from.y,
                     1.0,
                     drawColor
                  );
            }
         }
      );
      RenderSystem.enableTexture();
   }

   public void updateNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
   }

   private static class Connector {
      private final Vec2 from;
      private final Vec2 to;
      private final Vec2 dir;
      private final Rectangle rectangle;

      private Connector(Vec2 from, Vec2 to, Rectangle rectangle) {
         this.from = from;
         this.to = to;
         this.rectangle = rectangle;
         this.dir = new Vec2(this.to.x - this.from.x, this.to.y - this.from.y).normalized();
      }
   }

   public static enum ConnectorType {
      LINE,
      ARROW,
      DOUBLE_ARROW;
   }
}
