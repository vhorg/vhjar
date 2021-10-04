package iskallia.vault.container;

import iskallia.vault.container.base.RecipeContainer;
import iskallia.vault.container.inventory.KeyPressInventory;
import iskallia.vault.container.slot.RecipeOutputSlot;
import iskallia.vault.init.ModContainers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class KeyPressContainer extends RecipeContainer {
   public KeyPressContainer(int windowId, PlayerEntity player) {
      super(ModContainers.KEY_PRESS_CONTAINER, windowId, new KeyPressInventory(), player);
   }

   @Override
   protected void addInternalInventorySlots() {
      this.func_75146_a(new Slot(this.internalInventory, 0, 27, 47));
      this.func_75146_a(new Slot(this.internalInventory, 1, 76, 47));
      this.func_75146_a(new RecipeOutputSlot(this.internalInventory, this.internalInventory.outputSlotIndex(), 134, 47) {
         public ItemStack func_190901_a(PlayerEntity player, ItemStack stack) {
            ItemStack itemStack = super.func_190901_a(player, stack);
            if (!player.field_70170_p.field_72995_K && !itemStack.func_190926_b()) {
               player.field_70170_p.func_217379_c(1030, player.func_233580_cy_(), 0);
            }

            return itemStack;
         }
      });
   }

   public boolean func_75145_c(PlayerEntity player) {
      return true;
   }

   @Override
   public void onResultPicked(PlayerEntity player, int index) {
      player.field_70170_p.func_217379_c(1030, player.func_233580_cy_(), 0);
   }
}
