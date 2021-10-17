package iskallia.vault.mixin;

import iskallia.vault.Vault;
import java.util.Random;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LavaFluid.class})
public class MixinLavaFluid {
   @Inject(
      method = {"randomTick"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onRandomTick(World world, BlockPos pos, FluidState state, Random random, CallbackInfo ci) {
      if (world.func_234923_W_() == Vault.VAULT_KEY) {
         ci.cancel();
      }
   }
}
