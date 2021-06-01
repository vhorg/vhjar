package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;

public class VaultRuneTileEntity extends TileEntity {
   protected String belongsTo = "";

   public VaultRuneTileEntity() {
      super(ModBlocks.VAULT_RUNE_TILE_ENTITY);
   }

   public String getBelongsTo() {
      return this.belongsTo;
   }

   public CompoundNBT func_189515_b(CompoundNBT nbt) {
      nbt.func_74778_a("BelongsTo", this.belongsTo);
      return super.func_189515_b(nbt);
   }

   public void func_230337_a_(BlockState state, CompoundNBT nbt) {
      this.belongsTo = nbt.func_74779_i("BelongsTo");
      super.func_230337_a_(state, nbt);
   }

   public CompoundNBT func_189517_E_() {
      CompoundNBT nbt = super.func_189517_E_();
      nbt.func_74778_a("BelongsTo", this.belongsTo);
      return nbt;
   }

   public void handleUpdateTag(BlockState state, CompoundNBT nbt) {
      this.belongsTo = nbt.func_74779_i("BelongsTo");
      super.handleUpdateTag(state, nbt);
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
