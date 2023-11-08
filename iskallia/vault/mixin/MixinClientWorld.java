package iskallia.vault.mixin;

import iskallia.vault.core.event.ClientEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ClientLevel.class})
public abstract class MixinClientWorld {
   @Inject(
      method = {"effects"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void effects(CallbackInfoReturnable<DimensionSpecialEffects> ci) {
      DimensionSpecialEffects effects = ClientEvents.WORLD_EFFECT.invoke((DimensionSpecialEffects)ci.getReturnValue()).getEffects();
      if (ci.getReturnValue() != effects) {
         ci.setReturnValue(effects);
      }
   }
}
