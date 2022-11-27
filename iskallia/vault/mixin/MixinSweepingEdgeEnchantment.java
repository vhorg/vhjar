package iskallia.vault.mixin;

import net.minecraft.world.item.enchantment.SweepingEdgeEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({SweepingEdgeEnchantment.class})
public class MixinSweepingEdgeEnchantment {
   @Inject(
      method = {"getSweepingDamageRatio"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void noSweepingRatio(int pLevel, CallbackInfoReturnable<Float> cir) {
      cir.setReturnValue(0.0F);
   }
}
