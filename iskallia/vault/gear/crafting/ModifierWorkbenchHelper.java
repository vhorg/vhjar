package iskallia.vault.gear.crafting;

import iskallia.vault.config.gear.VaultGearWorkbenchConfig;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.data.VaultGearData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.item.ItemStack;

public class ModifierWorkbenchHelper {
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

   public record CraftingOption(@Nullable VaultGearWorkbenchConfig.CraftableModifierConfig cfg) {
      public List<ItemStack> getCraftingCost(ItemStack input) {
         return this.cfg() == null
            ? VaultGearWorkbenchConfig.getConfig(input.getItem()).map(VaultGearWorkbenchConfig::getCostRemoveCraftedModifiers).orElse(Collections.emptyList())
            : this.cfg().createCraftingCost(input);
      }
   }
}
