package iskallia.vault.mixin;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({SpawnEggItem.class})
public class MixinSpawnEggItem {
   @Inject(
      method = {"useOn"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/BaseSpawner;setEntityId(Lnet/minecraft/world/entity/EntityType;)V",
         shift = Shift.BEFORE
      )},
      cancellable = true
   )
   public void onItemUse(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
      if (context.getPlayer() != null && !context.getPlayer().isCreative()) {
         cir.setReturnValue(InteractionResult.PASS);
      }
   }
}
