package iskallia.vault.client.gui.framework.element.spi;

import iskallia.vault.client.gui.framework.spatial.spi.IMutableSpatial;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;

@FunctionalInterface
public interface ILayoutStrategy {
   ILayoutStrategy NONE = (screen, gui, parent, world) -> {};

   void apply(ISize var1, ISpatial var2, ISpatial var3, IMutableSpatial var4);
}
