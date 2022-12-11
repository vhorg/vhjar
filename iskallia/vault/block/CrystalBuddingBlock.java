package iskallia.vault.block;

import iskallia.vault.block.entity.CrystalBuddingBlockEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.BlockHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class CrystalBuddingBlock extends Block implements EntityBlock {
   public static final BooleanProperty UNBREAKABLE = BooleanProperty.create("unbreakable");
   private static final Direction[] DIRECTIONS = Direction.values();

   public CrystalBuddingBlock() {
      super(Properties.of(Material.AMETHYST).randomTicks().sound(SoundType.AMETHYST).requiresCorrectToolForDrops().strength(50.0F, 1200.0F));
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(UNBREAKABLE, false));
   }

   @Nonnull
   public PushReaction getPistonPushReaction(@Nonnull BlockState blockState) {
      return PushReaction.BLOCK;
   }

   @Nonnull
   @ParametersAreNonnullByDefault
   public InteractionResult use(
      BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult
   ) {
      if (level instanceof ServerLevel serverLevel && interactionHand == InteractionHand.MAIN_HAND && player.isCreative()) {
         if (player.getMainHandItem().getItem() == Items.STICK) {
            serverLevel.setBlockAndUpdate(blockPos, (BlockState)blockState.setValue(UNBREAKABLE, !(Boolean)blockState.getValue(UNBREAKABLE)));
            return InteractionResult.SUCCESS;
         }

         if (player.getMainHandItem().getItem() == Items.BONE_MEAL) {
            this.growRandom(serverLevel, blockPos, serverLevel.random);
            return InteractionResult.SUCCESS;
         }
      }

      return super.use(blockState, level, blockPos, player, interactionHand, blockHitResult);
   }

   @ParametersAreNonnullByDefault
   public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos changedBlockPos, boolean pIsMoving) {
      super.neighborChanged(blockState, level, blockPos, block, changedBlockPos, pIsMoving);
      this.checkAndUpdateMissingBlockEntity(level, blockPos);
   }

   @ParametersAreNonnullByDefault
   public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random random) {
      this.checkAndUpdateMissingBlockEntity(serverLevel, blockPos);
   }

   public void checkAndScheduleTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random random) {
      if (!serverLevel.getBlockTicks().hasScheduledTick(blockPos, this)) {
         this.tick(blockState, serverLevel, blockPos, random);
      }
   }

   @ParametersAreNonnullByDefault
   public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random random) {
      this.growRandom(serverLevel, blockPos, random);
      serverLevel.scheduleTick(blockPos, this, this.getNextUpdateIntervalTicks(random));
   }

   private int getNextUpdateIntervalTicks(Random random) {
      float max = ModConfigs.CRYSTAL_BUDDING.getMaxSecondsBetweenGrowthUpdates();
      float min = ModConfigs.CRYSTAL_BUDDING.getMinSecondsBetweenGrowthUpdates();
      return (int)(Math.max(1.0F, random.nextFloat() * (max - min) + min) * 20.0F);
   }

   public void growRandom(ServerLevel serverLevel, BlockPos blockPos, Random random) {
      Direction validGrowthDirection = this.getValidGrowthDirection(serverLevel, blockPos, random);
      if (validGrowthDirection != null) {
         BlockPos relativeBlockPos = blockPos.relative(validGrowthDirection);
         BlockState relativeBlockState = serverLevel.getBlockState(relativeBlockPos);
         Block block = null;
         if (this.canClusterGrowBeginAtState(relativeBlockState)) {
            block = ModBlocks.SMALL_CRYSTAL_BUD;
         } else if (relativeBlockState.is(ModBlocks.SMALL_CRYSTAL_BUD) && relativeBlockState.getValue(CrystalClusterBlock.FACING) == validGrowthDirection) {
            block = ModBlocks.MEDIUM_CRYSTAL_BUD;
         } else if (relativeBlockState.is(ModBlocks.MEDIUM_CRYSTAL_BUD) && relativeBlockState.getValue(CrystalClusterBlock.FACING) == validGrowthDirection) {
            block = ModBlocks.LARGE_CRYSTAL_BUD;
         } else if (relativeBlockState.is(ModBlocks.LARGE_CRYSTAL_BUD) && relativeBlockState.getValue(CrystalClusterBlock.FACING) == validGrowthDirection) {
            block = ModBlocks.CRYSTAL_CLUSTER;
         }

         if (block != null) {
            serverLevel.setBlockAndUpdate(
               relativeBlockPos,
               (BlockState)((BlockState)block.defaultBlockState()
                     .setValue(CrystalClusterBlock.WATERLOGGED, relativeBlockState.getFluidState().getType() == Fluids.WATER))
                  .setValue(CrystalClusterBlock.FACING, validGrowthDirection)
            );
         }
      }
   }

   @Nullable
   private Direction getValidGrowthDirection(ServerLevel serverLevel, BlockPos blockPos, Random random) {
      List<Direction> validGrowthDirections = this.getValidGrowthDirections(serverLevel, blockPos);
      return validGrowthDirections.size() < 1 ? null : validGrowthDirections.get(random.nextInt(validGrowthDirections.size()));
   }

   private List<Direction> getValidGrowthDirections(ServerLevel serverLevel, BlockPos blockPos) {
      List<Direction> result = new ArrayList<>(DIRECTIONS.length);

      for (Direction direction : DIRECTIONS) {
         BlockPos relativeBlockPos = blockPos.relative(direction);
         BlockState relativeBlockState = serverLevel.getBlockState(relativeBlockPos);
         if (this.canClusterGrowBeginAtState(relativeBlockState) || this.canClusterGrowContinueAtState(relativeBlockState, direction)) {
            for (int i = 0; i < this.getGrowthWeight(relativeBlockState); i++) {
               result.add(direction);
            }
         }
      }

      return result;
   }

   private boolean canClusterGrowBeginAtState(BlockState blockState) {
      return blockState.isAir() || blockState.is(Blocks.WATER) && blockState.getFluidState().getAmount() == 8;
   }

   private boolean canClusterGrowContinueAtState(BlockState blockState, Direction direction) {
      return (blockState.is(ModBlocks.SMALL_CRYSTAL_BUD) || blockState.is(ModBlocks.MEDIUM_CRYSTAL_BUD) || blockState.is(ModBlocks.LARGE_CRYSTAL_BUD))
         && blockState.getValue(CrystalClusterBlock.FACING) == direction;
   }

   private int getGrowthWeight(BlockState blockState) {
      if (blockState.is(ModBlocks.LARGE_CRYSTAL_BUD)) {
         return 8;
      } else if (blockState.is(ModBlocks.MEDIUM_CRYSTAL_BUD)) {
         return 4;
      } else {
         return blockState.is(ModBlocks.SMALL_CRYSTAL_BUD) ? 2 : 1;
      }
   }

   private void checkAndUpdateMissingBlockEntity(Level level, BlockPos blockPos) {
      level.getBlockEntity(blockPos);
   }

   protected void createBlockStateDefinition(@Nonnull Builder<Block, BlockState> builder) {
      builder.add(new Property[]{UNBREAKABLE});
   }

   @ParametersAreNonnullByDefault
   public float getDestroyProgress(BlockState blockState, Player player, BlockGetter blockGetter, BlockPos blockPos) {
      return blockState.getValue(UNBREAKABLE) ? 0.0F : super.getDestroyProgress(blockState, player, blockGetter, blockPos);
   }

   public float getExplosionResistance(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Explosion explosion) {
      return blockState.getValue(UNBREAKABLE) ? 3600000.0F : super.getExplosionResistance(blockState, blockGetter, blockPos, explosion);
   }

   @ParametersAreNonnullByDefault
   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
      return level.isClientSide ? null : BlockHelper.getTicker(blockEntityType, ModBlocks.CRYSTAL_BUDDING_TILE_ENTITY, CrystalBuddingBlockEntity::serverTick);
   }

   @ParametersAreNonnullByDefault
   @Nullable
   public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return ModBlocks.CRYSTAL_BUDDING_TILE_ENTITY.create(blockPos, blockState);
   }
}
