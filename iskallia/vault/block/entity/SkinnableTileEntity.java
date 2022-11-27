package iskallia.vault.block.entity;

import iskallia.vault.util.SkinProfile;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class SkinnableTileEntity extends BlockEntity {
   protected SkinProfile skin = new SkinProfile();

   public SkinnableTileEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
      super(tileEntityTypeIn, pos, state);
   }

   public SkinProfile getSkin() {
      return this.skin;
   }

   protected abstract void updateSkin();

   public void sendUpdates() {
      this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
      this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
      this.setChanged();
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }
}
