package iskallia.vault.container;

import iskallia.vault.block.entity.VaultEnhancementAltarTileEntity;
import iskallia.vault.container.slot.TabSlot;
import iskallia.vault.container.spi.AbstractElementContainer;
import iskallia.vault.init.ModContainers;
import iskallia.vault.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class VaultEnhancementAltarContainer extends AbstractElementContainer {
   private final VaultEnhancementAltarTileEntity tileEntity;
   private final BlockPos tilePos;

   public VaultEnhancementAltarContainer(int windowId, Level world, BlockPos pos, Inventory playerInventory) {
      super(ModContainers.ENHANCEMENT_ALTAR_CONTAINER, windowId, playerInventory.player);
      this.tilePos = pos;
      if (world.getBlockEntity(this.tilePos) instanceof VaultEnhancementAltarTileEntity altarTileEntity) {
         this.tileEntity = altarTileEntity;
         this.initSlots(playerInventory);
      } else {
         this.tileEntity = null;
      }
   }

   private void initSlots(Inventory playerInventory) {
      for (int row = 0; row < 3; row++) {
         for (int column = 0; column < 9; column++) {
            this.addSlot(new TabSlot(playerInventory, column + row * 9 + 9, 8 + column * 18, 74 + row * 18));
         }
      }

      for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
         this.addSlot(new TabSlot(playerInventory, hotbarSlot, 8 + hotbarSlot * 18, 132));
      }

      SimpleContainer ct = this.tileEntity.getInventory();
      this.addSlot(
         new Slot(ct, 0, 80, 22) {
            public boolean mayPlace(ItemStack stack) {
               VaultEnhancementAltarTileEntity tile = VaultEnhancementAltarContainer.this.getTileEntity();
               return tile != null && !tile.getUsedPlayers().contains(VaultEnhancementAltarContainer.this.getPlayer().getUUID())
                  ? stack.is(ModItems.HELMET)
                  : false;
            }
         }
      );
   }

   public ItemStack quickMoveStack(Player playerIn, int index) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(index);
      if (slot != null && slot.hasItem()) {
         ItemStack slotStack = slot.getItem();
         itemstack = slotStack.copy();
         if (index >= 0 && index < 36 && this.moveItemStackTo(slotStack, 36, 37, false)) {
            return itemstack;
         }

         if (index >= 0 && index < 27) {
            if (!this.moveItemStackTo(slotStack, 27, 36, false)) {
               return ItemStack.EMPTY;
            }
         } else if (index >= 27 && index < 36) {
            if (!this.moveItemStackTo(slotStack, 0, 27, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(slotStack, 0, 36, false)) {
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

   public VaultEnhancementAltarTileEntity getTileEntity() {
      return this.tileEntity;
   }

   public boolean stillValid(Player player) {
      return this.tileEntity == null ? false : this.tileEntity.stillValid(player);
   }
}
