package iskallia.vault.block;

import iskallia.vault.block.entity.WildSpawnerTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class WildSpawnerBlock extends BaseSpawnerBlock {
   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new WildSpawnerTileEntity(pos, state);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
      return BlockHelper.getTicker(blockEntityType, ModBlocks.WILD_SPAWNER_TILE_ENTITY, (l, p, s, te) -> WildSpawnerTileEntity.tick(l, p, te));
   }
}
