package iskallia.vault.mixin;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientPacketListener.class})
public class MixinClientPacketListener {
   @Inject(
      method = {"handleEntityEvent"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void interceptClientEntityEvent(ClientboundEntityEventPacket packet, CallbackInfo ci) {
      if (packet.getEventId() == 60) {
         ci.cancel();
      }
   }
}
