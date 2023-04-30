package iskallia.vault.mixin;

import iskallia.vault.entity.Targeting;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.sensing.Sensor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Sensor.class})
public class MixinSensor {
   @Inject(
      method = {"isEntityAttackable"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void checkVaultAttackable(LivingEntity attacker, LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
      updateAttackableResultWithOverride(attacker, target, cir);
   }

   @Inject(
      method = {"isEntityAttackableIgnoringLineOfSight"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void checkVaultAttackableIgnoringLineOfSight(LivingEntity attacker, LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
      updateAttackableResultWithOverride(attacker, target, cir);
   }

   private static void updateAttackableResultWithOverride(LivingEntity attacker, LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
      Targeting.TargetingResult targetingResult = Targeting.getTargetingResult(attacker, target);
      if (targetingResult != Targeting.TargetingResult.DEFAULT) {
         cir.setReturnValue(targetingResult.getShouldTarget());
         cir.cancel();
      }
   }
}
