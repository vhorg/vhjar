package iskallia.vault.client.gui.framework.element;

import iskallia.vault.client.gui.framework.element.spi.ISpatialElement;
import java.util.function.Consumer;

public interface SelectableElement<E extends SelectableElement<E>> extends ISpatialElement {
   void setSelected(boolean var1);

   boolean isSelected();

   void onSelect(Consumer<E> var1);
}
