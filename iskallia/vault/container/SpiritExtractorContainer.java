package iskallia.vault.container;

import iskallia.vault.block.entity.SpiritExtractorTileEntity;
import iskallia.vault.container.oversized.OverSizedSlotContainer;
import iskallia.vault.container.oversized.OverSizedTabSlot;
import iskallia.vault.container.slot.TabSlot;
import iskallia.vault.init.ModContainers;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSlotIcons;
import iskallia.vault.network.message.SpiritExtractorBuyItemsMessage;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SpiritExtractorContainer extends OverSizedSlotContainer {
   private final BlockPos pos;
   private final SpiritExtractorTileEntity tileEntity;
   private int lastSpiritRecoveryCount = -1;

   public SpiritExtractorContainer(int id, Inventory playerInventory, BlockPos pos) {
      super(ModContainers.SPIRIT_EXTRACTOR_CONTAINER, id, playerInventory.player);
      this.pos = pos;
      if (this.player.level.getBlockEntity(pos) instanceof SpiritExtractorTileEntity spiritExtractorTile) {
         this.tileEntity = spiritExtractorTile;
         this.initSlots(playerInventory);
      } else {
         this.tileEntity = null;
      }
   }

   private void initSlots(Inventory playerInventory) {
      int playerInventoryTopY = 102;

      for (int row = 0; row < 3; row++) {
         for (int column = 0; column < 9; column++) {
            this.addSlot(new TabSlot(playerInventory, column + row * 9 + 9, 8 + column * 18, playerInventoryTopY + row * 18));
         }
      }

      for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
         this.addSlot(new TabSlot(playerInventory, hotbarSlot, 8 + hotbarSlot * 18, playerInventoryTopY + 54 + 4));
      }

      OverSizedTabSlot paymentSlot = new OverSizedTabSlot(this.tileEntity.getPaymentInventory(), 0, 90, playerInventoryTopY - 32) {
         public boolean mayPickup(Player pPlayer) {
            return !SpiritExtractorContainer.this.tileEntity.isSpewingItems();
         }

         @Override
         public boolean mayPlace(ItemStack stack) {
            return SpiritExtractorContainer.this.getTotalCost().getItem() == stack.getItem();
         }
      };
      paymentSlot.setBackground(InventoryMenu.BLOCK_ATLAS, ModSlotIcons.COINS_NO_ITEM);
      this.addSlot(paymentSlot);
   }

   public boolean stillValid(Player player) {
      return player.distanceToSqr(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) <= 64.0;
   }

   public List<ItemStack> getStoredItems() {
      return this.tileEntity.getItems();
   }

   public void startSpewingItems() {
      this.tileEntity.spewItems();
      if (this.player.level.isClientSide()) {
         ModNetwork.CHANNEL.sendToServer(new SpiritExtractorBuyItemsMessage(this.getExtractorPos()));
      }

      this.player.closeContainer();
   }

   public boolean coinsCoverTotalCost() {
      return this.tileEntity.coinsCoverTotalCost();
   }

   public BlockPos getExtractorPos() {
      return this.pos;
   }

   public ItemStack getTotalCost() {
      return this.tileEntity.getTotalCost();
   }

   public int getSpiritRecoveryCount() {
      return this.tileEntity.getSpiritRecoveryCount();
   }

   public void broadcastChanges() {
      if (this.player.level instanceof ServerLevel serverLevel && this.lastSpiritRecoveryCount != this.getLastSpiritRecoveryCount(serverLevel)) {
         this.updateLastSpiritRecoveryCountOnClient(serverLevel);
      }

      super.broadcastChanges();
   }

   public void broadcastFullState() {
      if (this.player.level instanceof ServerLevel serverLevel && this.lastSpiritRecoveryCount != this.getLastSpiritRecoveryCount(serverLevel)) {
         this.updateLastSpiritRecoveryCountOnClient(serverLevel);
      }

      super.broadcastFullState();
   }

   private void updateLastSpiritRecoveryCountOnClient(ServerLevel serverLevel) {
      this.tileEntity.recalculateCost();
      serverLevel.sendBlockUpdated(this.tileEntity.getBlockPos(), this.tileEntity.getBlockState(), this.tileEntity.getBlockState(), 3);
      this.lastSpiritRecoveryCount = this.getLastSpiritRecoveryCount(serverLevel);
   }

   private int getLastSpiritRecoveryCount(ServerLevel serverLevel) {
      return this.tileEntity.getSpiritRecoveryCountFromData(serverLevel);
   }

   public ItemStack quickMoveStack(Player player, int index) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(index);
      if (slot != null && slot.hasItem()) {
         ItemStack slotStack = slot.getItem();
         itemstack = slotStack.copy();
         if (index >= 0 && index < 36 && this.moveItemStackTo(slotStack, 36, 37, false)) {
            return ItemStack.EMPTY;
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

   public boolean isSpewingItems() {
      return this.tileEntity.isSpewingItems();
   }
}
