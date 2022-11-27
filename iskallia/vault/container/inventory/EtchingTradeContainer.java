package iskallia.vault.container.inventory;

import iskallia.vault.block.entity.EtchingVendorControllerTileEntity;
import iskallia.vault.container.slot.EtchingBuySlot;
import iskallia.vault.container.slot.FilteredSlot;
import iskallia.vault.entity.entity.EtchingVendorEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModContainers;
import javax.annotation.Nullable;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.InvWrapper;

public class EtchingTradeContainer extends AbstractContainerMenu {
   private final Container tradeInventory = new SimpleContainer(6);
   private final Level world;
   private final int vendorEntityId;

   public EtchingTradeContainer(int containerId, Inventory playerInventory, int vendorEntityId) {
      super(ModContainers.ETCHING_TRADE_CONTAINER, containerId);
      this.world = playerInventory.player.level;
      this.vendorEntityId = vendorEntityId;
      this.initPlayerSlots(playerInventory);
      this.initTradeSlots();
   }

   private void initPlayerSlots(Inventory playerInventory) {
      for (int row = 0; row < 3; row++) {
         for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 102 + row * 18));
         }
      }

      for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
         this.addSlot(new Slot(playerInventory, hotbarSlot, 8 + hotbarSlot * 18, 160));
      }
   }

   private void initTradeSlots() {
      for (int i = 0; i < 3; i++) {
         this.addSlot(new FilteredSlot(new InvWrapper(this.tradeInventory), i * 2, 53, 10 + i * 28, stack -> stack.getItem() == ModBlocks.VAULT_PLATINUM));
         this.addSlot(new EtchingBuySlot(this, new InvWrapper(this.tradeInventory), i, i * 2 + 1, 107, 10 + i * 28));
      }

      EtchingVendorEntity vendor = this.getVendor();
      if (vendor != null) {
         EtchingVendorControllerTileEntity controllerTile = vendor.getControllerTile();
         if (controllerTile != null) {
            for (int i = 0; i < 3; i++) {
               EtchingVendorControllerTileEntity.EtchingTrade trade = controllerTile.getTrade(i);
               if (trade != null && !trade.isSold()) {
                  Slot outSlot = this.getSlot(37 + i * 2);
                  outSlot.set(trade.getSoldEtching().copy());
               }
            }
         }
      }
   }

   @Nullable
   public EtchingVendorEntity getVendor() {
      return (EtchingVendorEntity)this.world.getEntity(this.vendorEntityId);
   }

   public void removed(Player player) {
      super.removed(player);
      this.tradeInventory.setItem(1, ItemStack.EMPTY);
      this.tradeInventory.setItem(3, ItemStack.EMPTY);
      this.tradeInventory.setItem(5, ItemStack.EMPTY);
      this.clearContainer(player, this.tradeInventory);
   }

   public ItemStack quickMoveStack(Player player, int index) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(index);
      if (slot != null && slot.hasItem()) {
         ItemStack slotStack = slot.getItem();
         itemstack = slotStack.copy();
         if (index >= 0 && index < 36 && this.moveItemStackTo(slotStack, 36, 42, false)) {
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

         slot.onTake(player, slotStack);
      }

      return itemstack;
   }

   public boolean stillValid(Player player) {
      EtchingVendorEntity vendor = this.getVendor();
      return vendor != null && vendor.isValid();
   }
}
