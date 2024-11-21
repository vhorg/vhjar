package iskallia.vault.mixin;

import com.mojang.blaze3d.platform.Window;
import iskallia.vault.event.event.WindowResizeEvent;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Window.class})
public class MixinWindow {
   @Inject(
      method = {"onFramebufferResize"},
      at = {@At("TAIL")}
   )
   public void onResize(long windowId, int width, int height, CallbackInfo ci) {
      Window thisWindow = (Window)this;
      if (windowId == thisWindow.getWindow()) {
         WindowResizeEvent event = new WindowResizeEvent(thisWindow);
         MinecraftForge.EVENT_BUS.post(event);
      }
   }
}
