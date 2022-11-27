package iskallia.vault.block;

import iskallia.vault.block.entity.CoinPilesTileEntity;
import iskallia.vault.init.ModSounds;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CoinPileBlock extends Block implements EntityBlock {
   public static final IntegerProperty SIZE = IntegerProperty.create("size", 1, 7);
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape[] SHAPES = new VoxelShape[]{
      Block.box(5.0, 0.0, 5.0, 11.0, 4.0, 11.0),
      Block.box(3.0, 0.0, 3.0, 12.0, 4.0, 12.0),
      Block.box(3.0, 0.0, 3.0, 13.0, 6.0, 13.0),
      Block.box(2.0, 0.0, 2.0, 14.0, 7.0, 14.0),
      Block.box(2.0, 0.0, 2.0, 14.0, 7.0, 14.0),
      Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
      Block.box(0.0, 0.0, 0.0, 16.0, 9.0, 16.0)
   };

   public CoinPileBlock() {
      super(Properties.of(Material.METAL).sound(ModSounds.COIN_PILE_SOUND_TYPE).noOcclusion().strength(4.6F, 3.0F));
   }

   public PushReaction getPistonPushReaction(BlockState pState) {
      return PushReaction.DESTROY;
   }

   public List<ItemStack> getDrops(BlockState pState, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
      return Collections.emptyList();
   }

   public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
      int size = (Integer)state.getValue(SIZE);
      if (!player.isCreative()) {
         this.playerWillDestroy(level, pos, state, player);
         this.generateLoot(level, pos, player);
         if (size != 1) {
            level.setBlock(pos, (BlockState)state.setValue(SIZE, size - 1), level.isClientSide ? 11 : 3);
         } else {
            level.setBlock(pos, fluid.createLegacyBlock(), level.isClientSide ? 11 : 3);
         }

         return true;
      } else {
         return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
      }
   }

   private void generateLoot(Level level, BlockPos pos, Player player) {
      if (level.getBlockEntity(pos) instanceof CoinPilesTileEntity te) {
         if (player instanceof ServerPlayer serverPlayer) {
            List<ItemStack> loot = te.generateLoot(serverPlayer);
            if (level instanceof ServerLevel) {
               loot.forEach(stack -> popResource(level, pos, stack));
            }
         }
      }
   }

   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return SHAPES[pState.getValue(SIZE) - 1];
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      BlockState blockstate = pContext.getLevel().getBlockState(pContext.getClickedPos());
      if (blockstate.is(this)) {
         return (BlockState)blockstate.setValue(SIZE, Math.min(7, (Integer)blockstate.getValue(SIZE) + 1));
      } else {
         FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());
         boolean flag = fluidstate.getType() == Fluids.WATER;
         return (BlockState)Objects.requireNonNull(super.getStateForPlacement(pContext)).setValue(WATERLOGGED, flag);
      }
   }

   public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
      if (!pState.canSurvive(pLevel, pCurrentPos)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         if ((Boolean)pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
         }

         return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
      }
   }

   public boolean canBeReplaced(BlockState state, BlockPlaceContext ctx) {
      return !ctx.isSecondaryUseActive() && ctx.getItemInHand().is(this.asItem()) && (Integer)state.getValue(SIZE) < 7 || super.canBeReplaced(state, ctx);
   }

   public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{SIZE, WATERLOGGED});
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new CoinPilesTileEntity(pos, state);
   }

   public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!state.is(newState.getBlock())) {
         BlockEntity blockentity = world.getBlockEntity(pos);
         if (blockentity instanceof Container) {
            Containers.dropContents(world, pos, (Container)blockentity);
            world.updateNeighbourForOutputSignal(pos, this);
         }

         super.onRemove(state, world, pos, newState, isMoving);
      }
   }
}
