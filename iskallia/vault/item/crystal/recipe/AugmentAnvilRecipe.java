package iskallia.vault.item.crystal.recipe;

import iskallia.vault.core.data.key.ThemeKey;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.AugmentItem;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.crystal.VaultCrystalItem;
import iskallia.vault.item.crystal.theme.ValueCrystalTheme;
import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.world.item.ItemStack;

public class AugmentAnvilRecipe extends VanillaAnvilRecipe {
   @Override
   public boolean onSimpleCraft(AnvilContext context) {
      ItemStack primary = context.getInput()[0];
      ItemStack secondary = context.getInput()[1];
      if (primary.getItem() == ModItems.VAULT_CRYSTAL && secondary.getItem() == ModItems.AUGMENT) {
         ItemStack output = primary.copy();
         CrystalData crystal = CrystalData.read(output);
         ThemeKey theme = AugmentItem.getTheme(secondary).orElse(null);
         if (!crystal.getProperties().isUnmodifiable() && theme != null) {
            crystal.setTheme(new ValueCrystalTheme(theme.getId()));
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
      IVanillaRecipeFactory factory = registry.getVanillaRecipeFactory();
      List<ItemStack> primary = new ArrayList<>();
      List<ItemStack> secondary = new ArrayList<>();
      List<ItemStack> output = new ArrayList<>();
      primary.add(VaultCrystalItem.create(data -> {}));

      for (ThemeKey theme : VaultRegistry.THEME.getKeys()) {
         secondary.add(AugmentItem.create(theme.getId()));
         output.add(VaultCrystalItem.create(data -> data.setTheme(new ValueCrystalTheme(theme.getId()))));
      }

      registry.addRecipes(RecipeTypes.ANVIL, List.of(factory.createAnvilRecipe(primary, secondary, output)));
   }
}
