package iskallia.vault.container.slot;

import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class VaultGearSlot extends Slot {
   public VaultGearSlot(Container container, int index, int x, int y) {
      super(container, index, x, y);
   }

   public int getMaxStackSize() {
      return 1;
   }

   public boolean mayPlace(ItemStack itemStack) {
      return itemStack.getItem() instanceof VaultGearItem && VaultGearData.read(itemStack).getState() == VaultGearState.IDENTIFIED;
   }
}