package iskallia.vault.mixin;

import iskallia.vault.world.data.ServerVaults;
import java.util.Optional;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({PiglinAi.class})
public class MixinPiglinAi {
   @Inject(
      method = {"findNearestValidAttackTarget"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void findNearestValidAttackTarget(Piglin piglin, CallbackInfoReturnable<Optional<? extends LivingEntity>> cir) {
      if (ServerVaults.get(piglin.level).isPresent()) {
         cir.setReturnValue(Optional.ofNullable(piglin.getTarget()));
      }
   }
}
