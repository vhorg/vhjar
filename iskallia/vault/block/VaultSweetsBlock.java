package iskallia.vault.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VaultSweetsBlock extends Block {
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final IntegerProperty PIECES = IntegerProperty.create("pieces", 1, 4);
   protected static final VoxelShape CAKE_SHAPE_PIECE = Block.box(0.0, 0.0, 0.0, 8.0, 16.0, 8.0);

   public VaultSweetsBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(PIECES, 4));
   }

   public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
      super.onPlace(pState, pLevel, pPos, pOldState, pIsMoving);
      pLevel.playSound(null, pPos, SoundEvents.HONEY_BLOCK_PLACE, SoundSource.BLOCKS, 0.5F, 1.0F);
   }

   public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
      super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
      pLevel.playSound(null, pPos, SoundEvents.HONEY_BLOCK_PLACE, SoundSource.BLOCKS, 0.5F, 1.0F);
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      if (state.getValue(FACING) == Direction.NORTH) {
         switch (state.getValue(PIECES)) {
            case 1:
               return CAKE_SHAPE_PIECE.move(0.0, 0.0, 0.5);
            case 2:
               return Shapes.joinUnoptimized(CAKE_SHAPE_PIECE.move(0.0, 0.0, 0.5), CAKE_SHAPE_PIECE.move(0.5, 0.0, 0.5), BooleanOp.OR);
            case 3:
               return Shapes.joinUnoptimized(
                  Shapes.joinUnoptimized(CAKE_SHAPE_PIECE.move(0.0, 0.0, 0.5), CAKE_SHAPE_PIECE.move(0.5, 0.0, 0.0), BooleanOp.OR),
                  CAKE_SHAPE_PIECE.move(0.5, 0.0, 0.5),
                  BooleanOp.OR
               );
            case 4:
               return Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
            default:
               throw new IllegalStateException("Unexpected value: " + state.getValue(PIECES));
         }
      } else if (state.getValue(FACING) == Direction.SOUTH) {
         switch (state.getValue(PIECES)) {
            case 1:
               return CAKE_SHAPE_PIECE.move(0.5, 0.0, 0.0);
            case 2:
               return Shapes.joinUnoptimized(CAKE_SHAPE_PIECE, CAKE_SHAPE_PIECE.move(0.5, 0.0, 0.0), BooleanOp.OR);
            case 3:
               return Shapes.joinUnoptimized(
                  Shapes.joinUnoptimized(CAKE_SHAPE_PIECE, CAKE_SHAPE_PIECE.move(0.5, 0.0, 0.0), BooleanOp.OR),
                  CAKE_SHAPE_PIECE.move(0.0, 0.0, 0.5),
                  BooleanOp.OR
               );
            case 4:
               return Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
            default:
               throw new IllegalStateException("Unexpected value: " + state.getValue(PIECES));
         }
      } else if (state.getValue(FACING) == Direction.EAST) {
         switch (state.getValue(PIECES)) {
            case 1:
               return CAKE_SHAPE_PIECE;
            case 2:
               return Shapes.joinUnoptimized(CAKE_SHAPE_PIECE, CAKE_SHAPE_PIECE.move(0.0, 0.0, 0.5), BooleanOp.OR);
            case 3:
               return Shapes.joinUnoptimized(
                  Shapes.joinUnoptimized(CAKE_SHAPE_PIECE, CAKE_SHAPE_PIECE.move(0.5, 0.0, 0.5), BooleanOp.OR),
                  CAKE_SHAPE_PIECE.move(0.0, 0.0, 0.5),
                  BooleanOp.OR
               );
            case 4:
               return Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
            default:
               throw new IllegalStateException("Unexpected value: " + state.getValue(PIECES));
         }
      } else if (state.getValue(FACING) == Direction.WEST) {
         switch (state.getValue(PIECES)) {
            case 1:
               return CAKE_SHAPE_PIECE.move(0.5, 0.0, 0.5);
            case 2:
               return Shapes.joinUnoptimized(CAKE_SHAPE_PIECE.move(0.5, 0.0, 0.5), CAKE_SHAPE_PIECE.move(0.5, 0.0, 0.0), BooleanOp.OR);
            case 3:
               return Shapes.joinUnoptimized(
                  Shapes.joinUnoptimized(CAKE_SHAPE_PIECE.move(0.5, 0.0, 0.5), CAKE_SHAPE_PIECE.move(0.0, 0.0, 0.0), BooleanOp.OR),
                  CAKE_SHAPE_PIECE.move(0.5, 0.0, 0.0),
                  BooleanOp.OR
               );
            case 4:
               return Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
            default:
               throw new IllegalStateException("Unexpected value: " + state.getValue(PIECES));
         }
      } else {
         return Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
      }
   }

   public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      return level.isClientSide && this.takeServing(level, pos, state, player, hand).consumesAction()
         ? InteractionResult.SUCCESS
         : this.takeServing(level, pos, state, player, hand);
   }

   protected InteractionResult takeServing(LevelAccessor level, BlockPos pos, BlockState state, Player player, InteractionHand hand) {
      int servings = (Integer)state.getValue(PIECES);
      if (!player.canEat(false)) {
         return InteractionResult.PASS;
      } else if (servings > 0) {
         player.getFoodData().eat(10, 10.0F);
         if (servings > 1) {
            level.setBlock(pos, (BlockState)state.setValue(PIECES, servings - 1), 3);
         } else {
            level.removeBlock(pos, false);
         }

         level.playSound(null, pos, this.getSoundType(state).getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
         level.playSound(null, pos, SoundEvents.HONEY_BLOCK_PLACE, SoundSource.BLOCKS, 0.5F, 1.0F);
         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.PASS;
      }
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
   }

   public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
      return facing == Direction.DOWN && !stateIn.canSurvive(level, currentPos)
         ? Blocks.AIR.defaultBlockState()
         : super.updateShape(stateIn, facing, facingState, level, currentPos, facingPos);
   }

   public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
      return level.getBlockState(pos.below()).getMaterial().isSolid();
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, PIECES});
   }

   public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
      return (Integer)state.getValue(PIECES) == 4;
   }
}
