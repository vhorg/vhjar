package iskallia.vault.item.crystal.recipe;

import mezz.jei.api.registration.IRecipeRegistration;

public class MoteAnvilRecipe extends VanillaAnvilRecipe {
   @Override
   public boolean onSimpleCraft(AnvilContext context) {
      return false;
   }

   @Override
   public void onRegisterJEI(IRecipeRegistration registry) {
   }
}
