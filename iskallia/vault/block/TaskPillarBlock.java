package iskallia.vault.block;

import iskallia.vault.block.entity.TaskPillarTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.BlockHelper;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TaskPillarBlock extends Block implements EntityBlock {
   private static final VoxelShape SHAPE = Shapes.box(0.0625, 0.0625, 0.0625, 0.9375, 0.9375, 0.9375);

   public TaskPillarBlock() {
      super(Properties.copy(Blocks.STONE).strength(-1.0F, 3600000.0F).noDrops());
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new TaskPillarTileEntity(pos, state);
   }

   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return SHAPE;
   }

   @Nullable
   public <A extends BlockEntity> BlockEntityTicker<A> getTicker(Level level, BlockState state, BlockEntityType<A> tBlockEntityType) {
      return level.isClientSide() ? null : BlockHelper.getTicker(tBlockEntityType, ModBlocks.TASK_PILLAR_TILE_ENTITY, TaskPillarTileEntity::serverTick);
   }
}
