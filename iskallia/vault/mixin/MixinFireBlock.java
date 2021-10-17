package iskallia.vault.mixin;

import iskallia.vault.Vault;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.FireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({FireBlock.class})
public class MixinFireBlock {
   @Inject(
      method = {"tick"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onFireTick(BlockState state, ServerWorld world, BlockPos pos, Random rand, CallbackInfo ci) {
      if (world.func_234923_W_() == Vault.VAULT_KEY) {
         ci.cancel();
      }
   }
}
