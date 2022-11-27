package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.element.spi.AbstractSpatialElement;
import iskallia.vault.client.gui.framework.element.spi.IRenderedElement;
import iskallia.vault.client.gui.framework.render.NineSlice;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import org.jetbrains.annotations.NotNull;

public class NineSliceElement<E extends NineSliceElement<E>> extends AbstractSpatialElement<E> implements IRenderedElement {
   protected final NineSlice.TextureRegion textureRegion;
   protected boolean visible;

   public NineSliceElement(ISpatial spatial, NineSlice.TextureRegion textureRegion) {
      super(spatial);
      this.textureRegion = textureRegion;
      this.setVisible(true);
   }

   public NineSlice.Slices slices() {
      return this.textureRegion.slices();
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
      renderer.render(this.textureRegion, poseStack, this.worldSpatial);
   }
}
