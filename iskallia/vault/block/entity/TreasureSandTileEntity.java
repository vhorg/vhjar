package iskallia.vault.block.entity;

import iskallia.vault.block.base.LootableTileEntity;
import iskallia.vault.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TreasureSandTileEntity extends LootableTileEntity {
   public TreasureSandTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.TREASURE_SAND_TILE_ENTITY, pos, state);
   }
}
