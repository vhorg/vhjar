package iskallia.vault.container.spi;

import iskallia.vault.block.entity.base.ForgeRecipeTileEntity;
import iskallia.vault.container.oversized.OverSizedSlotContainer;
import iskallia.vault.container.oversized.OverSizedTabSlot;
import iskallia.vault.container.slot.RecipeOutputSlot;
import iskallia.vault.container.slot.TabSlot;
import java.awt.Point;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class ForgeRecipeContainer<T extends ForgeRecipeTileEntity> extends OverSizedSlotContainer {
   private final T tile;
   private final BlockPos tilePos;

   public ForgeRecipeContainer(MenuType<?> menuType, int id, Level world, BlockPos pos, Inventory playerInventory) {
      super(menuType, id, playerInventory.player);
      this.tilePos = pos;
      BlockEntity tile = world.getBlockEntity(this.tilePos);
      if (this.getTileClass().isInstance(tile)) {
         this.tile = (T)tile;
         this.initSlots(playerInventory);
      } else {
         this.tile = null;
      }
   }

   protected abstract Class<T> getTileClass();

   public abstract Point getOffset();

   public Point getPlayerInventoryOffset() {
      return this.getOffset();
   }

   public final Slot getResultSlot() {
      return this.getSlot(this.slots.size() - 1);
   }

   public BlockPos getTilePos() {
      return this.tilePos;
   }

   @Nullable
   public T getTile() {
      return this.tile;
   }

   private void initSlots(Inventory playerInventory) {
      Point inventoryOffset = this.getPlayerInventoryOffset();
      int invXOffset = inventoryOffset.x;
      int invYOffset = inventoryOffset.y;

      for (int row = 0; row < 3; row++) {
         for (int column = 0; column < 9; column++) {
            this.addSlot(new TabSlot(playerInventory, column + row * 9 + 9, invXOffset + column * 18, 68 + invYOffset + row * 18));
         }
      }

      for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
         this.addSlot(new TabSlot(playerInventory, hotbarSlot, invXOffset + hotbarSlot * 18, 126 + invYOffset));
      }

      Point containerOffset = this.getOffset();
      int xOffset = containerOffset.x;
      int yOffset = containerOffset.y;
      Container invContainer = this.tile.getInventory();

      for (int invSlot = 0; invSlot < invContainer.getContainerSize(); invSlot++) {
         int x = xOffset + invSlot / 3 * 18;
         int y = yOffset + invSlot % 3 * 18;
         this.addSlot(new OverSizedTabSlot(invContainer, invSlot, x, y));
      }

      int slotWidth = Mth.ceil(invContainer.getContainerSize() / 3.0F);
      this.addSlot(new RecipeOutputSlot(this.tile.getResultContainer(), 0, xOffset + slotWidth * 18 + 104, 18 + yOffset));
   }

   public ItemStack quickMoveStack(Player playerIn, int index) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(index);
      if (slot != null && slot.hasItem()) {
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

         slot.onTake(playerIn, slotStack);
      }

      return itemstack;
   }

   public boolean stillValid(Player player) {
      return this.tile == null ? false : this.tile.stillValid(player);
   }
}
