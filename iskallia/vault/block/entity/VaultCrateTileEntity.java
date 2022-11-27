package iskallia.vault.block.entity;

import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.init.ModBlocks;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class VaultCrateTileEntity extends BlockEntity {
   private final ItemStackHandler itemHandler = this.createHandler();
   private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> this.itemHandler);

   public VaultCrateTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.VAULT_CRATE_TILE_ENTITY, pos, state);
   }

   public void sendUpdates() {
      this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
      this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
      this.setChanged();
   }

   protected void saveAdditional(CompoundTag pTag) {
      super.saveAdditional(pTag);
      pTag.put("inv", this.itemHandler.serializeNBT());
   }

   public void load(CompoundTag pTag) {
      super.load(pTag);
      pTag.getCompound("inv").remove("Size");
      this.itemHandler.deserializeNBT(pTag.getCompound("inv"));
   }

   private ItemStackHandler createHandler() {
      return new ItemStackHandler(54) {
         protected void onContentsChanged(int slot) {
            VaultCrateTileEntity.this.sendUpdates();
         }

         public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return !stack.getItem().canFitInsideContainerItems()
               ? false
               : !(Block.byItem(stack.getItem()) instanceof ShulkerBoxBlock) && !(Block.byItem(stack.getItem()) instanceof VaultCrateBlock);
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

   public CompoundTag saveToNbt() {
      return this.itemHandler.serializeNBT();
   }

   public void loadFromNBT(CompoundTag nbt) {
      this.itemHandler.deserializeNBT(nbt);
   }
}
