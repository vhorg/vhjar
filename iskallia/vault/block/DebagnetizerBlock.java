package iskallia.vault.block;

import iskallia.vault.block.entity.DebagnetizerTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.DimensionPos;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

public class DebagnetizerBlock extends Block implements EntityBlock {
   private static final int RANGE = 32;
   private final Set<DimensionPos> debagnetizerBlocks = new HashSet<>();
   public static final BooleanProperty DEACTIVATED = BooleanProperty.create("deactivated");

   public DebagnetizerBlock() {
      super(Properties.copy(Blocks.IRON_BLOCK).noOcclusion());
   }

   public boolean isInRange(ResourceKey<Level> dimension, BlockPos pos) {
      for (DimensionPos dimensionPos : this.debagnetizerBlocks) {
         if (dimensionPos.isInRange(dimension, pos, 32)) {
            return true;
         }
      }

      return false;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{DEACTIVATED});
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      return (BlockState)super.defaultBlockState().setValue(DEACTIVATED, pContext.getLevel().hasNeighborSignal(pContext.getClickedPos()));
   }

   public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
      if (!level.isClientSide) {
         boolean flag = (Boolean)state.getValue(DEACTIVATED);
         if (flag != level.hasNeighborSignal(pos)) {
            if (flag) {
               level.scheduleTick(pos, this, 1);
            } else {
               level.setBlock(pos, (BlockState)state.cycle(DEACTIVATED), 2);
               if ((Boolean)state.getValue(DEACTIVATED)) {
                  this.debagnetizerBlocks.remove(new DimensionPos(level.dimension(), pos));
               } else {
                  this.debagnetizerBlocks.add(new DimensionPos(level.dimension(), pos));
               }
            }
         }
      }
   }

   public void tick(BlockState state, ServerLevel level, BlockPos pos, Random rand) {
      if ((Boolean)state.getValue(DEACTIVATED) && !level.hasNeighborSignal(pos)) {
         level.setBlock(pos, (BlockState)state.cycle(DEACTIVATED), 2);
         if ((Boolean)state.getValue(DEACTIVATED)) {
            this.debagnetizerBlocks.remove(new DimensionPos(level.dimension(), pos));
         } else {
            this.debagnetizerBlocks.add(new DimensionPos(level.dimension(), pos));
         }
      }
   }

   public void setPlacedBy(Level level, BlockPos pos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
      super.setPlacedBy(level, pos, pState, pPlacer, pStack);
      this.addDebagnetizerAt(level.dimension(), pos);
   }

   public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
      if (pState.getBlock() != pNewState.getBlock()) {
         pLevel.getBlockEntity(pPos, ModBlocks.DEBAGNETIZER_TILE_ENTITY)
            .ifPresent(blockEntity -> this.debagnetizerBlocks.remove(new DimensionPos(pLevel.dimension(), pPos)));
      }

      super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
   }

   public void addDebagnetizerAt(ResourceKey<Level> dimension, BlockPos worldPosition) {
      this.debagnetizerBlocks.add(new DimensionPos(dimension, worldPosition));
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new DebagnetizerTileEntity(pos, state);
   }
}
