package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MobBarrierTileEntity extends BlockEntity {
   public MobBarrierTileEntity(BlockPos pWorldPosition, BlockState pBlockState) {
      super(ModBlocks.MOB_BARRIER_ENTITY, pWorldPosition, pBlockState);
   }
}
