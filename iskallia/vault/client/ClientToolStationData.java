package iskallia.vault.client;

import iskallia.vault.gear.crafting.recipe.VaultForgeRecipe;
import java.util.ArrayList;
import java.util.List;

public class ClientToolStationData {
   private static final List<VaultForgeRecipe> recipes = new ArrayList<>();

   public static List<VaultForgeRecipe> getRecipes() {
      return recipes;
   }

   public static void receiveMessage(List<VaultForgeRecipe> msgRecipes) {
      recipes.clear();
      recipes.addAll(msgRecipes);
   }
}
