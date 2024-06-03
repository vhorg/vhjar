package iskallia.vault.container;

import iskallia.vault.block.entity.CrystalWorkbenchTileEntity;
import iskallia.vault.container.oversized.OverSizedSlotContainer;
import iskallia.vault.container.slot.TabSlot;
import iskallia.vault.init.ModContainers;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModSlotIcons;
import iskallia.vault.item.AugmentItem;
import iskallia.vault.item.InfusedCatalystItem;
import iskallia.vault.item.InscriptionItem;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.crystal.VaultCrystalItem;
import iskallia.vault.item.gear.CharmItem;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CrystalWorkbenchContainer extends OverSizedSlotContainer {
   private final CrystalWorkbenchTileEntity entity;
   private final BlockPos pos;

   public CrystalWorkbenchContainer(int windowId, Level world, BlockPos pos, Player player) {
      super(ModContainers.CRYSTAL_MODIFICATION_STATION_CONTAINER, windowId, player);
      this.pos = pos;
      if (!(world.getBlockEntity(this.pos) instanceof CrystalWorkbenchTileEntity entity)) {
         this.entity = null;
      } else {
         this.entity = entity;
         this.entity.onOpen(player);

         for (int var8 = 0; var8 < 3; var8++) {
            for (int column = 0; column < 9; column++) {
               this.addSlot(new TabSlot(player.getInventory(), column + var8 * 9 + 9, 58 + column * 18, 108 + var8 * 18));
            }
         }

         for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
            this.addSlot(new TabSlot(player.getInventory(), hotbarSlot, 58 + hotbarSlot * 18, 166));
         }

         this.addSlot(new TabSlot(this.entity.getInput(), 0, 120, 73) {
            public boolean mayPlace(ItemStack stack) {
               return stack.getItem() instanceof VaultCrystalItem && !CrystalData.read(stack).getProperties().isUnmodifiable();
            }
         });

         for (int y = 0; y < 20; y++) {
            for (int x = 0; x < 3; x++) {
               this.addSlot(
                  new TabSlot(this.entity.getIngredients(), y * 3 + x, -999 + x * 18, 50 + y * 18) {
                     public boolean mayPlace(ItemStack stack) {
                        return stack.getItem() instanceof InfusedCatalystItem
                           || stack.getItem() instanceof InscriptionItem
                           || stack.getItem() instanceof CharmItem;
                     }
                  }
               );
            }
         }

         for (int slotIndex = 0; slotIndex < 3; slotIndex++) {
            final int finalSlotIndex = slotIndex;
            this.addSlot((new TabSlot(this.entity.getUniqueIngredients(), finalSlotIndex, 112 + slotIndex * 18, -1) {
               public boolean mayPlace(ItemStack stack) {
                  if (finalSlotIndex == 0) {
                     return stack.is(ModItems.CRYSTAL_SEALS_TAG);
                  } else {
                     return finalSlotIndex == 1 ? stack.getItem() instanceof AugmentItem : stack.is(ModItems.CRYSTAL_CAPSTONES_TAG);
                  }
               }
            }).setBackground(InventoryMenu.BLOCK_ATLAS, this.getUniqueSlotBackground(slotIndex)));
         }
      }
   }

   private ResourceLocation getUniqueSlotBackground(int index) {
      switch (index) {
         case 0:
            return ModSlotIcons.SEAL_NO_ITEM;
         case 1:
            return ModSlotIcons.AUGMENT_NO_ITEM;
         case 2:
         default:
            return ModSlotIcons.CAPSTONE_NO_ITEM;
      }
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public CrystalWorkbenchTileEntity getEntity() {
      return this.entity;
   }

   public int getFirstCursedIngredient() {
      return this.entity.getFirstCursedIngredient();
   }

   public ItemStack quickMoveStack(Player player, int index) {
      Slot slot = (Slot)this.slots.get(index);
      if (!slot.hasItem()) {
         return ItemStack.EMPTY;
      } else {
         ItemStack stack = slot.getItem();
         ItemStack copy = stack.copy();
         if (index >= 0 && index < 36 && this.moveOverSizedItemStackTo(stack, slot, 36, this.slots.size(), false)) {
            return copy;
         } else {
            if (index >= 0 && index < 27) {
               if (!this.moveOverSizedItemStackTo(stack, slot, 27, 36, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (index >= 27 && index < 36) {
               if (!this.moveOverSizedItemStackTo(stack, slot, 0, 27, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (!this.moveOverSizedItemStackTo(stack, slot, 0, 36, false)) {
               return ItemStack.EMPTY;
            }

            if (stack.getCount() == 0) {
               slot.set(ItemStack.EMPTY);
            } else {
               slot.setChanged();
            }

            if (stack.getCount() == copy.getCount()) {
               return ItemStack.EMPTY;
            } else {
               slot.onTake(player, stack);
               return copy;
            }
         }
      }
   }

   public boolean stillValid(Player player) {
      return this.entity != null && this.entity.stillValid(this.player);
   }

   public void removed(Player player) {
      super.removed(player);
      this.entity.onClose(player);
   }
}
