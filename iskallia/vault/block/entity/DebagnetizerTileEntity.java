package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DebagnetizerTileEntity extends BlockEntity {
   public DebagnetizerTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.DEBAGNETIZER_TILE_ENTITY, pos, state);
   }
}
