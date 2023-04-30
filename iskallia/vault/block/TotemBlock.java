package iskallia.vault.block;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public abstract class TotemBlock extends Block implements EntityBlock, SimpleWaterloggedBlock {
   protected static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   private static final VoxelShape SHAPE = Block.box(6.0, 0.0, 6.0, 10.0, 9.0, 10.0);

   public TotemBlock() {
      super(
         Properties.of(Material.PORTAL)
            .noCollission()
            .noOcclusion()
            .strength(-1.0F, 3600000.0F)
            .noDrops()
            .isValidSpawn((blockState, blockGetter, blockPos, entityType) -> false)
            .lightLevel(value -> 5)
      );
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(WATERLOGGED, false));
   }

   @ParametersAreNonnullByDefault
   @Nonnull
   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return SHAPE;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{WATERLOGGED});
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockPos blockPos = context.getClickedPos();
      FluidState fluidstate = context.getLevel().getFluidState(blockPos);
      return (BlockState)this.defaultBlockState().setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
   }

   @Nonnull
   public FluidState getFluidState(BlockState blockState) {
      return blockState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(blockState);
   }
}
