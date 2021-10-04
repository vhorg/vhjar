package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.item.crystal.CrystalData;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;

public class VaultPortalTileEntity extends TileEntity {
   private CrystalData data;

   public VaultPortalTileEntity() {
      super(ModBlocks.VAULT_PORTAL_TILE_ENTITY);
   }

   public void sendUpdates() {
      this.field_145850_b.func_184138_a(this.field_174879_c, this.func_195044_w(), this.func_195044_w(), 3);
      this.field_145850_b.func_195593_d(this.field_174879_c, this.func_195044_w().func_177230_c());
      this.func_70296_d();
   }

   public CompoundNBT func_189515_b(CompoundNBT compound) {
      if (this.data != null) {
         compound.func_218657_a("Data", this.data.serializeNBT());
      }

      return super.func_189515_b(compound);
   }

   public void func_230337_a_(BlockState state, CompoundNBT nbt) {
      if (nbt.func_150297_b("Data", 10)) {
         this.data = new CrystalData(null);
         this.data.deserializeNBT(nbt.func_74775_l("Data"));
      }

      super.func_230337_a_(state, nbt);
   }

   public CrystalData getData() {
      return this.data;
   }

   public void setCrystalData(CrystalData data) {
      this.data = data;
      this.func_70296_d();
   }
}
