package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class VaultChampionTrophyTileEntity extends BlockEntity {
   private static final UUID NIL_UUID = new UUID(0L, 0L);
   @Nonnull
   protected UUID ownerUUID;
   @Nonnull
   protected String ownerNickname = "";

   public VaultChampionTrophyTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.VAULT_CHAMPION_TROPHY_TILE_ENTITY, pos, state);
      this.ownerUUID = NIL_UUID;
   }

   @Nonnull
   public UUID getOwnerUUID() {
      return this.ownerUUID;
   }

   @Nonnull
   public String getOwnerNickname() {
      return this.ownerNickname;
   }

   public void loadFromNBT(CompoundTag nbt) {
      this.ownerUUID = UUID.fromString(nbt.getString("OwnerUUID"));
      this.ownerNickname = nbt.getString("OwnerNickname");
   }

   public void writeToEntityTag(CompoundTag nbt) {
      nbt.putString("OwnerUUID", this.ownerUUID.toString());
      nbt.putString("OwnerNickname", this.ownerNickname);
   }

   protected void saveAdditional(CompoundTag pTag) {
      super.saveAdditional(pTag);
      this.writeToEntityTag(pTag);
   }

   public void load(CompoundTag pTag) {
      super.load(pTag);
      this.loadFromNBT(pTag);
   }

   @Nonnull
   public CompoundTag getUpdateTag() {
      return this.saveWithFullMetadata();
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }
}
