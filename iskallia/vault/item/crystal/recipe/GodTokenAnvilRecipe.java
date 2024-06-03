package iskallia.vault.item.crystal.recipe;

import iskallia.vault.VaultMod;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.core.vault.modifier.VaultModifierStack;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.GodTokenItem;
import iskallia.vault.item.crystal.CrystalData;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.world.item.ItemStack;

public class GodTokenAnvilRecipe extends VanillaAnvilRecipe {
   @Override
   public boolean onSimpleCraft(AnvilContext context) {
      ItemStack primary = context.getInput()[0];
      ItemStack secondary = context.getInput()[1];
      if (primary.getItem() == ModItems.VAULT_CRYSTAL && secondary.getItem() == ModItems.GOD_TOKEN) {
         ItemStack output = primary.copy();
         CrystalData crystal = CrystalData.read(output);
         VaultGod god = GodTokenItem.getGod(secondary);
         Integer level = crystal.getProperties().getLevel().orElse(null);
         if (god != null && level != null) {
            for (VaultModifier<?> modifier : ModConfigs.VAULT_MODIFIER_POOLS
               .getRandom(VaultMod.id("god_token_" + god.getName().toLowerCase()), level, JavaRandom.ofNanoTime())) {
               VaultModifierStack modifierStack = VaultModifierStack.of(modifier);
               if (crystal.addModifierByCrafting(modifierStack, false, true)) {
                  crystal.addModifierByCrafting(modifierStack, false, false);
               }
            }

            crystal.getProperties().setUnmodifiable(true);
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
      } else {
         return false;
      }
   }

   @Override
   public void onRegisterJEI(IRecipeRegistration registry) {
   }
}
