package iskallia.vault.container.base;

import iskallia.vault.util.EntityHelper;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public abstract class RecipeContainer extends Container {
   protected RecipeInventory internalInventory;
   protected PlayerInventory playerInventory;

   protected RecipeContainer(@Nullable ContainerType<?> containerType, int windowId, RecipeInventory internalInventory, PlayerEntity player) {
      super(containerType, windowId);
      this.internalInventory = internalInventory;
      this.playerInventory = player.field_71071_by;
      this.addInternalInventorySlots();
      this.addPlayerInventorySlots();
   }

   protected abstract void addInternalInventorySlots();

   protected void addPlayerInventorySlots() {
      for (int row = 0; row < 3; row++) {
         for (int col = 0; col < 9; col++) {
            this.func_75146_a(new Slot(this.playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
         }
      }

      for (int col = 0; col < 9; col++) {
         this.func_75146_a(new Slot(this.playerInventory, col, 8 + col * 18, 142));
      }
   }

   public ItemStack func_184996_a(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
      ItemStack result = super.func_184996_a(slotId, dragType, clickTypeIn, player);
      this.internalInventory.updateResult();
      return result;
   }

   public ItemStack func_82846_b(PlayerEntity player, int index) {
      Slot slot = (Slot)this.field_75151_b.get(index);
      if (slot != null && slot.func_75216_d()) {
         ItemStack stackOnSlot = slot.func_75211_c();
         ItemStack copiedStack = stackOnSlot.func_77946_l();
         int inventoryFirstIndex = this.internalInventory.func_70302_i_();
         int inventoryLastIndex = 36 + inventoryFirstIndex;
         if (index == this.internalInventory.outputSlotIndex()) {
            if (this.func_75135_a(stackOnSlot, inventoryFirstIndex, inventoryLastIndex, false)) {
               this.internalInventory.consumeIngredients();
               this.onResultPicked(player, index);
               return copiedStack;
            } else {
               return ItemStack.field_190927_a;
            }
         } else if (!this.internalInventory.isIngredientIndex(index)) {
            if (!this.func_75135_a(stackOnSlot, 0, this.internalInventory.func_70302_i_() - 1, false)) {
               return ItemStack.field_190927_a;
            } else {
               if (stackOnSlot.func_190926_b()) {
                  slot.func_75215_d(ItemStack.field_190927_a);
               } else {
                  slot.func_75218_e();
               }

               return stackOnSlot.func_190916_E() == copiedStack.func_190916_E() ? ItemStack.field_190927_a : copiedStack;
            }
         } else if (this.func_75135_a(stackOnSlot, inventoryFirstIndex, inventoryLastIndex, false)) {
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
      this.internalInventory.forEachInput(index -> {
         ItemStack ingredientStack = this.internalInventory.func_70301_a(index);
         if (!ingredientStack.func_190926_b()) {
            EntityHelper.giveItem(player, ingredientStack);
         }
      });
   }

   public void onResultPicked(PlayerEntity player, int index) {
   }
}
