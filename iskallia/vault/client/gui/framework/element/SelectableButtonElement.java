package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

public class SelectableButtonElement<E extends SelectableButtonElement<E>> extends ButtonElement<E> implements SelectableElement<E> {
   private boolean selected = false;

   public SelectableButtonElement(IPosition position, ButtonElement.ButtonTextures textures, Runnable onClick) {
      super(position, textures, onClick);
   }

   @Override
   public void setSelected(boolean selected) {
      this.selected = selected;
   }

   @Override
   public boolean isSelected() {
      return this.selected;
   }

   @Override
   public void onSelect(Consumer<E> onSelect) {
      this.setOnClick(onSelect);
   }

   @Override
   public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      TextureAtlasRegion texture = this.textures.selectTexture(this.isDisabled(), this.containsMouse(mouseX, mouseY), this.isSelected());
      renderer.render(texture, poseStack, this.worldSpatial);
   }
}
