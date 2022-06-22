package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;

public class VaultChampionTrophyTileEntity extends TileEntity {
   private static final UUID NIL_UUID = new UUID(0L, 0L);
   @Nonnull
   protected UUID ownerUUID;
   @Nonnull
   protected String ownerNickname = "";
   private int score;

   public VaultChampionTrophyTileEntity() {
      super(ModBlocks.VAULT_CHAMPION_TROPHY_TILE_ENTITY);
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

   public int getScore() {
      return this.score;
   }

   public void loadFromNBT(CompoundNBT nbt) {
      this.ownerUUID = UUID.fromString(nbt.func_74779_i("OwnerUUID"));
      this.ownerNickname = nbt.func_74779_i("OwnerNickname");
      this.score = nbt.func_74762_e("Score");
   }

   public void writeToEntityTag(CompoundNBT nbt) {
      nbt.func_74778_a("OwnerUUID", this.ownerUUID.toString());
      nbt.func_74778_a("OwnerNickname", this.ownerNickname);
      nbt.func_74768_a("Score", this.score);
   }

   @Nonnull
   public CompoundNBT func_189515_b(@Nonnull CompoundNBT nbt) {
      this.writeToEntityTag(nbt);
      return super.func_189515_b(nbt);
   }

   public void func_230337_a_(@Nonnull BlockState state, @Nonnull CompoundNBT nbt) {
      this.loadFromNBT(nbt);
      super.func_230337_a_(state, nbt);
   }

   @Nonnull
   public CompoundNBT func_189517_E_() {
      CompoundNBT nbt = super.func_189517_E_();
      this.writeToEntityTag(nbt);
      return nbt;
   }

   @Nullable
   public SUpdateTileEntityPacket func_189518_D_() {
      return new SUpdateTileEntityPacket(this.field_174879_c, 1, this.func_189517_E_());
   }

   public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
      CompoundNBT tag = pkt.func_148857_g();
      this.handleUpdateTag(this.func_195044_w(), tag);
   }
}
