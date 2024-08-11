package iskallia.vault.block.entity.base;

import iskallia.vault.container.oversized.OverSizedInvWrapper;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;

public interface FilteredInputInventoryTileEntity {
   boolean canInsertItem(int var1, @Nonnull ItemStack var2);

   boolean isInventorySideAccessible(@Nullable Direction var1);

   default <T> LazyOptional<T> getFilteredInputCapability(@Nonnull Container inventory, @Nullable Direction side) {
      return !this.isInventorySideAccessible(side)
         ? LazyOptional.empty()
         : LazyOptional.of(() -> new FilteredInputInventoryTileEntity.FilteredInvWrapper(this, inventory)).cast();
   }

   public static class FilteredInvWrapper extends OverSizedInvWrapper {
      private final FilteredInputInventoryTileEntity reference;

      public FilteredInvWrapper(FilteredInputInventoryTileEntity reference, Container inventory) {
         super(inventory);
         this.reference = reference;
      }

      @Nonnull
      @Override
      public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
         return !this.reference.canInsertItem(slot, stack) ? stack : super.insertItem(slot, stack, simulate);
      }

      @Nonnull
      public ItemStack extractItem(int slot, int amount, boolean simulate) {
         return ItemStack.EMPTY;
      }

      public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
         return this.reference.canInsertItem(slot, stack);
      }
   }
}
