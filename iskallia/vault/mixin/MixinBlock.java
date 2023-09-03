package iskallia.vault.mixin;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Block.class})
public class MixinBlock {
   @Inject(
      method = {"getSoundType"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getSoundType(BlockState state, CallbackInfoReturnable<SoundType> cir) {
      if (state.getBlock() instanceof LiquidBlock) {
         cir.setReturnValue(SoundType.HONEY_BLOCK);
      }
   }
}
