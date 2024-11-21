package iskallia.vault.block.entity.base;

import iskallia.vault.container.oversized.OverSizedInvWrapper;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public interface FilteredInputInventoryTileEntity {
   boolean isInventorySideAccessible(@Nullable Direction var1);

   default <T> LazyOptional<T> getFilteredInputCapability(@Nullable Direction side, @Nonnull Container... inventories) {
      return !this.isInventorySideAccessible(side)
         ? LazyOptional.empty()
         : LazyOptional.of(
               () -> new CombinedInvWrapper(
                  (IItemHandlerModifiable[])Arrays.stream(inventories)
                     .map(FilteredInputInventoryTileEntity.InputInvWrapper::new)
                     .toArray(OverSizedInvWrapper[]::new)
               )
            )
            .cast();
   }

   public static class InputInvWrapper extends OverSizedInvWrapper {
      public InputInvWrapper(Container inventory) {
         super(inventory);
      }

      @Nonnull
      public ItemStack extractItem(int slot, int amount, boolean simulate) {
         return ItemStack.EMPTY;
      }
   }
}
