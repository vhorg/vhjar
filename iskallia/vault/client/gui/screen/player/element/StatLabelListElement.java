package iskallia.vault.client.gui.screen.player.element;

import iskallia.vault.client.gui.framework.element.ElasticContainerElement;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import java.util.List;

public class StatLabelListElement<E extends StatLabelListElement<E>> extends ElasticContainerElement<E> {
   public StatLabelListElement(ISpatial spatial, List<StatLabelElementBuilder<?>> statList) {
      super(spatial);

      for (int i = 0; i < statList.size(); i++) {
         this.addElement(statList.get(i).build(spatial.width(), i));
      }
   }
}
