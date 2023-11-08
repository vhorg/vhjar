package iskallia.vault.block;

import iskallia.vault.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class PillarBlock extends Block {
   public static final BooleanProperty ABOVE = BooleanProperty.create("above");
   public static final BooleanProperty BELOW = BooleanProperty.create("below");
   public static final EnumProperty<Axis> AXIS = BlockStateProperties.AXIS;

   public PillarBlock() {
      super(Properties.copy(ModBlocks.VAULT_STONE));
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(AXIS, Axis.Y));
   }

   public PillarBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(AXIS, Axis.Y));
   }

   public BlockState rotate(BlockState state, Rotation rotation) {
      BlockState swapped = (BlockState)((BlockState)state.setValue(ABOVE, !(Boolean)state.getValue(ABOVE))).setValue(BELOW, !(Boolean)state.getValue(BELOW));

      return switch (rotation) {
         case NONE -> state;
         case CLOCKWISE_90 -> {
            switch ((Axis)state.getValue(AXIS)) {
               case X:
                  yield (BlockState)swapped.setValue(AXIS, Axis.Z);
               case Y:
                  yield state;
               case Z:
                  yield (BlockState)state.setValue(AXIS, Axis.X);
               default:
                  throw new IncompatibleClassChangeError();
            }
         }
         case CLOCKWISE_180 -> {
            switch ((Axis)state.getValue(AXIS)) {
               case X:
               case Z:
                  yield swapped;
               case Y:
                  yield state;
               default:
                  throw new IncompatibleClassChangeError();
            }
         }
         case COUNTERCLOCKWISE_90 -> {
            switch ((Axis)state.getValue(AXIS)) {
               case X:
                  yield (BlockState)state.setValue(AXIS, Axis.Z);
               case Y:
                  yield state;
               case Z:
                  yield (BlockState)swapped.setValue(AXIS, Axis.X);
               default:
                  throw new IncompatibleClassChangeError();
            }
         }
         default -> throw new IncompatibleClassChangeError();
      };
   }

   public BlockState mirror(BlockState state, Mirror mirror) {
      BlockState swapped = (BlockState)((BlockState)state.setValue(ABOVE, !(Boolean)state.getValue(ABOVE))).setValue(BELOW, !(Boolean)state.getValue(BELOW));

      return switch (mirror) {
         case NONE -> state;
         case LEFT_RIGHT -> state.getValue(AXIS) == Axis.Z ? swapped : state;
         case FRONT_BACK -> state.getValue(AXIS) == Axis.X ? swapped : state;
         default -> throw new IncompatibleClassChangeError();
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
         case Y:
            above = world.getBlockState(pos.above());
            below = world.getBlockState(pos.below());
            break;
         case Z:
            above = world.getBlockState(pos.north());
            below = world.getBlockState(pos.south());
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
