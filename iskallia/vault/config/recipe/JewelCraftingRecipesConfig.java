package iskallia.vault.config.recipe;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.recipe.ConfigJewelCraftingRecipe;
import iskallia.vault.gear.crafting.recipe.JewelCraftingRecipe;
import iskallia.vault.init.ModGearAttributes;
import java.util.ArrayList;
import java.util.List;

public class JewelCraftingRecipesConfig extends ForgeRecipesConfig<ConfigJewelCraftingRecipe, JewelCraftingRecipe> {
   @Expose
   private final List<ConfigJewelCraftingRecipe> jewelCraftingRecipes = new ArrayList<>();

   public JewelCraftingRecipesConfig() {
      super(ForgeRecipeType.JEWEL_CRAFTING);
   }

   @Override
   protected void reset() {
      this.jewelCraftingRecipes.clear();
      this.jewelCraftingRecipes.add(new ConfigJewelCraftingRecipe(ModGearAttributes.COIN_AFFINITY.getRegistryName(), 10));
   }

   @Override
   public List<ConfigJewelCraftingRecipe> getConfigRecipes() {
      return this.jewelCraftingRecipes;
   }
}
