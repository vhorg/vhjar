package iskallia.vault.mixin;

import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ServerGamePacketListenerImpl.class})
public class MixinServerPlayNetHandler {
   private boolean doesOwnerCheck = false;

   @Inject(
      method = {"handleMovePlayer"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;isSingleplayerOwner()Z",
         shift = Shift.BEFORE
      )}
   )
   public void onSpeedCheck(ServerboundMovePlayerPacket packetIn, CallbackInfo ci) {
      this.doesOwnerCheck = true;
   }

   @Inject(
      method = {"isSingleplayerOwner"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void isOwnerCheck(CallbackInfoReturnable<Boolean> cir) {
      if (this.doesOwnerCheck) {
         cir.setReturnValue(true);
      }

      this.doesOwnerCheck = false;
   }
}
