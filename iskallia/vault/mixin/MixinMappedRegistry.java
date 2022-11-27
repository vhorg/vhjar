package iskallia.vault.mixin;

import iskallia.vault.world.gen.structure.IRegistryIdentifiable;
import net.minecraft.core.MappedRegistry;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({MappedRegistry.class})
public class MixinMappedRegistry<T> {
   @Inject(
      method = {"getKey"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getKey(T value, CallbackInfoReturnable<ResourceLocation> cir) {
      if (value instanceof IRegistryIdentifiable) {
         cir.setReturnValue(((IRegistryIdentifiable)value).getId());
      }
   }
}
