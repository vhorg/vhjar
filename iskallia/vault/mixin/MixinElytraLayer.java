package iskallia.vault.mixin;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ElytraLayer.class})
public abstract class MixinElytraLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
   public MixinElytraLayer(RenderLayerParent<T, M> renderer) {
      super(renderer);
   }

   @Inject(
      method = {"shouldRender"},
      at = {@At("HEAD")},
      cancellable = true,
      remap = false
   )
   public void shouldRender(ItemStack stack, T entity, CallbackInfoReturnable<Boolean> ci) {
   }
}
