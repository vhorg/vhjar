package iskallia.vault.container.base;

import iskallia.vault.container.slot.PlayerSensitiveSlot;
import java.util.Set;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public interface PlayerSensitiveContainer {
   void setDragMode(int var1);

   int getDragMode();

   void setDragEvent(int var1);

   int getDragEvent();

   Set<Slot> getDragSlots();

   default void resetDrag() {
      this.setDragEvent(0);
      this.getDragSlots().clear();
   }

   default ItemStack playerSensitiveSlotClick(Container thisContainer, int slotId, int dragType, ClickType clickType, PlayerEntity player) {
      ItemStack itemstack = ItemStack.field_190927_a;
      PlayerInventory playerinventory = player.field_71071_by;
      if (clickType == ClickType.QUICK_CRAFT) {
         int currentDragEvent = this.getDragEvent();
         this.setDragEvent(Container.func_94532_c(dragType));
         if ((currentDragEvent != 1 || this.getDragEvent() != 2) && currentDragEvent != this.getDragEvent()) {
            this.resetDrag();
         } else if (playerinventory.func_70445_o().func_190926_b()) {
            this.resetDrag();
         } else if (this.getDragEvent() == 0) {
            this.setDragEvent(Container.func_94529_b(dragType));
            if (Container.func_180610_a(this.getDragMode(), player)) {
               this.setDragEvent(1);
               this.getDragSlots().clear();
            } else {
               this.resetDrag();
            }
         } else if (this.getDragEvent() == 1) {
            Slot slot7 = (Slot)thisContainer.field_75151_b.get(slotId);
            ItemStack itemstack12 = playerinventory.func_70445_o();
            if (slot7 != null
               && canMergeSlotItemStack(player, slot7, itemstack12, true)
               && slot7.func_75214_a(itemstack12)
               && (this.getDragMode() == 2 || itemstack12.func_190916_E() > this.getDragSlots().size())
               && thisContainer.func_94531_b(slot7)) {
               this.getDragSlots().add(slot7);
            }
         } else if (this.getDragEvent() == 2) {
            if (!this.getDragSlots().isEmpty()) {
               ItemStack itemstack10 = playerinventory.func_70445_o().func_77946_l();
               int k1 = playerinventory.func_70445_o().func_190916_E();

               for (Slot slot8 : this.getDragSlots()) {
                  ItemStack itemstack13 = playerinventory.func_70445_o();
                  if (slot8 != null
                     && canMergeSlotItemStack(player, slot8, itemstack13, true)
                     && slot8.func_75214_a(itemstack13)
                     && (this.getDragMode() == 2 || itemstack13.func_190916_E() >= this.getDragSlots().size())
                     && thisContainer.func_94531_b(slot8)) {
                     ItemStack itemstack14 = itemstack10.func_77946_l();
                     int j3 = slot8.func_75216_d() ? slot8.func_75211_c().func_190916_E() : 0;
                     Container.func_94525_a(this.getDragSlots(), this.getDragMode(), itemstack14, j3);
                     int k3 = Math.min(itemstack14.func_77976_d(), slot8.func_178170_b(itemstack14));
                     if (itemstack14.func_190916_E() > k3) {
                        itemstack14.func_190920_e(k3);
                     }

                     k1 -= itemstack14.func_190916_E() - j3;
                     slot8.func_75215_d(itemstack14);
                  }
               }

               itemstack10.func_190920_e(k1);
               playerinventory.func_70437_b(itemstack10);
            }

            this.resetDrag();
         } else {
            this.resetDrag();
         }
      } else if (this.getDragEvent() != 0) {
         this.resetDrag();
      } else if ((clickType == ClickType.PICKUP || clickType == ClickType.QUICK_MOVE) && (dragType == 0 || dragType == 1)) {
         if (slotId == -999) {
            if (!playerinventory.func_70445_o().func_190926_b()) {
               if (dragType == 0) {
                  player.func_71019_a(playerinventory.func_70445_o(), true);
                  playerinventory.func_70437_b(ItemStack.field_190927_a);
               }

               if (dragType == 1) {
                  player.func_71019_a(playerinventory.func_70445_o().func_77979_a(1), true);
               }
            }
         } else if (clickType == ClickType.QUICK_MOVE) {
            if (slotId < 0) {
               return ItemStack.field_190927_a;
            }

            Slot slot5 = (Slot)thisContainer.field_75151_b.get(slotId);
            if (slot5 == null || !slot5.func_82869_a(player)) {
               return ItemStack.field_190927_a;
            }

            for (ItemStack itemstack8 = thisContainer.func_82846_b(player, slotId);
               !itemstack8.func_190926_b() && ItemStack.func_179545_c(slot5.func_75211_c(), itemstack8);
               itemstack8 = thisContainer.func_82846_b(player, slotId)
            ) {
               itemstack = itemstack8.func_77946_l();
            }
         } else {
            if (slotId < 0) {
               return ItemStack.field_190927_a;
            }

            Slot slot = (Slot)thisContainer.field_75151_b.get(slotId);
            if (slot != null) {
               ItemStack slotStack = slot.func_75211_c();
               ItemStack playerMouseStack = playerinventory.func_70445_o();
               if (!slotStack.func_190926_b()) {
                  itemstack = slotStack.func_77946_l();
               }

               if (slotStack.func_190926_b()) {
                  if (!playerMouseStack.func_190926_b() && slot.func_75214_a(playerMouseStack)) {
                     int j2 = dragType == 0 ? playerMouseStack.func_190916_E() : 1;
                     if (j2 > slot.func_178170_b(playerMouseStack)) {
                        j2 = slot.func_178170_b(playerMouseStack);
                     }

                     slot.func_75215_d(playerMouseStack.func_77979_a(j2));
                  }
               } else if (slot.func_82869_a(player)) {
                  if (playerMouseStack.func_190926_b()) {
                     if (slotStack.func_190926_b()) {
                        slot.func_75215_d(ItemStack.field_190927_a);
                        playerinventory.func_70437_b(ItemStack.field_190927_a);
                     } else {
                        int k2 = dragType == 0 ? slotStack.func_190916_E() : (slotStack.func_190916_E() + 1) / 2;
                        ItemStack pickedStack = slot.func_75209_a(k2);
                        if (slot instanceof PlayerSensitiveSlot) {
                           pickedStack = ((PlayerSensitiveSlot)slot).modifyTakenStack(player, pickedStack, false);
                        }

                        playerinventory.func_70437_b(pickedStack);
                        if (slotStack.func_190926_b()) {
                           slot.func_75215_d(ItemStack.field_190927_a);
                        }

                        slot.func_190901_a(player, playerinventory.func_70445_o());
                     }
                  } else if (slot.func_75214_a(playerMouseStack)) {
                     if (Container.func_195929_a(slotStack, playerMouseStack)) {
                        int l2 = dragType == 0 ? playerMouseStack.func_190916_E() : 1;
                        if (l2 > slot.func_178170_b(playerMouseStack) - slotStack.func_190916_E()) {
                           l2 = slot.func_178170_b(playerMouseStack) - slotStack.func_190916_E();
                        }

                        if (l2 > playerMouseStack.func_77976_d() - slotStack.func_190916_E()) {
                           l2 = playerMouseStack.func_77976_d() - slotStack.func_190916_E();
                        }

                        playerMouseStack.func_190918_g(l2);
                        slotStack.func_190917_f(l2);
                     } else if (playerMouseStack.func_190916_E() <= slot.func_178170_b(playerMouseStack)) {
                        slot.func_75215_d(playerMouseStack);
                        playerinventory.func_70437_b(slotStack);
                     }
                  } else if (playerMouseStack.func_77976_d() > 1 && Container.func_195929_a(slotStack, playerMouseStack) && !slotStack.func_190926_b()) {
                     int i3 = slotStack.func_190916_E();
                     if (i3 + playerMouseStack.func_190916_E() <= playerMouseStack.func_77976_d()) {
                        playerMouseStack.func_190917_f(i3);
                        slotStack = slot.func_75209_a(i3);
                        if (slot instanceof PlayerSensitiveSlot) {
                           slotStack = ((PlayerSensitiveSlot)slot).modifyTakenStack(player, slotStack, false);
                        }

                        if (slotStack.func_190926_b()) {
                           slot.func_75215_d(ItemStack.field_190927_a);
                        }

                        slot.func_190901_a(player, playerinventory.func_70445_o());
                     }
                  }
               }

               slot.func_75218_e();
            }
         }
      } else if (clickType == ClickType.SWAP) {
         Slot slot = (Slot)thisContainer.field_75151_b.get(slotId);
         ItemStack plInventoryDragStack = playerinventory.func_70301_a(dragType);
         ItemStack slotStackx = slot.func_75211_c();
         if (!plInventoryDragStack.func_190926_b() || !slotStackx.func_190926_b()) {
            if (plInventoryDragStack.func_190926_b()) {
               if (slot.func_82869_a(player)) {
                  if (slot instanceof PlayerSensitiveSlot) {
                     slotStackx = ((PlayerSensitiveSlot)slot).modifyTakenStack(player, slotStackx, false);
                  }

                  playerinventory.func_70299_a(dragType, slotStackx);
                  slot.func_75215_d(ItemStack.field_190927_a);
                  slot.func_190901_a(player, slotStackx);
               }
            } else if (slotStackx.func_190926_b()) {
               if (slot.func_75214_a(plInventoryDragStack)) {
                  int i = slot.func_178170_b(plInventoryDragStack);
                  if (plInventoryDragStack.func_190916_E() > i) {
                     slot.func_75215_d(plInventoryDragStack.func_77979_a(i));
                  } else {
                     slot.func_75215_d(plInventoryDragStack);
                     playerinventory.func_70299_a(dragType, ItemStack.field_190927_a);
                  }
               }
            } else if (slot.func_82869_a(player) && slot.func_75214_a(plInventoryDragStack)) {
               int l1 = slot.func_178170_b(plInventoryDragStack);
               if (plInventoryDragStack.func_190916_E() > l1) {
                  slot.func_75215_d(plInventoryDragStack.func_77979_a(l1));
                  if (slot instanceof PlayerSensitiveSlot) {
                     slotStackx = ((PlayerSensitiveSlot)slot).modifyTakenStack(player, slotStackx, false);
                  }

                  slot.func_190901_a(player, slotStackx);
                  if (!playerinventory.func_70441_a(slotStackx)) {
                     player.func_71019_a(slotStackx, true);
                  }
               } else {
                  slot.func_75215_d(plInventoryDragStack);
                  if (slot instanceof PlayerSensitiveSlot) {
                     slotStackx = ((PlayerSensitiveSlot)slot).modifyTakenStack(player, slotStackx, false);
                  }

                  playerinventory.func_70299_a(dragType, slotStackx);
                  slot.func_190901_a(player, slotStackx);
               }
            }
         }
      } else if (clickType == ClickType.CLONE && player.field_71075_bZ.field_75098_d && playerinventory.func_70445_o().func_190926_b() && slotId >= 0) {
         Slot slot4 = (Slot)thisContainer.field_75151_b.get(slotId);
         if (slot4 != null && slot4.func_75216_d()) {
            ItemStack itemstack7 = slot4.func_75211_c().func_77946_l();
            itemstack7.func_190920_e(itemstack7.func_77976_d());
            playerinventory.func_70437_b(itemstack7);
         }
      } else if (clickType == ClickType.THROW && playerinventory.func_70445_o().func_190926_b() && slotId >= 0) {
         Slot slot3 = (Slot)thisContainer.field_75151_b.get(slotId);
         if (slot3 != null && slot3.func_75216_d() && slot3.func_82869_a(player)) {
            ItemStack slotStackx = slot3.func_75209_a(dragType == 0 ? 1 : slot3.func_75211_c().func_190916_E());
            if (slot3 instanceof PlayerSensitiveSlot) {
               slotStackx = ((PlayerSensitiveSlot)slot3).modifyTakenStack(player, slotStackx, false);
            }

            slot3.func_190901_a(player, slotStackx);
            player.func_71019_a(slotStackx, true);
         }
      } else if (clickType == ClickType.PICKUP_ALL && slotId >= 0) {
         Slot slot = (Slot)thisContainer.field_75151_b.get(slotId);
         ItemStack playerMouseStackx = playerinventory.func_70445_o();
         if (!playerMouseStackx.func_190926_b() && (slot == null || !slot.func_75216_d() || !slot.func_82869_a(player))) {
            int j1 = dragType == 0 ? 0 : thisContainer.field_75151_b.size() - 1;
            int i2 = dragType == 0 ? 1 : -1;

            for (int j = 0; j < 2; j++) {
               for (int k = j1;
                  k >= 0 && k < thisContainer.field_75151_b.size() && playerMouseStackx.func_190916_E() < playerMouseStackx.func_77976_d();
                  k += i2
               ) {
                  Slot slot1 = (Slot)thisContainer.field_75151_b.get(k);
                  if (slot1.func_75216_d()
                     && canMergeSlotItemStack(player, slot1, playerMouseStackx, true)
                     && slot1.func_82869_a(player)
                     && thisContainer.func_94530_a(playerMouseStackx, slot1)) {
                     ItemStack itemstack3 = slot1.func_75211_c();
                     if (j != 0 || itemstack3.func_190916_E() != itemstack3.func_77976_d()) {
                        int l = Math.min(playerMouseStackx.func_77976_d() - playerMouseStackx.func_190916_E(), itemstack3.func_190916_E());
                        ItemStack slotStackx = slot1.func_75209_a(l);
                        if (slot1 instanceof PlayerSensitiveSlot) {
                           slotStackx = ((PlayerSensitiveSlot)slot1).modifyTakenStack(player, slotStackx, false);
                        }

                        playerMouseStackx.func_190917_f(l);
                        if (slotStackx.func_190926_b()) {
                           slot1.func_75215_d(ItemStack.field_190927_a);
                        }

                        slot1.func_190901_a(player, slotStackx);
                     }
                  }
               }
            }
         }

         thisContainer.func_75142_b();
      }

      return itemstack;
   }

   static boolean canMergeSlotItemStack(PlayerEntity player, Slot slot, ItemStack toMergeOn, boolean stackSizeMatters) {
      if (slot != null && slot.func_75216_d()) {
         ItemStack slotStack = slot.func_75211_c();
         if (slot instanceof PlayerSensitiveSlot) {
            slotStack = ((PlayerSensitiveSlot)slot).modifyTakenStack(player, slotStack, true);
         }

         return toMergeOn.func_77969_a(slotStack) && ItemStack.func_77970_a(slotStack, toMergeOn)
            ? slotStack.func_190916_E() + (stackSizeMatters ? 0 : toMergeOn.func_190916_E()) <= toMergeOn.func_77976_d()
            : false;
      } else {
         return true;
      }
   }
}
