package iskallia.vault.mixin;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ItemStack.class})
public abstract class MixinItemStackClient {
   @Inject(
      method = {"hasCustomHoverName"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void doDisplayNameItalic(CallbackInfoReturnable<Boolean> cir) {
      cir.setReturnValue(false);
   }
}
