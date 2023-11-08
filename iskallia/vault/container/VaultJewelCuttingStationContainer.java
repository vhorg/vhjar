package iskallia.vault.container;

import iskallia.vault.block.entity.VaultJewelCuttingStationTileEntity;
import iskallia.vault.container.oversized.OverSizedSlotContainer;
import iskallia.vault.container.oversized.OverSizedTabSlot;
import iskallia.vault.container.slot.TabSlot;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModContainers;
import iskallia.vault.init.ModSlotIcons;
import iskallia.vault.item.tool.JewelItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class VaultJewelCuttingStationContainer extends OverSizedSlotContainer {
   private final VaultJewelCuttingStationTileEntity tileEntity;
   private final BlockPos tilePos;

   public VaultJewelCuttingStationContainer(int windowId, Level world, BlockPos pos, Inventory playerInventory) {
      super(ModContainers.VAULT_JEWEL_CUTTING_STATION_CONTAINER, windowId, playerInventory.player);
      this.tilePos = pos;
      if (world.getBlockEntity(this.tilePos) instanceof VaultJewelCuttingStationTileEntity craftingStationTileEntity) {
         this.tileEntity = craftingStationTileEntity;
         this.initSlots(playerInventory);
      } else {
         this.tileEntity = null;
      }
   }

   public Slot getScrapSlot() {
      return (Slot)this.slots.get(36);
   }

   public Slot getBronzeSlot() {
      return (Slot)this.slots.get(37);
   }

   public Slot getOutputSlot() {
      return (Slot)this.slots.get(38);
   }

   public Slot getOutputSlot2() {
      return (Slot)this.slots.get(39);
   }

   public Slot getOutputSlot3() {
      return (Slot)this.slots.get(40);
   }

   public Slot getJewelInputSlot() {
      return (Slot)this.slots.get(this.slots.size() - 1);
   }

   private void initSlots(Inventory playerInventory) {
      for (int row = 0; row < 3; row++) {
         for (int column = 0; column < 9; column++) {
            this.addSlot(new TabSlot(playerInventory, column + row * 9 + 9, 8 + column * 18, 148 + row * 18 - 60));
         }
      }

      for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
         this.addSlot(new TabSlot(playerInventory, hotbarSlot, 8 + hotbarSlot * 18, 146));
      }

      Container invContainer = this.tileEntity.getInventory();
      this.addSlot(
         new OverSizedTabSlot(invContainer, 0, 19, 30)
            .setFilter(stack -> stack.is(ModConfigs.VAULT_JEWEL_CUTTING_CONFIG.getJewelCuttingInput().getMainInput().getItem()))
            .setBackground(InventoryMenu.BLOCK_ATLAS, ModSlotIcons.SILVER_SCRAP_NO_ITEM)
      );
      this.addSlot(
         new OverSizedTabSlot(invContainer, 1, 39, 30)
            .setFilter(stack -> stack.is(ModConfigs.VAULT_JEWEL_CUTTING_CONFIG.getJewelCuttingInput().getSecondInput().getItem()))
            .setBackground(InventoryMenu.BLOCK_ATLAS, ModSlotIcons.COINS_NO_ITEM)
      );
      this.addSlot(new OverSizedTabSlot(invContainer, 2, 99, 40) {
         @Override
         public boolean mayPlace(ItemStack stack) {
            return false;
         }
      });
      this.addSlot(new OverSizedTabSlot(invContainer, 3, 119, 40) {
         @Override
         public boolean mayPlace(ItemStack stack) {
            return false;
         }
      });
      this.addSlot(new OverSizedTabSlot(invContainer, 4, 139, 40) {
         @Override
         public boolean mayPlace(ItemStack stack) {
            return false;
         }
      });
      this.addSlot((new TabSlot(invContainer, 5, 29, 50) {
         public boolean mayPlace(ItemStack stack) {
            return stack.getItem() instanceof JewelItem;
         }
      }).setBackground(InventoryMenu.BLOCK_ATLAS, ModSlotIcons.JEWEL_NO_ITEM));
   }

   public ItemStack quickMoveStack(Player player, int index) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(index);
      if (slot.hasItem()) {
         ItemStack slotStack = slot.getItem();
         itemstack = slotStack.copy();
         if (index >= 0 && index < 36 && this.moveOverSizedItemStackTo(slotStack, slot, 36, this.slots.size(), false)) {
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

   public VaultJewelCuttingStationTileEntity getTileEntity() {
      return this.tileEntity;
   }

   public boolean stillValid(Player player) {
      return this.tileEntity == null ? false : this.tileEntity.stillValid(this.player);
   }
}
