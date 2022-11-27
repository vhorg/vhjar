package iskallia.vault.client.gui.screen.player.element;

import iskallia.vault.client.gui.framework.element.VerticalScrollClipContainer;
import iskallia.vault.client.gui.framework.spatial.Padding;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import java.util.List;

public class StatListVaultContainerElement extends VerticalScrollClipContainer<StatListVaultContainerElement> {
   public StatListVaultContainerElement(ISpatial spatial, List<StatLabelElementBuilder<?>> statList) {
      super(spatial, Padding.of(2, 0));
      this.addElement(
         (StatLabelListElement)new StatLabelListElement(Spatials.positionY(3), statList).layout((screen, gui, parent, world) -> world.width(this.innerWidth()))
      );
   }
}
