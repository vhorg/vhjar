package iskallia.vault.client.gui.framework.screen.layout;

import net.minecraft.client.Minecraft;

public class ScreenLayout {
   public static void requestLayout() {
      if (Minecraft.getInstance().screen instanceof ILayoutScreen screen) {
         screen.requestLayout();
      }
   }

   private ScreenLayout() {
   }
}
