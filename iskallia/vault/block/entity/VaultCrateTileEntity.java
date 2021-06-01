package iskallia.vault.block.entity;

import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.init.ModBlocks;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class VaultCrateTileEntity extends TileEntity {
   private ItemStackHandler itemHandler = this.createHandler();
   private LazyOptional<IItemHandler> handler = LazyOptional.of(() -> this.itemHandler);

   public VaultCrateTileEntity() {
      super(ModBlocks.VAULT_CRATE_TILE_ENTITY);
   }

   public void sendUpdates() {
      this.field_145850_b.func_184138_a(this.field_174879_c, this.func_195044_w(), this.func_195044_w(), 3);
      this.field_145850_b.func_195593_d(this.field_174879_c, this.func_195044_w().func_177230_c());
      this.func_70296_d();
   }

   public CompoundNBT func_189515_b(CompoundNBT compound) {
      compound.func_218657_a("inv", this.itemHandler.serializeNBT());
      return super.func_189515_b(compound);
   }

   public void func_230337_a_(BlockState state, CompoundNBT nbt) {
      nbt.func_74775_l("inv").func_82580_o("Size");
      this.itemHandler.deserializeNBT(nbt.func_74775_l("inv"));
      super.func_230337_a_(state, nbt);
   }

   private ItemStackHandler createHandler() {
      return new ItemStackHandler(54) {
         protected void onContentsChanged(int slot) {
            VaultCrateTileEntity.this.sendUpdates();
         }

         public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return !(Block.func_149634_a(stack.func_77973_b()) instanceof ShulkerBoxBlock)
               && !(Block.func_149634_a(stack.func_77973_b()) instanceof VaultCrateBlock);
         }

         @Nonnull
         public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return super.insertItem(slot, stack, simulate);
         }
      };
   }

   @Nonnull
   public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
      return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? this.handler.cast() : super.getCapability(cap, side);
   }

   public CompoundNBT saveToNbt() {
      return this.itemHandler.serializeNBT();
   }

   public void loadFromNBT(CompoundNBT nbt) {
      this.itemHandler.deserializeNBT(nbt);
   }
}
