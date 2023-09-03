package iskallia.vault.block;

import iskallia.vault.block.entity.AlchemyTableTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.BlockHelper;
import iskallia.vault.util.VoxelUtils;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
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
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class AlchemyTableBlock extends HorizontalDirectionalBlock implements EntityBlock, SimpleWaterloggedBlock {
   public static final VoxelShape SHAPE = Block.box(0.0, 5.0, 0.0, 16.0, 8.0, 16.0);
   public static final VoxelShape SHAPE2 = Block.box(1.0, 0.0, 1.0, 15.0, 5.0, 15.0);

   public AlchemyTableBlock() {
      super(Properties.of(Material.STONE).sound(SoundType.STONE).strength(0.5F).noOcclusion());
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.getStateDefinition().any()).setValue(FACING, Direction.NORTH))
            .setValue(BlockStateProperties.WATERLOGGED, false)
      );
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

   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return VoxelUtils.combineAll(BooleanOp.OR, SHAPE, SHAPE2);
   }

   @Nonnull
   public InteractionResult use(@Nonnull BlockState state, Level level, @Nonnull BlockPos pos, @Nonnull Player player, InteractionHand hand, BlockHitResult hit) {
      if (level.isClientSide()) {
         return InteractionResult.SUCCESS;
      } else if (player instanceof ServerPlayer sPlayer) {
         if (level.getBlockEntity(pos) instanceof AlchemyTableTileEntity workbench) {
            NetworkHooks.openGui(sPlayer, workbench, buffer -> buffer.writeBlockPos(pos));
            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.SUCCESS;
         }
      } else {
         return InteractionResult.SUCCESS;
      }
   }

   @Nullable
   public <A extends BlockEntity> BlockEntityTicker<A> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<A> type) {
      return BlockHelper.getTicker(
         type, ModBlocks.ALCHEMY_TABLE_TILE_ENTITY, (level1, blockPos, blockState, alchemyTableTile) -> alchemyTableTile.tick(level1, blockPos, blockState)
      );
   }

   public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
      return false;
   }

   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof AlchemyTableTileEntity workbench) {
         Containers.dropContents(level, pos, workbench.getInventory());
         workbench.getInventory().clearContent();
         level.updateNeighbourForOutputSignal(pos, this);
      }

      super.onRemove(state, level, pos, newState, isMoving);
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return ModBlocks.ALCHEMY_TABLE_TILE_ENTITY.create(pos, state);
   }
}
