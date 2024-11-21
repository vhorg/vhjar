package iskallia.vault.event.event;

import com.mojang.blaze3d.platform.Window;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Event;

@OnlyIn(Dist.CLIENT)
public class WindowResizeEvent extends Event {
   private final Window window;

   public WindowResizeEvent(Window window) {
      this.window = window;
   }

   public Window getWindow() {
      return this.window;
   }
}
