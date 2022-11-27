package iskallia.vault.container;

import iskallia.vault.init.ModContainers;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.MathUtilities;
import iskallia.vault.world.data.VaultCharmData;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;

public class VaultCharmControllerContainer extends AbstractContainerMenu {
   public Container visibleItems;
   private final int inventorySize;
   private final List<ResourceLocation> whitelist;
   private final int invStartIndex;
   private final int invEndIndex;
   private int currentStart = 0;
   private int currentEnd = 53;
   private float scrollDelta = 0.0F;

   public VaultCharmControllerContainer(int windowId, Inventory playerInventory, CompoundTag data) {
      super(ModContainers.VAULT_CHARM_CONTROLLER_CONTAINER, windowId);
      VaultCharmData.VaultCharmInventory vaultCharmInventory = VaultCharmData.VaultCharmInventory.fromNbt(data);
      this.inventorySize = vaultCharmInventory.getSize();
      this.whitelist = vaultCharmInventory.getWhitelist();
      this.initVisibleItems();
      this.initPlayerInventorySlots(playerInventory);
      this.initCharmControllerSlots();
      this.invStartIndex = 36;
      this.invEndIndex = 36 + Math.min(54, this.inventorySize);
   }

   private void initVisibleItems() {
      this.visibleItems = new SimpleContainer(this.inventorySize);
      int index = 0;

      for (ResourceLocation id : this.whitelist) {
         this.visibleItems.setItem(index, new ItemStack((ItemLike)ForgeRegistries.ITEMS.getValue(id)));
         index++;
      }
   }

   private void initPlayerInventorySlots(Inventory playerInventory) {
      for (int row = 0; row < 3; row++) {
         for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 9 + column * 18, 140 + row * 18));
         }
      }

      for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
         this.addSlot(new Slot(playerInventory, hotbarSlot, 9 + hotbarSlot * 18, 198));
      }
   }

   private void initCharmControllerSlots() {
      int rows = Math.min(6, this.inventorySize / 9);

      for (int row = 0; row < rows; row++) {
         for (int column = 0; column < 9; column++) {
            this.addSlot(new VaultCharmControllerContainer.VaultCharmControllerSlot(this.visibleItems, column + row * 9, 9 + column * 18, 18 + row * 18));
         }
      }
   }

   public boolean canScroll() {
      return this.inventorySize > 54;
   }

   public void scrollTo(float scroll) {
      if (!(scroll >= 1.0F) || !(this.scrollDelta >= 1.0F)) {
         this.shiftInventoryIndexes(this.scrollDelta - scroll < 0.0F);
         this.updateVisibleItems();
         this.scrollDelta = scroll;
      }
   }

   private void shiftInventoryIndexes(boolean ascending) {
      if (ascending) {
         this.currentStart = Math.min(this.inventorySize - 54, this.currentStart + 9);
         this.currentEnd = Math.min(this.currentStart + 54, this.inventorySize);
      } else {
         this.currentStart = Math.max(0, this.currentStart - 9);
         this.currentEnd = Math.max(54, this.currentEnd - 9);
      }
   }

   private void updateVisibleItems() {
      for (int i = 0; i < this.getInventorySize() && i < 54; i++) {
         int whitelistIndex = this.currentStart + i;
         if (whitelistIndex >= this.whitelist.size()) {
            this.visibleItems.setItem(i, ItemStack.EMPTY);
            this.lastSlots.set(i, ItemStack.EMPTY);
         } else {
            ResourceLocation id = this.whitelist.get(whitelistIndex);
            ItemStack stack = new ItemStack((ItemLike)ForgeRegistries.ITEMS.getValue(id));
            this.visibleItems.setItem(i, stack);
            this.lastSlots.add(i, stack);
         }
      }
   }

   public boolean stillValid(Player playerIn) {
      return true;
   }

   public ItemStack quickMoveStack(Player playerIn, int index) {
      ItemStack stack = ItemStack.EMPTY;
      Slot slot = this.getSlot(index);
      if (!slot.hasItem()) {
         return stack;
      } else {
         ItemStack slotStack = slot.getItem();
         stack = slotStack.copy();
         if (slot instanceof VaultCharmControllerContainer.VaultCharmControllerSlot) {
            this.whitelist.remove(slot.getItem().getItem().getRegistryName());
            slot.set(ItemStack.EMPTY);
            this.updateVisibleItems();
            return ItemStack.EMPTY;
         } else if (this.whitelist.size() < this.inventorySize && !this.whitelist.contains(stack.getItem().getRegistryName())) {
            this.whitelist.add(stack.getItem().getRegistryName());
            float pitch = MathUtilities.randomFloat(0.9F, 1.1F);
            playerIn.level.playSound(null, playerIn.blockPosition(), SoundEvents.FUNGUS_BREAK, SoundSource.PLAYERS, 0.7F, pitch);
            this.updateVisibleItems();
            return ItemStack.EMPTY;
         } else {
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

            if (slotStack.getCount() == stack.getCount()) {
               return ItemStack.EMPTY;
            } else {
               slot.onTake(playerIn, slotStack);
               this.updateVisibleItems();
               return stack;
            }
         }
      }
   }

   public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
      Slot slot = slotId >= 0 ? this.getSlot(slotId) : null;
      if (slot instanceof VaultCharmControllerContainer.VaultCharmControllerSlot) {
         if (slot.hasItem()) {
            this.whitelist.remove(slot.getItem().getItem().getRegistryName());
            slot.set(ItemStack.EMPTY);
            this.updateVisibleItems();
            return;
         }

         if (!this.getCarried().isEmpty()) {
            ItemStack stack = this.getCarried().copy();
            if (!this.whitelist.contains(stack.getItem().getRegistryName())) {
               this.whitelist.add(stack.getItem().getRegistryName());
               this.updateVisibleItems();
               return;
            }
         }
      }

      super.clicked(slotId, dragType, clickTypeIn, player);
   }

   public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
      return slot.index >= this.invStartIndex ? false : super.canTakeItemForPickAll(stack, slot);
   }

   public void removed(Player player) {
      if (player instanceof ServerPlayer sPlayer) {
         VaultCharmData.get(sPlayer.getLevel()).updateWhitelist(sPlayer, this.whitelist);
      }

      super.removed(player);
   }

   public int getInventorySize() {
      return this.inventorySize;
   }

   public List<ResourceLocation> getWhitelist() {
      return this.whitelist;
   }

   public int getCurrentAmountWhitelisted() {
      return this.whitelist.size();
   }

   public class VaultCharmControllerSlot extends Slot {
      public VaultCharmControllerSlot(Container inventory, int index, int xPosition, int yPosition) {
         super(inventory, index, xPosition, yPosition);
      }

      public boolean mayPlace(@Nonnull ItemStack stack) {
         if (this.hasItem()) {
            return false;
         } else if (stack.getItem() == ModItems.VAULT_CHARM) {
            return false;
         } else {
            ResourceLocation id = stack.getItem().getRegistryName();
            return !VaultCharmControllerContainer.this.whitelist.contains(id);
         }
      }

      public int getMaxStackSize() {
         return 1;
      }
   }
}
