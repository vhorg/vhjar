package iskallia.vault.block;

import iskallia.vault.block.entity.DebagnetizerTileEntity;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;

public class DebagnetizerBlock extends Block implements EntityBlock {
   private static final int RANGE = 32;
   public static final BooleanProperty DEACTIVATED = BooleanProperty.create("deactivated");

   public DebagnetizerBlock() {
      super(Properties.copy(Blocks.IRON_BLOCK).noOcclusion());
   }

   public boolean isInRange(Level world, Vec3 origin) {
      ChunkPos min = new ChunkPos((int)(origin.x - 32.0) >> 4, (int)(origin.z - 32.0) >> 4);
      ChunkPos max = new ChunkPos((int)(origin.x + 32.0) >> 4, (int)(origin.z + 32.0) >> 4);

      for (int x = min.x; x <= max.x; x++) {
         for (int z = min.z; z <= max.z; z++) {
            LevelChunk chunk = world.getChunkSource().getChunkNow(x, z);
            if (chunk != null) {
               for (BlockEntity entity : chunk.getBlockEntities().values()) {
                  if (entity instanceof DebagnetizerTileEntity debagnetizer) {
                     double dx = origin.x - debagnetizer.getBlockPos().getX();
                     double dz = origin.z - debagnetizer.getBlockPos().getZ();
                     double distanceSq = dx * dx + dz * dz;
                     if (distanceSq <= 1024.0) {
                        return true;
                     }
                  }
               }
            }
         }
      }

      return false;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{DEACTIVATED});
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)super.defaultBlockState().setValue(DEACTIVATED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
   }

   public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
      if (!world.isClientSide()) {
         world.scheduleTick(pos, this, 1);
      }
   }

   public void tick(BlockState state, ServerLevel world, BlockPos pos, Random random) {
      if ((Boolean)state.getValue(DEACTIVATED) != world.hasNeighborSignal(pos)) {
         world.setBlock(pos, (BlockState)state.cycle(DEACTIVATED), 2);
      }
   }

   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new DebagnetizerTileEntity(pos, state);
   }
}
