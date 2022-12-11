package iskallia.vault.mixin;

import iskallia.vault.core.vault.ClientVaults;
import net.minecraft.client.player.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({KeyboardInput.class})
public abstract class MixinKeyboardInput {
   @Invoker("calculateImpulse")
   public static float calculateImpulse(boolean first, boolean second) {
      throw new AssertionError();
   }

   @Redirect(
      method = {"tick"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/player/KeyboardInput;calculateImpulse(ZZ)F",
         ordinal = 0
      )
   )
   private float calculateAdjustedUpDownInput(boolean up, boolean down) {
      up = up && ClientVaults.CONTROLS_PROPERTIES.canMoveForward();
      down = down && ClientVaults.CONTROLS_PROPERTIES.canMoveForward();
      return calculateImpulse(up, down);
   }

   @Redirect(
      method = {"tick"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/player/KeyboardInput;calculateImpulse(ZZ)F",
         ordinal = 1
      )
   )
   private float calculateAdjustedLeftRightInput(boolean left, boolean right) {
      return ClientVaults.CONTROLS_PROPERTIES.isLeftAndRightSwapped() ? calculateImpulse(right, left) : calculateImpulse(left, right);
   }

   @Inject(
      method = {"tick"},
      at = {@At("TAIL")}
   )
   private void updateJump(boolean pIsMovingSlowly, CallbackInfo ci) {
      KeyboardInput instance = (KeyboardInput)this;
      instance.jumping = instance.jumping && ClientVaults.CONTROLS_PROPERTIES.canJump();
   }
}
