package iskallia.vault.mixin;

import iskallia.vault.init.ModBlocks;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.p3pp3rf1y.sophisticatedbackpacks.common.CommonEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {CommonEventHandler.class},
   remap = false
)
public class MixinSBCommonEventHandler {
   @Inject(
      method = {"onItemPickup"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void cancelIfInDebagnetizerRange(EntityItemPickupEvent event, CallbackInfo ci) {
      if (event.getEntity() != null && ModBlocks.DEBAGNETIZER.isInRange(event.getEntity().level.dimension(), event.getEntity().blockPosition())) {
         ci.cancel();
      }
   }
}
