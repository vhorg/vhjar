package iskallia.vault.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class OrnateChainBlock extends ChainBlock {
   protected static final VoxelShape Y_AXIS_AABB = Block.box(4.5, 0.0, 4.5, 11.5, 16.0, 11.5);
   protected static final VoxelShape Z_AXIS_AABB = Block.box(4.5, 4.5, 0.0, 11.5, 11.5, 16.0);
   protected static final VoxelShape X_AXIS_AABB = Block.box(0.0, 4.5, 4.5, 16.0, 11.5, 11.5);
   private static final VoxelShape COLLISION_SHAPE = Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);

   public OrnateChainBlock() {
      super(Properties.copy(Blocks.CHAIN));
   }

   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      switch ((Axis)pState.getValue(AXIS)) {
         case X:
         default:
            return X_AXIS_AABB;
         case Z:
            return Z_AXIS_AABB;
         case Y:
            return Y_AXIS_AABB;
      }
   }

   public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return state.getValue(AXIS) == Axis.Y ? COLLISION_SHAPE : super.getCollisionShape(state, worldIn, pos, context);
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      FluidState ifluidstate = context.getLevel().getFluidState(context.getClickedPos());
      boolean flag = ifluidstate.is(FluidTags.WATER) && ifluidstate.getAmount() == 8;
      return (BlockState)super.getStateForPlacement(context).setValue(WATERLOGGED, flag);
   }

   public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
      return !(Boolean)state.getValue(WATERLOGGED);
   }

   public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }
}
