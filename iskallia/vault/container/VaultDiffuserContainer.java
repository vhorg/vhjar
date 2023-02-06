package iskallia.vault.container;

import iskallia.vault.block.entity.VaultDiffuserTileEntity;
import iskallia.vault.container.base.SimpleSidedContainer;
import iskallia.vault.container.oversized.OverSizedSlotContainer;
import iskallia.vault.container.oversized.OverSizedTabSlot;
import iskallia.vault.container.slot.TabSlot;
import iskallia.vault.init.ModContainers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class VaultDiffuserContainer extends OverSizedSlotContainer {
   private final VaultDiffuserTileEntity tileEntity;
   private final BlockPos tilePos;

   public VaultDiffuserContainer(int windowId, Level world, BlockPos pos, Inventory playerInventory) {
      super(ModContainers.VAULT_DIFFUSER_CONTAINER, windowId, playerInventory.player);
      this.tilePos = pos;
      if (world.getBlockEntity(this.tilePos) instanceof VaultDiffuserTileEntity recyclerTileEntity) {
         this.tileEntity = recyclerTileEntity;
         this.initSlots(playerInventory);
      } else {
         this.tileEntity = null;
      }
   }

   private void initSlots(Inventory playerInventory) {
      for (int row = 0; row < 3; row++) {
         for (int column = 0; column < 9; column++) {
            this.addSlot(new TabSlot(playerInventory, column + row * 9 + 9, 8 + column * 18, 54 + row * 18));
         }
      }

      for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
         this.addSlot(new TabSlot(playerInventory, hotbarSlot, 8 + hotbarSlot * 18, 112));
      }

      SimpleSidedContainer ct = this.tileEntity.getInputInv();
      this.addSlot(new Slot(ct, 0, 55, 22) {
         public boolean mayPlace(ItemStack stack) {
            return VaultDiffuserContainer.this.tileEntity.isValidInput(stack);
         }
      });
      VaultDiffuserTileEntity.DiffuserOutput out = this.tileEntity.getOutputInv();
      this.addSlot(new OverSizedTabSlot(out, 0, 105, 22) {
         @Override
         public boolean mayPlace(ItemStack stack) {
            return false;
         }
      });
   }

   public ItemStack quickMoveStack(Player player, int index) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(index);
      if (slot.hasItem()) {
         ItemStack slotStack = slot.getItem();
         itemstack = slotStack.copy();
         if (index >= 0 && index < 36 && this.moveOverSizedItemStackTo(slotStack, slot, 36, this.slots.size() - 1, false)) {
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

         slot.onTake(player, slotStack);
      }

      return itemstack;
   }

   public BlockPos getTilePos() {
      return this.tilePos;
   }

   public VaultDiffuserTileEntity getTileEntity() {
      return this.tileEntity;
   }

   public boolean stillValid(Player player) {
      return this.tileEntity == null ? false : this.tileEntity.stillValid(this.player);
   }
}
