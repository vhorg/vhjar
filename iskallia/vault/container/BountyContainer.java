package iskallia.vault.container;

import iskallia.vault.block.entity.BountyTableTileEntity;
import iskallia.vault.bounty.Bounty;
import iskallia.vault.bounty.BountyList;
import iskallia.vault.container.oversized.OverSizedSlotContainer;
import iskallia.vault.container.oversized.OverSizedTabSlot;
import iskallia.vault.container.slot.TabSlot;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModContainers;
import iskallia.vault.init.ModSlotIcons;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class BountyContainer extends OverSizedSlotContainer {
   private final BountyList active;
   private final BountyList available;
   private final BountyList complete;
   private final int vaultLevel;
   private final BountyTableTileEntity tileEntity;

   public BountyContainer(int id, Level level, Inventory playerInventory, CompoundTag data) {
      super(ModContainers.BOUNTY_CONTAINER, id, playerInventory.player);
      if (!data.isEmpty()) {
         this.active = new BountyList(data.getCompound("active"));
         this.available = new BountyList(data.getCompound("available"));
         this.complete = new BountyList(data.getCompound("abandoned"));
      } else {
         this.active = new BountyList();
         this.available = new BountyList();
         this.complete = new BountyList();
      }

      this.vaultLevel = data.getInt("vaultLevel");
      BlockPos pos = NbtUtils.readBlockPos(data.getCompound("pos"));
      if (level.getBlockEntity(pos) instanceof BountyTableTileEntity bountyTableTileEntity) {
         this.tileEntity = bountyTableTileEntity;
         this.initSlots(playerInventory);
      } else {
         this.tileEntity = null;
      }
   }

   public Slot getBronzeSlot() {
      return (Slot)this.slots.get(36);
   }

   private void initSlots(Inventory playerInventory) {
      for (int row = 0; row < 3; row++) {
         for (int column = 0; column < 9; column++) {
            this.addSlot(new TabSlot(playerInventory, column + row * 9 + 9, 12 + column * 18, 139 + row * 18));
         }
      }

      for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
         this.addSlot(new TabSlot(playerInventory, hotbarSlot, 12 + hotbarSlot * 18, 197));
      }

      Container invContainer = this.tileEntity.getInventory();
      this.addSlot(
         new OverSizedTabSlot(invContainer, 0, 72, 118)
            .setFilter(stack -> stack.is(ModBlocks.VAULT_BRONZE))
            .setBackground(InventoryMenu.BLOCK_ATLAS, ModSlotIcons.COINS_NO_ITEM)
      );
   }

   public BountyList getActive() {
      return this.active;
   }

   public BountyList getAvailable() {
      return this.available;
   }

   public BountyList getComplete() {
      return this.complete;
   }

   public int getVaultLevel() {
      return this.vaultLevel;
   }

   public Optional<Bounty> getBountyById(UUID id) {
      if (this.active.contains(id)) {
         return this.active.findById(id);
      } else if (this.available.contains(id)) {
         return this.available.findById(id);
      } else {
         return this.complete.contains(id) ? this.complete.findById(id) : Optional.empty();
      }
   }

   @NotNull
   public ItemStack quickMoveStack(@NotNull Player player, int index) {
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

   public boolean stillValid(@Nonnull Player player) {
      return true;
   }

   public void replaceActive(BountyList list) {
      this.active.clear();
      this.active.addAll(list);
   }

   public void replaceAvailable(BountyList list) {
      this.available.clear();
      this.available.addAll(list);
   }

   public void replaceComplete(BountyList list) {
      this.complete.clear();
      this.complete.addAll(list);
   }

   public BountyTableTileEntity getTileEntity() {
      return this.tileEntity;
   }
}
