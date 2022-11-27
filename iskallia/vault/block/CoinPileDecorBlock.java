package iskallia.vault.block;

import iskallia.vault.init.ModSounds;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

public class CoinPileDecorBlock extends Block {
   public static final IntegerProperty SIZE = IntegerProperty.create("size", 1, 13);
   public static final IntegerProperty COINS = IntegerProperty.create("coins", 1, 64);
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape[] SHAPES = new VoxelShape[]{
      Block.box(4.0, 0.0, 4.0, 12.0, 2.0, 12.0),
      Block.box(4.0, 0.0, 4.0, 12.0, 3.0, 12.0),
      Block.box(4.0, 0.0, 4.0, 12.0, 3.0, 12.0),
      Block.box(4.0, 0.0, 4.0, 12.0, 3.0, 12.0),
      Block.box(3.0, 0.0, 3.0, 13.0, 3.0, 13.0),
      Block.box(3.0, 0.0, 3.0, 13.0, 4.0, 13.0),
      Block.box(1.0, 0.0, 1.0, 15.0, 6.0, 15.0),
      Block.box(1.0, 0.0, 1.0, 15.0, 8.0, 15.0),
      Block.box(1.0, 0.0, 1.0, 15.0, 8.0, 15.0),
      Block.box(0.0, 0.0, 0.0, 16.0, 11.0, 16.0),
      Block.box(0.0, 0.0, 0.0, 16.0, 11.0, 16.0),
      Block.box(0.0, 0.0, 0.0, 16.0, 11.0, 16.0),
      Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0)
   };

   public CoinPileDecorBlock() {
      super(Properties.of(Material.METAL).sound(ModSounds.COIN_PILE_SOUND_TYPE).noOcclusion().strength(0.2F, 3.0F));
   }

   public PushReaction getPistonPushReaction(BlockState pState) {
      return PushReaction.DESTROY;
   }

   public List<ItemStack> getDrops(BlockState pState, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
      return Collections.emptyList();
   }

   public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
      if (!player.isCreative()) {
         this.generateLoot(level, pos, state);
      }

      return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
   }

   private void generateLoot(Level level, BlockPos pos, BlockState state) {
      int numOfCoins = (Integer)state.getValue(COINS);
      popResource(level, pos, new ItemStack(state.getBlock().asItem(), numOfCoins));
   }

   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return SHAPES[pState.getValue(SIZE) - 1];
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      BlockState blockstate = pContext.getLevel().getBlockState(pContext.getClickedPos());
      if (blockstate.is(this)) {
         int existingCoins = (Integer)blockstate.getValue(COINS);
         ItemStack stack = pContext.getItemInHand();
         int stackSize = stack.getCount();
         int itemsTilFull = 64 - existingCoins;
         int newCoins = Math.min(64, (Integer)blockstate.getValue(COINS) + 1);

         int size = switch (newCoins) {
            case 2 -> 2;
            case 3 -> 3;
            case 4 -> 4;
            case 5, 6, 7 -> 5;
            case 8, 9, 10, 11, 12, 13, 14, 15 -> 6;
            case 16, 17, 18, 19, 20, 21, 22, 23 -> 7;
            case 24, 25, 26, 27, 28, 29, 30, 31 -> 8;
            case 32, 33, 34, 35, 36, 37, 38, 39 -> 9;
            case 40, 41, 42, 43, 44, 45, 46, 47 -> 10;
            case 48, 49, 50, 51, 52, 53, 54, 55 -> 11;
            case 56, 57, 58, 59, 60, 61, 62, 63 -> 12;
            case 64 -> 13;
            default -> 1;
         };
         return (BlockState)((BlockState)blockstate.setValue(SIZE, size)).setValue(COINS, Math.min(64, (Integer)blockstate.getValue(COINS) + 1));
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
      return !ctx.isSecondaryUseActive() && ctx.getItemInHand().is(this.asItem()) && (Integer)state.getValue(COINS) < 64 || super.canBeReplaced(state, ctx);
   }

   public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{SIZE, COINS, WATERLOGGED});
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
