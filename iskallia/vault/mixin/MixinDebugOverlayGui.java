package iskallia.vault.mixin;

import iskallia.vault.block.property.HiddenIntegerProperty;
import java.util.Map.Entry;
import net.minecraft.client.gui.overlay.DebugOverlayGui;
import net.minecraft.state.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({DebugOverlayGui.class})
public class MixinDebugOverlayGui {
   @Inject(
      method = {"getPropertyString"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void hidePropertyString(Entry<Property<?>, Comparable<?>> entryIn, CallbackInfoReturnable<String> cir) {
      if (entryIn.getKey() instanceof HiddenIntegerProperty) {
         cir.setReturnValue(entryIn.getKey().func_177701_a() + ": unknown");
      }
   }
}
