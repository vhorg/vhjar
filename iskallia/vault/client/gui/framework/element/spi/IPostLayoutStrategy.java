package iskallia.vault.client.gui.framework.element.spi;

import iskallia.vault.client.gui.framework.spatial.spi.IMutableSpatial;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;

@FunctionalInterface
public interface IPostLayoutStrategy {
   IPostLayoutStrategy NONE = (screen, gui, parent, world) -> false;

   boolean apply(ISize var1, ISpatial var2, ISpatial var3, IMutableSpatial var4);
}
