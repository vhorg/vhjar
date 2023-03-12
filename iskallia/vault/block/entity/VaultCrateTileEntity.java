package iskallia.vault.block.entity;

import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.nbt.NBTHelper;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
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
   private final List<OverSizedItemStack> items = new ArrayList<>();

   public VaultCrateTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.VAULT_CRATE_TILE_ENTITY, pos, state);
   }

   public List<OverSizedItemStack> getItems() {
      return this.items;
   }

   public void sendUpdates() {
      this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
      this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
      this.setChanged();
   }

   protected void saveAdditional(CompoundTag pTag) {
      super.saveAdditional(pTag);
      NBTHelper.writeCollection(pTag, "items", this.items, CompoundTag.class, OverSizedItemStack::serialize);
   }

   public void load(CompoundTag pTag) {
      super.load(pTag);
      if (pTag.contains("inv")) {
         pTag.getCompound("inv").remove("Size");
         this.itemHandler.deserializeNBT(pTag.getCompound("inv"));

         for (int i = 0; i < this.itemHandler.getSlots(); i++) {
            ItemStack stack = this.itemHandler.getStackInSlot(i);
            this.items.add(i, OverSizedItemStack.of(stack));
         }
      } else if (pTag.contains("Items")) {
         this.items.clear();
         NonNullList<ItemStack> itemStacks = NonNullList.withSize(pTag.getList("Items", 10).size(), ItemStack.EMPTY);
         ContainerHelper.loadAllItems(pTag, itemStacks);

         for (ItemStack stack : itemStacks) {
            this.items.add(OverSizedItemStack.of(stack));
         }
      } else if (pTag.contains("items")) {
         NBTHelper.readCollection(pTag, "items", CompoundTag.class, OverSizedItemStack::deserialize, this.items);
      }
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
}
