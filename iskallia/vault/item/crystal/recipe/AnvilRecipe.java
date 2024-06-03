package iskallia.vault.item.crystal.recipe;

import mezz.jei.api.registration.IRecipeRegistration;

public abstract class AnvilRecipe {
   public abstract boolean onCraft(AnvilContext var1);

   public abstract void onRegisterJEI(IRecipeRegistration var1);
}
