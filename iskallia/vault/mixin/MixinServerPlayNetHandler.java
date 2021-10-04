package iskallia.vault.mixin;

import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.client.CPlayerPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ServerPlayNetHandler.class})
public class MixinServerPlayNetHandler {
   private boolean doesOwnerCheck = false;

   @Inject(
      method = {"processPlayer"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/play/ServerPlayNetHandler;func_217264_d()Z",
         shift = Shift.BEFORE
      )}
   )
   public void onSpeedCheck(CPlayerPacket packetIn, CallbackInfo ci) {
      this.doesOwnerCheck = true;
   }

   @Inject(
      method = {"func_217264_d"},
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
