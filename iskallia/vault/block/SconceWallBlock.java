package iskallia.vault.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class SconceWallBlock extends WallTorchBlock {
   public SconceWallBlock() {
      super(Properties.copy(Blocks.TORCH), ParticleTypes.FLAME);
   }

   public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, Random pRand) {
      Direction direction = (Direction)pState.getValue(FACING);
      double d0 = pPos.getX() + 0.5;
      double d1 = pPos.getY() + 0.7;
      double d2 = pPos.getZ() + 0.5;
      Direction direction1 = direction.getOpposite();
      pLevel.addParticle(ParticleTypes.SMOKE, d0 + 0.17 * direction1.getStepX(), d1 + 0.22, d2 + 0.17 * direction1.getStepZ(), 0.0, 0.0, 0.0);
      pLevel.addParticle(this.flameParticle, d0 + 0.17 * direction1.getStepX(), d1 + 0.22, d2 + 0.17 * direction1.getStepZ(), 0.0, 0.0, 0.0);
   }
}
