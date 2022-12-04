package iskallia.vault.block;

import iskallia.vault.VaultMod;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.crystal.theme.ValueCrystalTheme;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CrystalClusterBlock extends Block implements SimpleWaterloggedBlock {
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final DirectionProperty FACING = BlockStateProperties.FACING;
   protected final VoxelShape northAabb;
   protected final VoxelShape southAabb;
   protected final VoxelShape eastAabb;
   protected final VoxelShape westAabb;
   protected final VoxelShape upAabb;
   protected final VoxelShape downAabb;

   public CrystalClusterBlock(SoundType soundType, int lightLevel, int height, double width) {
      super(Properties.of(Material.AMETHYST).noOcclusion().randomTicks().sound(soundType).strength(1.5F).lightLevel(value -> lightLevel));
      this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue(WATERLOGGED, false)).setValue(FACING, Direction.UP));
      this.upAabb = Block.box(width, 0.0, width, 16.0 - width, height, 16.0 - width);
      this.downAabb = Block.box(width, 16 - height, width, 16.0 - width, 16.0, 16.0 - width);
      this.northAabb = Block.box(width, width, 16 - height, 16.0 - width, 16.0 - width, 16.0);
      this.southAabb = Block.box(width, width, 0.0, 16.0 - width, 16.0 - width, height);
      this.eastAabb = Block.box(0.0, width, width, height, 16.0 - width, 16.0 - width);
      this.westAabb = Block.box(16 - height, width, width, 16.0, 16.0 - width, 16.0 - width);
   }

   @ParametersAreNonnullByDefault
   @Nonnull
   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      Direction direction = (Direction)blockState.getValue(FACING);

      return switch (direction) {
         case NORTH -> this.northAabb;
         case SOUTH -> this.southAabb;
         case EAST -> this.eastAabb;
         case WEST -> this.westAabb;
         case DOWN -> this.downAabb;
         case UP -> this.upAabb;
         default -> throw new IncompatibleClassChangeError();
      };
   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      Direction direction = (Direction)blockState.getValue(FACING);
      BlockPos relativeBlockPos = blockPos.relative(direction.getOpposite());
      return levelReader.getBlockState(relativeBlockPos).isFaceSturdy(levelReader, relativeBlockPos, direction);
   }

   @ParametersAreNonnullByDefault
   @Nonnull
   public BlockState updateShape(
      BlockState blockState, Direction direction, BlockState neighborState, LevelAccessor levelAccessor, BlockPos currentBlockPos, BlockPos neighborBlockPos
   ) {
      if ((Boolean)blockState.getValue(WATERLOGGED)) {
         levelAccessor.scheduleTick(currentBlockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
      }

      return direction == ((Direction)blockState.getValue(FACING)).getOpposite() && !blockState.canSurvive(levelAccessor, currentBlockPos)
         ? Blocks.AIR.defaultBlockState()
         : super.updateShape(blockState, direction, neighborState, levelAccessor, currentBlockPos, neighborBlockPos);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      return (BlockState)((BlockState)this.defaultBlockState()
            .setValue(WATERLOGGED, blockPlaceContext.getLevel().getFluidState(blockPlaceContext.getClickedPos()).getType() == Fluids.WATER))
         .setValue(FACING, blockPlaceContext.getClickedFace());
   }

   @Nonnull
   public BlockState rotate(BlockState blockState, Rotation rotation) {
      return (BlockState)blockState.setValue(FACING, rotation.rotate((Direction)blockState.getValue(FACING)));
   }

   @Nonnull
   public BlockState mirror(BlockState blockState, Mirror mirror) {
      return blockState.rotate(mirror.getRotation((Direction)blockState.getValue(FACING)));
   }

   @Nonnull
   public FluidState getFluidState(BlockState blockState) {
      return blockState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(blockState);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{WATERLOGGED, FACING});
   }

   @Nonnull
   public PushReaction getPistonPushReaction(@Nonnull BlockState blockState) {
      return PushReaction.DESTROY;
   }

   @Nonnull
   @ParametersAreNonnullByDefault
   public List<ItemStack> getDrops(BlockState blockState, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
      List<ItemStack> drops = super.getDrops(blockState, builder);
      if (blockState.getBlock() == ModBlocks.CRYSTAL_CLUSTER) {
         ItemStack stack = new ItemStack(ModItems.VAULT_CRYSTAL);
         CrystalData crystal = new CrystalData(stack);
         Entity entity = (Entity)builder.getOptionalParameter(LootContextParams.THIS_ENTITY);
         int level = entity instanceof Player player ? PlayerVaultStatsData.get((ServerLevel)player.level).getVaultStats(player).getVaultLevel() : 0;
         crystal.setLevel(level);
         crystal.setModel(CrystalData.Model.RAW);
         crystal.setTheme(new ValueCrystalTheme(VaultMod.id("raw_vault_cave")));
         drops.add(stack);
      }

      return drops;
   }
}
