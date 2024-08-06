package iskallia.vault.block.entity;

import iskallia.vault.VaultMod;
import iskallia.vault.init.ModBlocks;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Deprecated(
   forRemoval = true
)
public class RelicPedestalTileEntity extends BlockEntity {
   protected ResourceLocation relicSet = VaultMod.id("none");

   public RelicPedestalTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.RELIC_STATUE_TILE_ENTITY, pos, state);
   }

   public boolean stillValid(Player player) {
      return this.level != null && this.level.getBlockEntity(this.worldPosition) == this;
   }

   public ResourceLocation getRelicSet() {
      return this.relicSet;
   }

   public void setRelicSet(ResourceLocation relicSet) {
      this.relicSet = relicSet;
   }

   public void sendUpdates() {
      this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
      this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
      this.setChanged();
   }

   protected void saveAdditional(CompoundTag pTag) {
      super.saveAdditional(pTag);
      pTag.putString("RelicSet", this.relicSet.toString());
   }

   public void load(CompoundTag pTag) {
      super.load(pTag);
      this.relicSet = new ResourceLocation(pTag.getString("RelicSet"));
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }
}
