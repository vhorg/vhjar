package iskallia.vault.client.gui.framework.element;

import iskallia.vault.client.gui.framework.element.spi.IRenderedElement;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;

public class TabElement<E extends TabElement<E>> extends ElasticContainerElement<E> {
   protected final Runnable onClick;

   public TabElement(IPosition position, IRenderedElement background, IRenderedElement icon, Runnable onClick) {
      super(Spatials.positionXYZ(position));
      this.addElement(background);
      this.addElement(icon);
      this.onClick = onClick;
   }

   @Override
   public boolean onMouseClicked(double mouseX, double mouseY, int buttonIndex) {
      this.onClick.run();
      return true;
   }
}
