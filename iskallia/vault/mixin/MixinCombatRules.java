package iskallia.vault.mixin;

import iskallia.vault.util.StatUtils;
import net.minecraft.world.damagesource.CombatRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({CombatRules.class})
public class MixinCombatRules {
   @Inject(
      method = {"getDamageAfterAbsorb"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void armorEffect(float dmg, float armor, float toughness, CallbackInfoReturnable<Float> cir) {
      cir.setReturnValue(dmg * StatUtils.getArmorMultiplier(armor));
   }
}
