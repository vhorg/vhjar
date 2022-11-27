package iskallia.vault.block;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;

public class CrystalBuddingBlock extends Block {
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

   @ParametersAreNonnullByDefault
   public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random random) {
      if (random.nextFloat() <= ModConfigs.CRYSTAL_BUDDING.getGrowthChancePerRandomTick()) {
         Direction direction = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
         BlockPos blockpos = blockPos.relative(direction);
         BlockState blockstate = serverLevel.getBlockState(blockpos);
         Block block = null;
         if (this.canClusterGrowAtState(blockstate)) {
            block = ModBlocks.SMALL_CRYSTAL_BUD;
         } else if (blockstate.is(ModBlocks.SMALL_CRYSTAL_BUD) && blockstate.getValue(CrystalClusterBlock.FACING) == direction) {
            block = ModBlocks.MEDIUM_CRYSTAL_BUD;
         } else if (blockstate.is(ModBlocks.MEDIUM_CRYSTAL_BUD) && blockstate.getValue(CrystalClusterBlock.FACING) == direction) {
            block = ModBlocks.LARGE_CRYSTAL_BUD;
         } else if (blockstate.is(ModBlocks.LARGE_CRYSTAL_BUD) && blockstate.getValue(CrystalClusterBlock.FACING) == direction) {
            block = ModBlocks.CRYSTAL_CLUSTER;
         }

         if (block != null) {
            serverLevel.setBlockAndUpdate(
               blockpos,
               (BlockState)((BlockState)block.defaultBlockState()
                     .setValue(CrystalClusterBlock.WATERLOGGED, blockstate.getFluidState().getType() == Fluids.WATER))
                  .setValue(CrystalClusterBlock.FACING, direction)
            );
         }
      }
   }

   private boolean canClusterGrowAtState(BlockState blockState) {
      return blockState.isAir() || blockState.is(Blocks.WATER) && blockState.getFluidState().getAmount() == 8;
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
}
