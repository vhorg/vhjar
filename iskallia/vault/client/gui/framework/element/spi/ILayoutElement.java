package iskallia.vault.client.gui.framework.element.spi;

import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;

public interface ILayoutElement<T extends ILayoutElement<T>> extends ISpatialElement {
   T layout(ILayoutStrategy var1);

   void onLayout(ISize var1, ISpatial var2, ISpatial var3);
}
