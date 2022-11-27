package iskallia.vault.mixin;

import iskallia.vault.world.data.ServerVaults;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
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
   public void onFireTick(BlockState state, ServerLevel world, BlockPos pos, Random rand, CallbackInfo ci) {
      if (ServerVaults.isVaultWorld(world)) {
         ci.cancel();
      }
   }
}
