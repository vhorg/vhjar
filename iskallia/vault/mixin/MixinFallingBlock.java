package iskallia.vault.mixin;

import iskallia.vault.world.gen.structure.JigsawPiecePlacer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({FallingBlock.class})
public class MixinFallingBlock {
   @Redirect(
      method = {"onPlace"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/Level;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V"
      )
   )
   public <T> void interceptBlockAddedTick(Level instance, BlockPos blockPos, Block block, int i) {
      if (!JigsawPiecePlacer.isPlacingRoom()) {
         instance.scheduleTick(blockPos, block, i);
      }
   }

   @Redirect(
      method = {"updateShape"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/LevelAccessor;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V"
      )
   )
   public <T> void interceptPostPlacementTick(LevelAccessor instance, BlockPos pos, Block block, int scheduledTime) {
      if (!JigsawPiecePlacer.isPlacingRoom()) {
         instance.scheduleTick(pos, block, scheduledTime);
      }
   }
}
