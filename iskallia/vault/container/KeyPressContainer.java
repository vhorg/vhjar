package iskallia.vault.container;

import iskallia.vault.container.inventory.KeyPressInventory;
import iskallia.vault.init.ModContainers;
import iskallia.vault.util.EntityHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class KeyPressContainer extends Container {
   private KeyPressInventory internalInventory = new KeyPressInventory();
   private IItemHandler playerInventory;

   public KeyPressContainer(int windowId, PlayerEntity player) {
      super(ModContainers.KEY_PRESS_CONTAINER, windowId);
      this.playerInventory = new InvWrapper(player.field_71071_by);
      this.func_75146_a(new Slot(this.internalInventory, 0, 27, 47));
      this.func_75146_a(new Slot(this.internalInventory, 1, 76, 47));
      this.func_75146_a(new Slot(this.internalInventory, 2, 134, 47) {
         public ItemStack func_190901_a(PlayerEntity player, ItemStack stack) {
            ItemStack itemStack = super.func_190901_a(player, stack);
            if (!player.field_70170_p.field_72995_K && !itemStack.func_190926_b()) {
               player.field_70170_p.func_217379_c(1030, player.func_233580_cy_(), 0);
            }

            return itemStack;
         }

         public boolean func_75214_a(ItemStack stack) {
            return false;
         }
      });

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 9; j++) {
            this.func_75146_a(new Slot(player.field_71071_by, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for (int k = 0; k < 9; k++) {
         this.func_75146_a(new Slot(player.field_71071_by, k, 8 + k * 18, 142));
      }
   }

   public boolean func_75145_c(PlayerEntity player) {
      return true;
   }

   public ItemStack func_82846_b(PlayerEntity player, int index) {
      Slot slot = (Slot)this.field_75151_b.get(index);
      if (slot != null && slot.func_75216_d()) {
         ItemStack stackOnSlot = slot.func_75211_c();
         ItemStack copiedStack = stackOnSlot.func_77946_l();
         if (index == 2) {
            if (this.func_75135_a(stackOnSlot, 3, 39, false)) {
               this.internalInventory.func_70298_a(0, 1);
               this.internalInventory.func_70298_a(1, 1);
               player.field_70170_p.func_217379_c(1030, player.func_233580_cy_(), 0);
               return copiedStack;
            } else {
               return ItemStack.field_190927_a;
            }
         } else if (index != 0 && index != 1) {
            if (!this.func_75135_a(stackOnSlot, 0, 2, false)) {
               return ItemStack.field_190927_a;
            } else {
               if (stackOnSlot.func_190926_b()) {
                  slot.func_75215_d(ItemStack.field_190927_a);
               } else {
                  slot.func_75218_e();
               }

               return stackOnSlot.func_190916_E() == copiedStack.func_190916_E() ? ItemStack.field_190927_a : copiedStack;
            }
         } else if (this.func_75135_a(stackOnSlot, 3, 39, false)) {
            this.internalInventory.updateResult();
            return copiedStack;
         } else {
            return ItemStack.field_190927_a;
         }
      } else {
         return ItemStack.field_190927_a;
      }
   }

   public void func_75134_a(PlayerEntity player) {
      super.func_75134_a(player);
      ItemStack keyStack = this.internalInventory.func_70301_a(0);
      ItemStack clusterStack = this.internalInventory.func_70301_a(1);
      if (!keyStack.func_190926_b()) {
         EntityHelper.giveItem(player, keyStack);
         EntityHelper.giveItem(player, clusterStack);
      }
   }
}
