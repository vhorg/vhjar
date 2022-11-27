package iskallia.vault.mixin;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class MixinColorRunesModule {
   private static void foilCanHaveRune(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
      if (stack.hasFoil()) {
         cir.setReturnValue(true);
      }
   }
}
