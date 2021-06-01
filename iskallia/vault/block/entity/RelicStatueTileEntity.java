package iskallia.vault.block.entity;

import iskallia.vault.Vault;
import iskallia.vault.init.ModBlocks;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class RelicStatueTileEntity extends TileEntity {
   protected ResourceLocation relicSet = Vault.id("none");

   public RelicStatueTileEntity() {
      super(ModBlocks.RELIC_STATUE_TILE_ENTITY);
   }

   public ResourceLocation getRelicSet() {
      return this.relicSet;
   }

   public void setRelicSet(ResourceLocation relicSet) {
      this.relicSet = relicSet;
   }

   public void sendUpdates() {
      this.field_145850_b.func_184138_a(this.field_174879_c, this.func_195044_w(), this.func_195044_w(), 3);
      this.field_145850_b.func_195593_d(this.field_174879_c, this.func_195044_w().func_177230_c());
      this.func_70296_d();
   }

   public CompoundNBT func_189515_b(CompoundNBT nbt) {
      nbt.func_74778_a("RelicSet", this.relicSet.toString());
      return super.func_189515_b(nbt);
   }

   public void func_230337_a_(BlockState state, CompoundNBT nbt) {
      this.relicSet = new ResourceLocation(nbt.func_74779_i("RelicSet"));
      super.func_230337_a_(state, nbt);
   }

   public CompoundNBT func_189517_E_() {
      CompoundNBT nbt = super.func_189517_E_();
      nbt.func_74778_a("RelicSet", this.relicSet.toString());
      return nbt;
   }

   public void handleUpdateTag(BlockState state, CompoundNBT tag) {
      this.func_230337_a_(state, tag);
   }

   @Nullable
   public SUpdateTileEntityPacket func_189518_D_() {
      return new SUpdateTileEntityPacket(this.field_174879_c, 1, this.func_189517_E_());
   }

   public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
      CompoundNBT nbt = pkt.func_148857_g();
      this.handleUpdateTag(this.func_195044_w(), nbt);
   }
}
