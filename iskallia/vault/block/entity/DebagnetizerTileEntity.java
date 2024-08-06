package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DebagnetizerTileEntity extends BlockEntity {
   public int tickCount;

   public DebagnetizerTileEntity(BlockPos pPos, BlockState pState) {
      super(ModBlocks.DEBAGNETIZER_TILE_ENTITY, pPos, pState);
   }

   public void onLoad() {
      super.onLoad();
      ModBlocks.DEBAGNETIZER.addDebagnetizerAt(this.level.dimension(), this.worldPosition);
   }
}
