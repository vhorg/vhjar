package iskallia.vault.gear.crafting;

import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.data.VaultGearData;
import java.util.ArrayList;
import net.minecraft.world.item.ItemStack;

public class VaultEnchanterHelper {
   public static boolean hasCraftedModifier(ItemStack gear) {
      if (!gear.isEmpty() && AttributeGearData.hasData(gear)) {
         VaultGearData data = VaultGearData.read(gear);

         for (VaultGearModifier<?> modifier : data.getAllModifierAffixes()) {
            if (modifier.getCategory() == VaultGearModifier.AffixCategory.CRAFTED) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static boolean removeCraftedModifiers(ItemStack stack) {
      VaultGearData data = VaultGearData.read(stack);
      boolean removedModifiers = false;

      for (VaultGearModifier<?> modifier : new ArrayList<>(data.getModifiers(VaultGearModifier.AffixType.IMPLICIT))) {
         if (modifier.getCategory() == VaultGearModifier.AffixCategory.CRAFTED && data.removeModifier(modifier)) {
            removedModifiers = true;
         }
      }

      for (VaultGearModifier<?> modifierx : new ArrayList<>(data.getModifiers(VaultGearModifier.AffixType.PREFIX))) {
         if (modifierx.getCategory() == VaultGearModifier.AffixCategory.CRAFTED && data.removeModifier(modifierx)) {
            removedModifiers = true;
         }
      }

      for (VaultGearModifier<?> modifierxx : new ArrayList<>(data.getModifiers(VaultGearModifier.AffixType.SUFFIX))) {
         if (modifierxx.getCategory() == VaultGearModifier.AffixCategory.CRAFTED && data.removeModifier(modifierxx)) {
            removedModifiers = true;
         }
      }

      if (removedModifiers) {
         data.write(stack);
      }

      return removedModifiers;
   }
}
