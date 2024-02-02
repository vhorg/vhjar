package iskallia.vault.block.entity;

import iskallia.vault.block.SoulPlaqueBlock;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.SkinProfile;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SoulPlaqueTileEntity extends BlockEntity {
   private UUID uuid;
   private SkinProfile skin = new SkinProfile();
   private int score;

   public SoulPlaqueTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
      super(type, pos, state);
   }

   public SoulPlaqueTileEntity(BlockPos pos, BlockState state) {
      this(ModBlocks.SOUL_PLAQUE_TILE_ENTITY, pos, state);
   }

   public UUID getUuid() {
      return this.uuid;
   }

   public SkinProfile getSkin() {
      return this.skin;
   }

   public int getScore() {
      return this.score;
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public void sendUpdates() {
      if (this.level != null) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
         this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
         this.setChanged();
      }
   }

   public void load(CompoundTag nbt) {
      super.load(nbt);
      if (nbt.contains("OwnerUuid")) {
         this.uuid = UUID.fromString(nbt.getString("OwnerUuid"));
      } else {
         this.uuid = null;
      }

      if (nbt.contains("OwnerName")) {
         this.skin.updateSkin(nbt.getString("OwnerName"));
      }

      this.score = nbt.getInt("Score");
   }

   protected void saveAdditional(CompoundTag nbt) {
      super.saveAdditional(nbt);
      if (this.uuid != null) {
         nbt.putString("OwnerUuid", this.uuid.toString());
      }

      if (this.skin.getLatestNickname() != null) {
         nbt.putString("OwnerName", this.skin.getLatestNickname());
      }

      nbt.putInt("Score", this.score);
   }

   public static void tick(Level level, BlockPos pos, BlockState state, SoulPlaqueTileEntity tile) {
      if (level instanceof ServerLevel world) {
         int tier = ModConfigs.ASCENSION.getTier(tile.getScore());
         if ((Integer)state.getValue(SoulPlaqueBlock.TIER) != tier) {
            world.setBlock(pos, (BlockState)state.setValue(SoulPlaqueBlock.TIER, tier), 2);
         }
      }
   }
}
