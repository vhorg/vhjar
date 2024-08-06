package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.element.spi.AbstractSpatialElement;
import iskallia.vault.client.gui.framework.element.spi.IRenderedElement;
import iskallia.vault.client.gui.framework.render.ThreeSliceHorizontal;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import org.jetbrains.annotations.NotNull;

public class ThreeSliceHorizontalElement<E extends ThreeSliceHorizontalElement<E>> extends AbstractSpatialElement<E> implements IRenderedElement {
   protected final ThreeSliceHorizontal.TextureRegion textureRegion;
   protected boolean visible;

   public ThreeSliceHorizontalElement(ISpatial spatial, ThreeSliceHorizontal.TextureRegion textureRegion) {
      super(spatial);
      this.textureRegion = textureRegion;
      this.setVisible(true);
   }

   @Override
   public void setVisible(boolean visible) {
      this.visible = visible;
   }

   @Override
   public boolean isVisible() {
      return this.visible;
   }

   @Override
   public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      this.textureRegion
         .blit(poseStack, this.worldSpatial.x(), this.worldSpatial.y(), this.worldSpatial.z(), this.worldSpatial.width(), this.worldSpatial.height());
   }
}
