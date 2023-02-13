package iskallia.vault.config.recipe;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.recipe.ConfigJewelRecipe;
import iskallia.vault.gear.crafting.recipe.JewelForgeRecipe;
import java.util.ArrayList;
import java.util.List;

public class JewelRecipesConfig extends ForgeRecipesConfig<ConfigJewelRecipe, JewelForgeRecipe> {
   @Expose
   private final List<ConfigJewelRecipe> jewelRecipes = new ArrayList<>();

   public JewelRecipesConfig() {
      super(ForgeRecipeType.JEWEL);
   }

   @Override
   protected void reset() {
      this.jewelRecipes.clear();
      this.jewelRecipes.add(new ConfigJewelRecipe());
   }

   @Override
   protected List<ConfigJewelRecipe> getConfigRecipes() {
      return this.jewelRecipes;
   }
}
