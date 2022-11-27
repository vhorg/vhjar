package iskallia.vault.mixin;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.BlockSetEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Level.class})
public abstract class MixinWorld {
   @Inject(
      method = {"setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z"},
      at = {@At("HEAD")}
   )
   public void setBlockHead(BlockPos pos, BlockState state, int flags, int recursionLeft, CallbackInfoReturnable<Boolean> cir) {
      CommonEvents.BLOCK_SET.invoke((LevelWriter)this, pos, state, flags, recursionLeft, BlockSetEvent.Type.HEAD);
   }

   @Inject(
      method = {"setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z"},
      at = {@At("RETURN")}
   )
   public void setBlockReturn(BlockPos pos, BlockState state, int flags, int recursionLeft, CallbackInfoReturnable<Boolean> cir) {
      CommonEvents.BLOCK_SET.invoke((LevelWriter)this, pos, state, flags, recursionLeft, BlockSetEvent.Type.RETURN);
   }
}
