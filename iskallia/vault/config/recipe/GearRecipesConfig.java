package iskallia.vault.config.recipe;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.recipe.ConfigGearRecipe;
import iskallia.vault.gear.crafting.ProficiencyType;
import iskallia.vault.gear.crafting.recipe.GearForgeRecipe;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class GearRecipesConfig extends ForgeRecipesConfig<ConfigGearRecipe, GearForgeRecipe> {
   @Expose
   private final List<ConfigGearRecipe> gearRecipes = new ArrayList<>();

   public GearRecipesConfig() {
      super(ForgeRecipeType.GEAR);
   }

   @Override
   public List<ConfigGearRecipe> getConfigRecipes() {
      return this.gearRecipes;
   }

   @Override
   protected void reset() {
      this.gearRecipes.clear();

      for (ProficiencyType type : ProficiencyType.getCraftableTypes()) {
         ItemStack out = new ItemStack(type.getDisplayStack().get().getItem());
         ConfigGearRecipe recipe = new ConfigGearRecipe(out);
         recipe.addInput(new ItemStack(Items.DIAMOND, 2));
         this.gearRecipes.add(recipe);
      }
   }
}
