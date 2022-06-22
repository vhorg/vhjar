package iskallia.vault.mixin;

import net.minecraft.item.ItemUseContext;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.ActionResultType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({SpawnEggItem.class})
public class MixinSpawnEggItem {
   @Inject(
      method = {"onItemUse"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/spawner/AbstractSpawner;setEntityType(Lnet/minecraft/entity/EntityType;)V",
         shift = Shift.BEFORE
      )},
      cancellable = true
   )
   public void onItemUse(ItemUseContext context, CallbackInfoReturnable<ActionResultType> cir) {
      if (context.func_195999_j() != null && !context.func_195999_j().func_184812_l_()) {
         cir.setReturnValue(ActionResultType.PASS);
      }
   }
}
