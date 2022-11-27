package iskallia.vault.client.gui.framework.render.spi;

import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;

public interface IClipRegionStrategy {
   void beginFrame();

   void endFrame();

   void beginClipRegion(ISpatial var1);

   void endClipRegion();
}
