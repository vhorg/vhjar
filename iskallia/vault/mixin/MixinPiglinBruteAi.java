package iskallia.vault.mixin;

import iskallia.vault.entity.Targeting;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.piglin.PiglinBruteAi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({PiglinBruteAi.class})
public class MixinPiglinBruteAi {
   @Inject(
      method = {"setAngerTarget"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void checkAttackable(PiglinBrute piglinBrute, LivingEntity target, CallbackInfo ci) {
      if (Targeting.getTargetingResult(piglinBrute, target) == Targeting.TargetingResult.IGNORE) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"isNearestValidAttackTarget"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void checkTargetOverrides(AbstractPiglin piglin, LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
      Targeting.TargetingResult targetingResult = Targeting.getTargetingResult(piglin, entity);
      if (targetingResult != Targeting.TargetingResult.DEFAULT) {
         cir.setReturnValue(targetingResult.getShouldTarget());
         cir.cancel();
      }
   }
}
