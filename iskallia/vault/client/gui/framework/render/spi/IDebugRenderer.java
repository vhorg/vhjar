package iskallia.vault.client.gui.framework.render.spi;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;

public interface IDebugRenderer {
   IDebugRenderer NONE = new IDebugRenderer() {
      @Override
      public void begin() {
      }

      @Override
      public void renderSpatial(PoseStack poseStack, ISpatial spatial, int color) {
      }

      @Override
      public void end() {
      }
   };

   void begin();

   void renderSpatial(PoseStack var1, ISpatial var2, int var3);

   void end();
}
