package iskallia.vault.gear.item;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public interface CuriosGearItem {
   boolean isIntendedSlot(ItemStack var1, String var2);

   default boolean isIntendedSlot(ItemStack stack, EquipmentSlot slot) {
      return false;
   }
}
