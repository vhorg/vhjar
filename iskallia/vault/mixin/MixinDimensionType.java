package iskallia.vault.mixin;

import iskallia.vault.core.event.ClientEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({DimensionType.class})
public class MixinDimensionType {
   @Inject(
      method = {"effectsLocation"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void getEffects(CallbackInfoReturnable<ResourceLocation> cir) {
      ResourceLocation id = (ResourceLocation)cir.getReturnValue();
      ResourceLocation other = ClientEvents.WORLD_EFFECT_LOCATION.invoke(id).getId();
      if (!id.equals(other)) {
         cir.setReturnValue(other);
      }
   }
}
