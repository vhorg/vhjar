package iskallia.vault.mixin;

import iskallia.vault.init.ModBlocks;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({AnvilBlock.class})
public class AnvilBlockMixin {
   @Inject(
      method = {"damage"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private static void damage(BlockState state, CallbackInfoReturnable<BlockState> cir) {
      if (state.is(ModBlocks.VAULT_ANVIL)) {
         cir.setReturnValue(state);
      }
   }
}
