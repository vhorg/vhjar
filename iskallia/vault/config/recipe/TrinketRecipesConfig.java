package iskallia.vault.config.recipe;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.recipe.ConfigTrinketRecipe;
import iskallia.vault.gear.crafting.recipe.TrinketForgeRecipe;
import iskallia.vault.gear.trinket.TrinketEffect;
import iskallia.vault.gear.trinket.TrinketEffectRegistry;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TrinketRecipesConfig extends ForgeRecipesConfig<ConfigTrinketRecipe, TrinketForgeRecipe> {
   @Expose
   private final List<ConfigTrinketRecipe> trinketRecipes = new ArrayList<>();

   public TrinketRecipesConfig() {
      super(ForgeRecipeType.TRINKET);
   }

   @Override
   public List<ConfigTrinketRecipe> getConfigRecipes() {
      return this.trinketRecipes;
   }

   @Override
   protected void reset() {
      this.trinketRecipes.clear();

      for (TrinketEffect<?> trinket : TrinketEffectRegistry.getOrderedEntries()) {
         ConfigTrinketRecipe recipe = new ConfigTrinketRecipe(trinket);
         recipe.addInput(new ItemStack(Items.DIAMOND, 2));
         this.trinketRecipes.add(recipe);
      }
   }
}
