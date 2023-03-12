package iskallia.vault.config.recipe;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.recipe.ConfigInscriptionRecipe;
import iskallia.vault.gear.crafting.recipe.InscriptionForgeRecipe;
import java.util.ArrayList;
import java.util.List;

public class InscriptionRecipesConfig extends ForgeRecipesConfig<ConfigInscriptionRecipe, InscriptionForgeRecipe> {
   @Expose
   private final List<ConfigInscriptionRecipe> inscriptionRecipes = new ArrayList<>();

   public InscriptionRecipesConfig() {
      super(ForgeRecipeType.INSCRIPTION);
   }

   @Override
   protected void reset() {
      this.inscriptionRecipes.clear();
      this.inscriptionRecipes.add(new ConfigInscriptionRecipe());
   }

   @Override
   protected List<ConfigInscriptionRecipe> getConfigRecipes() {
      return this.inscriptionRecipes;
   }
}
