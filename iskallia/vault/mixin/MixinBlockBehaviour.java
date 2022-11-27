package iskallia.vault.mixin;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.BlockUseEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BlockStateBase.class})
public abstract class MixinBlockBehaviour {
   @Shadow
   protected abstract BlockState asState();

   @Shadow
   public abstract Block getBlock();

   @Inject(
      method = {"use"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void use(Level world, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
      BlockState state = this.asState();
      BlockPos pos = hit.getBlockPos();
      BlockUseEvent.Data data = CommonEvents.BLOCK_USE.invoke(world, state, pos, player, hand, hit);
      if (data.getResult() == null) {
         data.setResult(this.getBlock().use(state, world, pos, player, hand, hit));
      }

      cir.setReturnValue(data.getResult());
   }

   @Inject(
      method = {"isPathfindable"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void isPathfindable(BlockGetter pLevel, BlockPos pPos, PathComputationType pType, CallbackInfoReturnable<Boolean> cir) {
      Block block = this.asState().getBlock();
      if (block == Blocks.AZALEA || block == Blocks.FLOWERING_AZALEA) {
         cir.setReturnValue(false);
      }
   }
}
