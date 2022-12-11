package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlackMarketTileEntity extends BlockEntity {
   public BlackMarketTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.BLACK_MARKET_TILE_ENTITY, pos, state);
   }
}
