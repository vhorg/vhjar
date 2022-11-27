package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.SkinProfile;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FinalVaultFrameTileEntity extends BlockEntity {
   private static final UUID NIL_UUID = new UUID(0L, 0L);
   @Nonnull
   protected UUID ownerUUID;
   @Nonnull
   protected String ownerNickname = "";
   protected SkinProfile skin;

   public FinalVaultFrameTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.FINAL_VAULT_FRAME_TILE_ENTITY, pos, state);
      this.ownerUUID = NIL_UUID;
      this.skin = new SkinProfile();
   }

   @Nonnull
   public String getOwnerNickname() {
      return this.ownerNickname;
   }

   @Nonnull
   public UUID getOwnerUUID() {
      return this.ownerUUID;
   }

   public SkinProfile getSkin() {
      return this.skin;
   }

   public void loadFromNBT(CompoundTag nbt) {
      this.ownerUUID = UUID.fromString(nbt.getString("OwnerUUID"));
      this.ownerNickname = nbt.getString("OwnerNickname");
      this.skin.updateSkin(this.ownerNickname);
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
      return this.saveWithoutMetadata();
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @Nullable
   public static FinalVaultFrameTileEntity get(BlockGetter reader, BlockPos pos) {
      if (reader == null) {
         return null;
      } else {
         BlockEntity tileEntity = reader.getBlockEntity(pos);
         return tileEntity instanceof FinalVaultFrameTileEntity ? (FinalVaultFrameTileEntity)tileEntity : null;
      }
   }
}
