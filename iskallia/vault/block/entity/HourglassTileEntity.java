package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class HourglassTileEntity extends BlockEntity {
   private UUID ownerUUID;
   private String ownerPlayerName = "Unknown";
   private int currentSand = 0;
   private int totalSand = -1;

   public HourglassTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.HOURGLASS_TILE_ENTITY, pos, state);
   }

   public void setOwner(@Nonnull UUID ownerUUID, @Nonnull String playerName) {
      this.ownerUUID = ownerUUID;
      this.ownerPlayerName = playerName;
   }

   @Nonnull
   public UUID getOwnerUUID() {
      return this.ownerUUID;
   }

   @Nonnull
   public String getOwnerPlayerName() {
      return this.ownerPlayerName;
   }

   public void setTotalSand(int totalSand) {
      if (this.totalSand != (this.totalSand = totalSand)) {
         this.markForUpdate();
      }
   }

   public boolean addSand(Player player, int amount) {
      int total = this.totalSand <= 0 ? ModConfigs.SAND_EVENT.getTotalSandRequired(player) : this.totalSand;
      if (this.currentSand >= total) {
         return false;
      } else if (this.ownerUUID != null && !player.getUUID().equals(this.getOwnerUUID())) {
         return false;
      } else {
         this.currentSand += amount;
         this.markForUpdate();
         return true;
      }
   }

   public float getFilledPercentage() {
      return Mth.clamp((float)this.currentSand / this.totalSand, 0.0F, 1.0F);
   }

   protected void saveAdditional(CompoundTag pTag) {
      super.saveAdditional(pTag);
      if (this.ownerUUID != null) {
         pTag.putUUID("ownerUUID", this.ownerUUID);
      }

      pTag.putString("ownerPlayerName", this.ownerPlayerName);
      pTag.putInt("currentSand", this.currentSand);
      pTag.putInt("totalSand", this.totalSand);
   }

   public void load(CompoundTag pTag) {
      super.load(pTag);
      this.ownerUUID = pTag.contains("ownerUUID", 11) ? pTag.getUUID("ownerUUID") : null;
      this.ownerPlayerName = pTag.getString("ownerPlayerName");
      this.currentSand = pTag.getInt("currentSand");
      this.totalSand = pTag.getInt("totalSand");
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public void markForUpdate() {
      if (this.level != null) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
         this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
         this.setChanged();
      }
   }
}
