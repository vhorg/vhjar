package iskallia.vault.container;

import iskallia.vault.container.inventory.KeyPressInventory;
import iskallia.vault.container.slot.RecipeOutputSlot;
import iskallia.vault.container.spi.RecipeContainer;
import iskallia.vault.init.ModContainers;
import javax.annotation.Nonnull;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class KeyPressContainer extends RecipeContainer {
   public KeyPressContainer(int windowId, Player player) {
      super(ModContainers.KEY_PRESS_CONTAINER, windowId, new KeyPressInventory(), player);
   }

   @Override
   protected void addInternalInventorySlots() {
      this.addSlot(new Slot(this.internalInventory, 0, 27, 47));
      this.addSlot(new Slot(this.internalInventory, 1, 76, 47));
      this.addSlot(new RecipeOutputSlot(this.internalInventory, this.internalInventory.outputSlotIndex(), 134, 47) {
         public void onTake(@Nonnull Player player, @Nonnull ItemStack itemStack) {
            super.onTake(player, itemStack);
            if (!player.level.isClientSide && !itemStack.isEmpty()) {
               player.level.levelEvent(1030, player.blockPosition(), 0);
            }
         }
      });
   }

   public boolean stillValid(@Nonnull Player player) {
      return true;
   }

   @Override
   public void onResultPicked(Player player, int index) {
      player.level.levelEvent(1030, player.blockPosition(), 0);
   }
}
