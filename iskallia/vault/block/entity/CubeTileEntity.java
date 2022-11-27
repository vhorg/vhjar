package iskallia.vault.block.entity;

import iskallia.vault.block.base.LootableTileEntity;
import iskallia.vault.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CubeTileEntity extends LootableTileEntity {
   public CubeTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.CUBE_BLOCK_TILE_ENTITY, pos, state);
   }
}
