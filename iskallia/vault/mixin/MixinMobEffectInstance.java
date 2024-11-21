package iskallia.vault.mixin;

import iskallia.vault.init.ModEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({MobEffectInstance.class})
public abstract class MixinMobEffectInstance {
   @Shadow
   public int duration;
   @Shadow
   private int amplifier;

   @Shadow
   public abstract MobEffect getEffect();

   @Inject(
      method = {"update"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void specialEffectMerge(MobEffectInstance other, CallbackInfoReturnable<Boolean> ci) {
      if (this.getEffect() == ModEffects.CORRUPTION && other.getEffect() == ModEffects.CORRUPTION) {
         this.duration = Math.max(this.duration, other.duration);
         this.amplifier = this.amplifier + other.getAmplifier() + 1;
         ci.setReturnValue(true);
      }
   }
}
