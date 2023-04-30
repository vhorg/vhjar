package iskallia.vault.mixin;

import iskallia.vault.world.data.ServerVaults;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.LavaFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LavaFluid.class})
public abstract class MixinLavaFluid {
   @Inject(
      method = {"randomTick"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onRandomTick(Level world, BlockPos pos, FluidState state, Random random, CallbackInfo ci) {
      if (ServerVaults.get(world).isPresent()) {
         ci.cancel();
      }
   }
}
