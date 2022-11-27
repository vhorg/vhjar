package iskallia.vault.mixin;

import iskallia.vault.entity.VaultBoss;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({EnchantmentHelper.class})
public class MixinEnchantmentHelper {
   @Inject(
      method = {"getDepthStrider"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private static void modifyBossDepthStrider(LivingEntity entityIn, CallbackInfoReturnable<Integer> cir) {
      if (entityIn instanceof VaultBoss) {
         cir.setReturnValue(3);
      }
   }

   @Inject(
      method = {"getFireAspect"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void noFireAspect(LivingEntity pPlayer, CallbackInfoReturnable<Integer> cir) {
      cir.setReturnValue(0);
   }
}
