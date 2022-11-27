package iskallia.vault.container;

import iskallia.vault.block.entity.VaultForgeTileEntity;
import iskallia.vault.container.oversized.OverSizedSlotContainer;
import iskallia.vault.container.slot.RecipeOutputSlot;
import iskallia.vault.container.slot.TabSlot;
import iskallia.vault.init.ModContainers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class VaultForgeContainer extends OverSizedSlotContainer {
   private final VaultForgeTileEntity tileEntity;
   private final BlockPos tilePos;

   public VaultForgeContainer(int windowId, Level world, BlockPos pos, Inventory playerInventory) {
      super(ModContainers.VAULT_FORGE_CONTAINER, windowId, playerInventory.player);
      this.tilePos = pos;
      if (world.getBlockEntity(this.tilePos) instanceof VaultForgeTileEntity craftingStationTileEntity) {
         this.tileEntity = craftingStationTileEntity;
         this.initSlots(playerInventory);
      } else {
         this.tileEntity = null;
      }
   }

   public Slot getResultSlot() {
      return this.getSlot(42);
   }

   private void initSlots(Inventory playerInventory) {
      for (int row = 0; row < 3; row++) {
         for (int column = 0; column < 9; column++) {
            this.addSlot(new TabSlot(playerInventory, column + row * 9 + 9, 8 + column * 18, 124 + row * 18));
         }
      }

      for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
         this.addSlot(new TabSlot(playerInventory, hotbarSlot, 8 + hotbarSlot * 18, 182));
      }

      final Container invContainer = this.tileEntity.getInventory();

      for (int invSlot = 0; invSlot < invContainer.getContainerSize(); invSlot++) {
         int x = 8 + invSlot / 3 * 18;
         int y = 56 + invSlot % 3 * 18;
         this.addSlot(new TabSlot(invContainer, invSlot, x, y) {
            public int getMaxStackSize(ItemStack stack) {
               return invContainer.getMaxStackSize();
            }
         });
      }

      this.addSlot(new RecipeOutputSlot(this.tileEntity.getResultContainer(), 0, 148, 74));
   }

   public ItemStack quickMoveStack(Player playerIn, int index) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(index);
      if (slot != null && slot.hasItem()) {
         ItemStack slotStack = slot.getItem();
         itemstack = slotStack.copy();
         if (index >= 0 && index < 36 && this.moveOverSizedItemStackTo(slotStack, slot, 36, 42, false)) {
            return itemstack;
         }

         if (index >= 0 && index < 27) {
            if (!this.moveOverSizedItemStackTo(slotStack, slot, 27, 36, false)) {
               return ItemStack.EMPTY;
            }
         } else if (index >= 27 && index < 36) {
            if (!this.moveOverSizedItemStackTo(slotStack, slot, 0, 27, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveOverSizedItemStackTo(slotStack, slot, 0, 36, false)) {
            return ItemStack.EMPTY;
         }

         if (slotStack.getCount() == 0) {
            slot.set(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }

         if (slotStack.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(playerIn, slotStack);
      }

      return itemstack;
   }

   public BlockPos getTilePos() {
      return this.tilePos;
   }

   public VaultForgeTileEntity getTileEntity() {
      return this.tileEntity;
   }

   public boolean stillValid(Player player) {
      return this.tileEntity == null ? false : this.tileEntity.getInventory().stillValid(this.player);
   }
}
