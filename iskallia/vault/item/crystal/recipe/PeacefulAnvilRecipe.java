package iskallia.vault.item.crystal.recipe;

import iskallia.vault.VaultMod;
import iskallia.vault.core.vault.modifier.VaultModifierStack;
import iskallia.vault.core.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.crystal.CrystalData;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.world.item.ItemStack;

public class PeacefulAnvilRecipe extends VanillaAnvilRecipe {
   @Override
   public boolean onSimpleCraft(AnvilContext context) {
      ItemStack primary = context.getInput()[0];
      ItemStack secondary = context.getInput()[1];
      if (primary.getItem() == ModItems.VAULT_CRYSTAL && secondary.getItem() == ModItems.PEACEFUL_CAPSTONE) {
         ItemStack output = primary.copy();
         CrystalData crystal = CrystalData.read(output);
         VaultModifierRegistry.getOpt(VaultMod.id("peaceful")).ifPresent(modifier -> {
            VaultModifierStack modifierStack = VaultModifierStack.of((VaultModifier<?>)modifier);
            boolean hasPhoenix = false;

            for (VaultModifierStack stack : crystal.getModifiers()) {
               if (stack.getModifier() == modifier) {
                  hasPhoenix = true;
                  break;
               }
            }

            if (hasPhoenix) {
               crystal.getProperties().setUnmodifiable(false);
            }

            if (crystal.addModifierByCrafting(modifierStack, false, true)) {
               crystal.addModifierByCrafting(modifierStack, false, false);
               crystal.getProperties().setUnmodifiable(true);
            }
         });
         crystal.write(output);
         context.setOutput(output);
         context.onTake(context.getTake().append(() -> {
            context.getInput()[0].shrink(1);
            context.getInput()[1].shrink(1);
         }));
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void onRegisterJEI(IRecipeRegistration registry) {
   }
}
