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

public class HeraldTrophyTileEntity extends BlockEntity {
   protected UUID ownerUUID;
   protected String ownerNickname;
   protected int time;

   public HeraldTrophyTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.HERALD_TROPHY_TILE_ENTITY, pos, state);
   }

   public UUID getOwnerUUID() {
      return this.ownerUUID;
   }

   public String getOwnerNickname() {
      return this.ownerNickname;
   }

   public int getTime() {
      return this.time;
   }

   public void loadFromNBT(CompoundTag nbt) {
      if (nbt.contains("OwnerUUID")) {
         this.ownerUUID = UUID.fromString(nbt.getString("OwnerUUID"));
      }

      if (nbt.contains("OwnerName")) {
         this.ownerNickname = nbt.getString("OwnerName");
      }

      if (nbt.contains("Time")) {
         this.time = nbt.getInt("Time");
      }
   }

   public void writeToEntityTag(CompoundTag nbt) {
      if (this.ownerUUID != null) {
         nbt.putString("OwnerUUID", this.ownerUUID.toString());
      }

      if (this.ownerNickname != null) {
         nbt.putString("OwnerName", this.ownerNickname);
      }

      nbt.putInt("Time", this.time);
   }

   protected void saveAdditional(CompoundTag nbt) {
      super.saveAdditional(nbt);
      this.writeToEntityTag(nbt);
   }

   public void load(CompoundTag nbt) {
      super.load(nbt);
      this.loadFromNBT(nbt);
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
