package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.item.OtherSideData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class OtherSidePortalTileEntity extends BlockEntity {
   private OtherSideData data;

   public OtherSidePortalTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.OTHER_SIDE_PORTAL_TILE_ENTITY, pos, state);
   }

   public void sendUpdates() {
      this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
      this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
      this.setChanged();
   }

   protected void saveAdditional(CompoundTag pTag) {
      super.saveAdditional(pTag);
      if (this.data != null) {
         pTag.put("Data", this.data.serializeNBT());
      }
   }

   public void load(CompoundTag pTag) {
      super.load(pTag);
      if (pTag.contains("Data", 10)) {
         this.data = new OtherSideData(null);
         this.data.deserializeNBT(pTag.getCompound("Data"));
      }
   }

   public OtherSideData getData() {
      return this.data;
   }

   public void setOtherSideData(OtherSideData data) {
      this.data = data;
      this.setChanged();
   }
}
