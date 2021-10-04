package iskallia.vault.container.slot;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class ReadOnlySlot extends Slot {
   public ReadOnlySlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
      super(inventoryIn, index, xPosition, yPosition);
   }

   public boolean func_75214_a(ItemStack stack) {
      return false;
   }

   public boolean func_82869_a(PlayerEntity playerIn) {
      return false;
   }
}
