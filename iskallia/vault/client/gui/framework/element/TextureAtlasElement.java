package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.element.spi.AbstractSpatialElement;
import iskallia.vault.client.gui.framework.element.spi.IRenderedElement;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import org.jetbrains.annotations.NotNull;

public class TextureAtlasElement<E extends TextureAtlasElement<E>> extends AbstractSpatialElement<E> implements IRenderedElement {
   private final TextureAtlasRegion textureAtlasRegion;
   private boolean visible;

   public TextureAtlasElement(TextureAtlasRegion textureAtlasRegion) {
      this(IPosition.ZERO, textureAtlasRegion);
   }

   public TextureAtlasElement(IPosition position, TextureAtlasRegion textureAtlasRegion) {
      this(position, Spatials.size(textureAtlasRegion), textureAtlasRegion);
   }

   public TextureAtlasElement(IPosition position, ISize size, TextureAtlasRegion textureAtlasRegion) {
      super(Spatials.positionXYZ(position).size(size));
      this.textureAtlasRegion = textureAtlasRegion;
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
      renderer.render(this.textureAtlasRegion, poseStack, this.worldSpatial);
   }
}
