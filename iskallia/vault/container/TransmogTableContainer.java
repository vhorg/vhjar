package iskallia.vault.container;

import iskallia.vault.container.base.RecipeContainer;
import iskallia.vault.container.inventory.TransmogTableInventory;
import iskallia.vault.container.slot.RecipeOutputSlot;
import iskallia.vault.init.ModContainers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;

public class TransmogTableContainer extends RecipeContainer {
   public TransmogTableContainer(int windowId, PlayerEntity player) {
      super(ModContainers.TRANSMOG_TABLE_CONTAINER, windowId, new TransmogTableInventory(), player);
   }

   @Override
   protected void addInternalInventorySlots() {
      this.func_75146_a(new Slot(this.internalInventory, 0, 38, 51));
      this.func_75146_a(new Slot(this.internalInventory, 1, 82, 51));
      this.func_75146_a(new Slot(this.internalInventory, 2, 60, 29));
      this.func_75146_a(new RecipeOutputSlot(this.internalInventory, this.internalInventory.outputSlotIndex(), 137, 52));
   }

   public boolean func_75145_c(PlayerEntity player) {
      return true;
   }

   public TransmogTableInventory getInternalInventory() {
      return (TransmogTableInventory)this.internalInventory;
   }
}
