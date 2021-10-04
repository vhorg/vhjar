package iskallia.vault.mixin;

import iskallia.vault.entity.VaultBoss;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({EnchantmentHelper.class})
public class MixinEnchantmentHelper {
   @Inject(
      method = {"getDepthStriderModifier"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private static void modifyBossDepthStrider(LivingEntity entityIn, CallbackInfoReturnable<Integer> cir) {
      if (entityIn instanceof VaultBoss) {
         cir.setReturnValue(3);
      }
   }
}
