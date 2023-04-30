package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.framework.element.spi.AbstractSpatialElement;
import iskallia.vault.client.gui.framework.element.spi.IRenderedElement;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import java.awt.Color;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public class ColorSquareElement extends AbstractSpatialElement<ColorSquareElement> implements IRenderedElement {
   protected boolean visible;
   protected Supplier<Color> colorSupplier;

   public ColorSquareElement(ISpatial spatial, Supplier<Color> colorSupplier) {
      super(spatial);
      this.colorSupplier = colorSupplier;
      this.visible = true;
   }

   @Override
   public void setVisible(boolean visible) {
      this.visible = visible;
   }

   @Override
   public boolean isVisible() {
      return this.visible;
   }

   public Supplier<Color> getColorSupplier() {
      return this.colorSupplier;
   }

   public void setColorSupplier(Supplier<Color> colorSupplier) {
      this.colorSupplier = colorSupplier;
   }

   @Override
   public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      ScreenRenderers.getImmediate().renderColoredQuad(poseStack, this.colorSupplier.get().getRGB(), this.getWorldSpatial());
   }
}
