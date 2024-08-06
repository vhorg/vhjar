package iskallia.vault.block;

import iskallia.vault.block.base.LootableBlock;
import iskallia.vault.block.entity.TreasureSandTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.BlockHelper;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class TreasureSandBlock extends LootableBlock {
   public TreasureSandBlock() {
      super(Properties.copy(Blocks.SAND));
   }

   @Nullable
   public <A extends BlockEntity> BlockEntityTicker<A> getTicker(Level world, BlockState state, BlockEntityType<A> type) {
      return !world.isClientSide() ? null : BlockHelper.getTicker(type, ModBlocks.TREASURE_SAND_TILE_ENTITY, TreasureSandTileEntity::tick);
   }

   @Nullable
   public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
      return ModBlocks.TREASURE_SAND_TILE_ENTITY.create(pos, state);
   }
}
