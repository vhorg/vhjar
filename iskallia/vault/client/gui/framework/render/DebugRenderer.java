package iskallia.vault.client.gui.framework.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import iskallia.vault.client.gui.framework.render.spi.IDebugRenderer;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.util.LineRenderUtil;
import net.minecraft.client.renderer.GameRenderer;

public class DebugRenderer implements IDebugRenderer {
   private final BufferBuilder bufferBuilder;

   public DebugRenderer(BufferBuilder bufferBuilder) {
      this.bufferBuilder = bufferBuilder;
   }

   @Override
   public void begin() {
      this.bufferBuilder.begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
   }

   @Override
   public void renderSpatial(PoseStack poseStack, ISpatial spatial, int color) {
      LineRenderUtil util = LineRenderUtil.getInstance();
      util.drawLine(this.bufferBuilder, poseStack, spatial.left(), spatial.top(), spatial.right(), spatial.top(), 0.5, color);
      util.drawLine(this.bufferBuilder, poseStack, spatial.right(), spatial.top(), spatial.right(), spatial.bottom(), 0.5, color);
      util.drawLine(this.bufferBuilder, poseStack, spatial.left(), spatial.bottom(), spatial.right(), spatial.bottom(), 0.5, color);
      util.drawLine(this.bufferBuilder, poseStack, spatial.left(), spatial.top(), spatial.left(), spatial.bottom(), 0.5, color);
   }

   @Override
   public void end() {
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      this.bufferBuilder.end();
      BufferUploader.end(this.bufferBuilder);
   }
}
