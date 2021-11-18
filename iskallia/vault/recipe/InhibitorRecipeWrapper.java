package iskallia.vault.recipe;

import iskallia.vault.init.ModItems;
import java.util.Collections;
import java.util.List;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import net.minecraft.item.ItemStack;

public class InhibitorRecipeWrapper {
   public static List<Object> getRecipes(IVanillaRecipeFactory factory) {
      return Collections.singletonList(
         factory.createAnvilRecipe(
            new ItemStack(ModItems.PERFECT_ECHO_GEM),
            Collections.singletonList(new ItemStack(ModItems.VAULT_CATALYST)),
            Collections.singletonList(new ItemStack(ModItems.VAULT_INHIBITOR))
         )
      );
   }
}
