package iskallia.vault.block;

import iskallia.vault.entity.entity.FallingSootEntity;
import java.util.Arrays;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SootLayerBlock extends FallingBlock {
   private static final int MAX_LAYERS = 8;
   public static final IntegerProperty LAYERS = BlockStateProperties.LAYERS;
   protected static final VoxelShape[] SHAPE_BY_LAYER = new VoxelShape[9];

   public SootLayerBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(LAYERS, 1));
   }

   public int getDustColor(BlockState state, BlockGetter reader, BlockPos pos) {
      return 3223083;
   }

   public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
      if (state.getBlock() != oldState.getBlock()) {
         worldIn.scheduleTick(pos, this, this.getDelayAfterPlace());
      }
   }

   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return SHAPE_BY_LAYER[pState.getValue(LAYERS)];
   }

   public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      if (pContext instanceof EntityCollisionContext c) {
         Entity e = c.getEntity();
         if (e instanceof LivingEntity) {
            return SHAPE_BY_LAYER[pState.getValue(LAYERS) - 1];
         }
      }

      return this.getShape(pState, pLevel, pPos, pContext);
   }

   public VoxelShape getBlockSupportShape(BlockState pState, BlockGetter pReader, BlockPos pPos) {
      return SHAPE_BY_LAYER[pState.getValue(LAYERS)];
   }

   public VoxelShape getVisualShape(BlockState pState, BlockGetter pReader, BlockPos pPos, CollisionContext pContext) {
      return SHAPE_BY_LAYER[pState.getValue(LAYERS)];
   }

   public boolean isPathfindable(BlockState state, BlockGetter blockGetter, BlockPos pos, PathComputationType pathType) {
      return pathType == PathComputationType.LAND ? (Integer)state.getValue(LAYERS) <= 4 : false;
   }

   public boolean useShapeForLightOcclusion(BlockState state) {
      return true;
   }

   public BlockState updateShape(BlockState state, Direction direction, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos otherPos) {
      if (world instanceof ServerLevel serverLevel) {
         BlockPos pos = currentPos.above();

         for (BlockState state1 = world.getBlockState(pos); state1.is(this); state1 = serverLevel.getBlockState(pos)) {
            serverLevel.scheduleTick(pos, this, this.getDelayAfterPlace());
            pos = pos.above();
         }
      }

      return super.updateShape(state, direction, facingState, world, currentPos, otherPos);
   }

   public void tick(BlockState state, ServerLevel level, BlockPos pos, Random pRand) {
      BlockState below = level.getBlockState(pos.below());
      if ((FallingSootEntity.isSpaceBelow(below) || this.hasIncompleteSootBelow(below)) && pos.getY() >= level.getMinBuildHeight()) {
         while (state.is(this)) {
            FallingSootEntity fallingblockentity = FallingSootEntity.fall(level, pos, state);
            this.falling(fallingblockentity);
            pos = pos.above();
            state = level.getBlockState(pos);
         }
      }
   }

   private boolean hasIncompleteSootBelow(BlockState state) {
      return state.is(this) && (Integer)state.getValue(LAYERS) != 8;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos());
      if (blockstate.is(this)) {
         int i = (Integer)blockstate.getValue(LAYERS);
         return (BlockState)blockstate.setValue(LAYERS, Math.min(8, i + 1));
      } else {
         return super.getStateForPlacement(context);
      }
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{LAYERS});
   }

   public boolean canBeReplaced(BlockState pState, BlockPlaceContext pUseContext) {
      int i = (Integer)pState.getValue(LAYERS);
      return pUseContext.getItemInHand().is(this.asItem()) && i < 8 ? true : i == 1;
   }

   public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float height) {
      int layers = (Integer)state.getValue(LAYERS);
      entity.causeFallDamage(height, layers > 2 ? 0.3F : 1.0F, DamageSource.FALL);
   }

   static {
      Arrays.setAll(SHAPE_BY_LAYER, l -> Block.box(0.0, 0.0, 0.0, 16.0, l * 2, 16.0));
   }
}
