package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.element.spi.IRenderedElement;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import org.jetbrains.annotations.NotNull;

public class ClipContainerElement<E extends ClipContainerElement<E>> extends ContainerElement<E> {
   public ClipContainerElement(ISpatial spatial) {
      super(spatial);
   }

   @Override
   public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      renderer.beginClipRegion(this.getWorldSpatial());

      for (IRenderedElement element : this.elementStore.getRenderedElementList()) {
         if (element.isVisible()) {
            element.render(renderer, poseStack, mouseX, mouseY, partialTick);
         }
      }

      renderer.endClipRegion();
   }
}
