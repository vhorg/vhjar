package iskallia.vault.block;

import iskallia.vault.block.entity.challenge.ChallengeControllerBlockEntity;
import iskallia.vault.util.BlockHelper;
import iskallia.vault.util.VoxelUtils;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ChallengeControllerBlock extends Block implements EntityBlock {
   public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
   public static final DirectionProperty FACING = BlockStateProperties.FACING;
   private static final VoxelShape SHAPE_TOP = makeShape().move(0.0, -1.0, 0.0);
   private static final VoxelShape SHAPE_BOTTOM = makeShape();
   private final Supplier<BlockEntityType<? extends ChallengeControllerBlockEntity<?>>> entityType;

   public ChallengeControllerBlock(Supplier<BlockEntityType<? extends ChallengeControllerBlockEntity<?>>> entityType) {
      super(Properties.of(Material.GLASS).sound(SoundType.GLASS).strength(-1.0F, 3600000.0F).lightLevel(value -> 10).noCollission().noOcclusion().noDrops());
      this.entityType = entityType;
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(HALF, DoubleBlockHalf.LOWER)).setValue(FACING, Direction.NORTH)
      );
   }

   private static VoxelShape makeShape() {
      VoxelShape m1 = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
      VoxelShape m2 = Block.box(2.0, 2.0, 2.0, 14.0, 29.0, 14.0);
      return VoxelUtils.combineAll(BooleanOp.OR, m1, m2);
   }

   public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return state.getValue(HALF) == DoubleBlockHalf.UPPER ? SHAPE_TOP : SHAPE_BOTTOM;
   }

   public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
      super.onRemove(state, world, pos, newState, isMoving);
      if (!state.is(newState.getBlock())) {
         if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockState otherState = world.getBlockState(pos.below());
            if (otherState.is(state.getBlock())) {
               world.removeBlock(pos.below(), isMoving);
            }
         } else {
            BlockState otherState = world.getBlockState(pos.above());
            if (otherState.is(state.getBlock())) {
               world.removeBlock(pos.above(), isMoving);
            }
         }
      }
   }

   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return state.getValue(HALF) == DoubleBlockHalf.LOWER ? this.entityType.get().create(pos, state) : null;
   }

   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
      return BlockHelper.getTicker(pBlockEntityType, this.entityType.get(), ChallengeControllerBlockEntity::tick);
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{HALF, FACING});
   }

   public BlockState rotate(BlockState state, Rotation rotation) {
      return (BlockState)super.rotate(state, rotation).setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
   }

   public BlockState mirror(BlockState state, Mirror mirror) {
      return (BlockState)super.mirror(state, mirror).setValue(FACING, mirror.mirror((Direction)state.getValue(FACING)));
   }
}
