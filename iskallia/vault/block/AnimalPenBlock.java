package iskallia.vault.block;

import iskallia.vault.block.entity.AnimalPenTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AnimalPenBlock extends HorizontalDirectionalBlock implements EntityBlock, SimpleWaterloggedBlock {
   public static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 10.0, 16.0);

   public AnimalPenBlock() {
      super(Properties.copy(Blocks.OAK_PLANKS).strength(1.0F).noOcclusion());
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.getStateDefinition().any()).setValue(FACING, Direction.NORTH))
            .setValue(BlockStateProperties.WATERLOGGED, false)
      );
   }

   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return SHAPE;
   }

   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      pBuilder.add(new Property[]{FACING, BlockStateProperties.WATERLOGGED});
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
      boolean flag = fluidState.getType() == Fluids.WATER;
      return (BlockState)((BlockState)super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection().getOpposite()))
         .setValue(BlockStateProperties.WATERLOGGED, flag);
   }

   @NotNull
   public BlockState updateShape(
      BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos
   ) {
      if (!pState.canSurvive(pLevel, pCurrentPos)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         if ((Boolean)pState.getValue(BlockStateProperties.WATERLOGGED)) {
            pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
         }

         return super.updateShape(pState, pDirection, pNeighborState, pLevel, pCurrentPos, pNeighborPos);
      }
   }

   @NotNull
   public FluidState getFluidState(BlockState pState) {
      return pState.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
   }

   @NotNull
   public InteractionResult use(
      @NotNull BlockState state,
      @NotNull Level level,
      @NotNull BlockPos pos,
      @NotNull Player player,
      @NotNull InteractionHand hand,
      @NotNull BlockHitResult hit
   ) {
      if (level.getBlockEntity(pos) instanceof AnimalPenTileEntity animalPenTileEntity) {
         return animalPenTileEntity.interact(state, level, player, hand, hit) ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
      } else {
         return InteractionResult.SUCCESS;
      }
   }

   public void attack(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
      if (!(pLevel.getBlockEntity(pPos) instanceof AnimalPenTileEntity animalPenTileEntity && animalPenTileEntity.attack(pState, pLevel, pPlayer))) {
         super.attack(pState, pLevel, pPos, pPlayer);
      }
   }

   public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
      if (!pState.is(pNewState.getBlock())) {
         if (pLevel.getBlockEntity(pPos) instanceof AnimalPenTileEntity animalPenTileEntity) {
            Containers.dropContents(pLevel, pPos, animalPenTileEntity.getInventory());
            pLevel.updateNeighbourForOutputSignal(pPos, this);
         }

         super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
      }
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return ModBlocks.ANIMAL_PEN_ENTITY.create(pos, state);
   }

   @javax.annotation.Nullable
   public <A extends BlockEntity> BlockEntityTicker<A> getTicker(Level world, BlockState state, BlockEntityType<A> type) {
      return BlockHelper.getTicker(type, ModBlocks.ANIMAL_PEN_ENTITY, AnimalPenTileEntity::tick);
   }

   public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
      return false;
   }
}
