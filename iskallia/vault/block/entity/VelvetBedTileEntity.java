package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class VelvetBedTileEntity extends BlockEntity {
   private DyeColor color;

   public VelvetBedTileEntity(BlockPos pWorldPosition, BlockState pBlockState) {
      super(ModBlocks.VELVET_BED_TILE_ENTITY, pWorldPosition, pBlockState);
      this.color = ((BedBlock)pBlockState.getBlock()).getColor();
   }

   public VelvetBedTileEntity(BlockPos pWorldPosition, BlockState pBlockState, DyeColor pColor) {
      super(ModBlocks.VELVET_BED_TILE_ENTITY, pWorldPosition, pBlockState);
      this.color = pColor;
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public DyeColor getColor() {
      return this.color;
   }

   public void setColor(DyeColor pColor) {
      this.color = pColor;
   }
}
