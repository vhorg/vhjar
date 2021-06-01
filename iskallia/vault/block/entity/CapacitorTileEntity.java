package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

public class CapacitorTileEntity extends TileEntity {
   private CapacitorTileEntity.CapacitorEnergyStorage energyStorage = this.createEnergyStorage();
   private LazyOptional<IEnergyStorage> handler = LazyOptional.of(() -> this.energyStorage);

   private CapacitorTileEntity.CapacitorEnergyStorage createEnergyStorage() {
      return new CapacitorTileEntity.CapacitorEnergyStorage(1000000000, 10000) {
         @Override
         protected void onEnergyChanged() {
            CapacitorTileEntity.this.func_70296_d();
         }
      };
   }

   public CapacitorTileEntity.CapacitorEnergyStorage getEnergyStorage() {
      return this.energyStorage;
   }

   public CapacitorTileEntity() {
      super(ModBlocks.CAPACITOR_TILE_ENTITY);
   }

   public void sendUpdates() {
      this.field_145850_b.func_184138_a(this.field_174879_c, this.func_195044_w(), this.func_195044_w(), 3);
      this.field_145850_b.func_195593_d(this.field_174879_c, this.func_195044_w().func_177230_c());
      this.func_70296_d();
   }

   public CompoundNBT func_189515_b(CompoundNBT compound) {
      compound.func_218657_a("energy", this.energyStorage.serializeNBT());
      return super.func_189515_b(compound);
   }

   public void func_230337_a_(BlockState state, CompoundNBT nbt) {
      this.energyStorage.deserializeNBT(nbt.func_74775_l("energy"));
      super.func_230337_a_(state, nbt);
   }

   @Nonnull
   public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
      return cap == CapabilityEnergy.ENERGY ? this.handler.cast() : super.getCapability(cap, side);
   }

   public static class CapacitorEnergyStorage extends EnergyStorage implements INBTSerializable<CompoundNBT> {
      public CapacitorEnergyStorage(int capacity, int maxTransfer) {
         super(capacity, maxTransfer);
      }

      protected void onEnergyChanged() {
      }

      public void setEnergy(int energy) {
         this.energy = energy;
         this.onEnergyChanged();
      }

      public void addEnergy(int energy) {
         this.energy += energy;
         if (this.energy > this.getMaxEnergyStored()) {
            this.energy = this.getEnergyStored();
         }

         this.onEnergyChanged();
      }

      public void consumeEnergy(int energy) {
         this.energy -= energy;
         if (this.energy < 0) {
            this.energy = 0;
         }

         this.onEnergyChanged();
      }

      public CompoundNBT serializeNBT() {
         CompoundNBT nbt = new CompoundNBT();
         nbt.func_74768_a("energy", this.getEnergyStored());
         return nbt;
      }

      public void deserializeNBT(CompoundNBT nbt) {
         this.setEnergy(nbt.func_74762_e("energy"));
      }
   }
}
