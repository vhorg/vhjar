package iskallia.vault.mixin;

import iskallia.vault.block.VaultCrateBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BlockItem.class})
public class MixinBlockItem {
   @Inject(
      method = {"canPlace"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void canPlace(BlockPlaceContext context, BlockState state, CallbackInfoReturnable<Boolean> cir) {
      Level level = context.getLevel();
      BlockPos clickedPos = context.getClickedPos().relative(context.getClickedFace().getOpposite());
      if (level.getBlockState(clickedPos).getBlock() instanceof VaultCrateBlock) {
         cir.setReturnValue(false);
      }
   }
}
