package iskallia.vault.block;

import iskallia.vault.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class PillarBlock extends Block {
   public static BooleanProperty ABOVE = BooleanProperty.create("above");
   public static BooleanProperty BELOW = BooleanProperty.create("below");
   public static final EnumProperty<Axis> AXIS = BlockStateProperties.AXIS;

   public PillarBlock() {
      super(Properties.copy(ModBlocks.VAULT_STONE));
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(AXIS, Axis.Y));
   }

   public PillarBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(AXIS, Axis.Y));
   }

   public BlockState rotate(BlockState pState, Rotation pRot) {
      return rotatePillar(pState, pRot);
   }

   public static BlockState rotatePillar(BlockState pState, Rotation pRotation) {
      return switch (pRotation) {
         case COUNTERCLOCKWISE_90, CLOCKWISE_90 -> {
            switch ((Axis)pState.getValue(AXIS)) {
               case X:
                  yield (BlockState)pState.setValue(AXIS, Axis.Z);
               case Z:
                  yield (BlockState)pState.setValue(AXIS, Axis.X);
               default:
                  yield pState;
            }
         }
         default -> pState;
      };
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      pBuilder.add(new Property[]{AXIS, ABOVE, BELOW});
   }

   public BlockState updateShape(
      BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos
   ) {
      return pState.hasProperty(AXIS)
         ? this.updateConnecting(pLevel, pCurrentPos, pState, (Axis)pState.getValue(AXIS))
         : super.updateShape(pState, pDirection, pNeighborState, pLevel, pCurrentPos, pNeighborPos);
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockGetter iblockreader = context.getLevel();
      BlockPos blockpos = context.getClickedPos();
      return this.updateConnecting(iblockreader, blockpos, super.getStateForPlacement(context), context.getClickedFace().getAxis());
   }

   protected BlockState updateConnecting(BlockGetter world, BlockPos pos, BlockState state, Axis axis) {
      BlockState above;
      BlockState below;
      switch (axis) {
         case Z:
            above = world.getBlockState(pos.north());
            below = world.getBlockState(pos.south());
            break;
         case Y:
            above = world.getBlockState(pos.above());
            below = world.getBlockState(pos.below());
            break;
         default:
            above = world.getBlockState(pos.east());
            below = world.getBlockState(pos.west());
      }

      return (BlockState)((BlockState)((BlockState)state.setValue(ABOVE, above.getBlock() == this && above.hasProperty(AXIS) && above.getValue(AXIS) == axis))
            .setValue(BELOW, below.getBlock() == this && below.hasProperty(AXIS) && below.getValue(AXIS) == axis))
         .setValue(AXIS, axis);
   }
}
