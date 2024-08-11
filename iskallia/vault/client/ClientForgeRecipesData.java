package iskallia.vault.client;

import iskallia.vault.config.recipe.ForgeRecipeType;
import iskallia.vault.gear.crafting.recipe.VaultForgeRecipe;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientForgeRecipesData {
   private static final Map<ForgeRecipeType, List<VaultForgeRecipe>> recipes = new HashMap<>();

   public static List<VaultForgeRecipe> getRecipes(List<ForgeRecipeType> types) {
      List<VaultForgeRecipe> combinedRecipes = new ArrayList<>();

      for (ForgeRecipeType type : types) {
         combinedRecipes.addAll(recipes.getOrDefault(type, Collections.emptyList()));
      }

      return Collections.unmodifiableList(combinedRecipes);
   }

   public static void receiveMessage(List<? extends VaultForgeRecipe> msgRecipes, ForgeRecipeType type) {
      List<VaultForgeRecipe> recipeList = recipes.computeIfAbsent(type, t -> new ArrayList<>());
      recipeList.clear();
      recipeList.addAll(msgRecipes);
   }
}
