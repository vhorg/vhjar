package iskallia.vault.block;

import iskallia.vault.block.entity.ScavengerChestTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.BlockHelper;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.jetbrains.annotations.Nullable;

public class ScavengerChestBlock extends ChestBlock {
   protected ScavengerChestBlock(Properties builder, Supplier<BlockEntityType<? extends ChestBlockEntity>> tileEntityTypeIn) {
      super(builder, tileEntityTypeIn);
   }

   public ScavengerChestBlock(Properties builder) {
      this(builder, () -> ModBlocks.SCAVENGER_CHEST_TILE_ENTITY);
   }

   @Nullable
   public <A extends BlockEntity> BlockEntityTicker<A> getTicker(Level level, BlockState state, BlockEntityType<A> tBlockEntityType) {
      return BlockHelper.getTicker(tBlockEntityType, ModBlocks.SCAVENGER_CHEST_TILE_ENTITY, level.isClientSide ? ScavengerChestTileEntity::tick : null);
   }

   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return new ScavengerChestTileEntity(pPos, pState);
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockState state = super.getStateForPlacement(context);
      return state == null ? null : (BlockState)state.setValue(TYPE, ChestType.SINGLE);
   }
}
