package iskallia.vault.client.gui.framework.element.spi;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.render.spi.IDebugRenderer;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;

public interface ISpatialElement extends IElement, ISpatial {
   ISpatial getFixedSpatial();

   ISpatial getWorldSpatial();

   void enableLayoutDebugLogging();

   ISpatialElement enableSpatialDebugRender(boolean var1, boolean var2);

   void renderDebug(IDebugRenderer var1, PoseStack var2);
}
