package iskallia.vault.config.recipe;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.config.entry.recipe.ConfigCatalystRecipe;
import iskallia.vault.gear.crafting.recipe.CatalystForgeRecipe;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CatalystRecipesConfig extends ForgeRecipesConfig<ConfigCatalystRecipe, CatalystForgeRecipe> {
   @Expose
   private final List<ConfigCatalystRecipe> catalystRecipes = new ArrayList<>();

   public CatalystRecipesConfig() {
      super(ForgeRecipeType.CATALYST);
   }

   @Override
   protected void reset() {
      this.catalystRecipes.clear();
      this.catalystRecipes
         .add(
            new ConfigCatalystRecipe(VaultMod.id("catalyst_wooden_cascade"), VaultMod.id("craft_wooden_cascade"), 15, 25, VaultMod.id("wooden_cascade"))
               .addInput(new ItemStack(Items.DIAMOND, 2))
         );
      this.catalystRecipes
         .add(
            new ConfigCatalystRecipe(VaultMod.id("catalyst_coin_cascade"), VaultMod.id("craft_coin_cascade"), 15, 25, VaultMod.id("coin_cascade"))
               .addInput(new ItemStack(Items.DIAMOND, 2))
         );
   }

   @Override
   public List<ConfigCatalystRecipe> getConfigRecipes() {
      return this.catalystRecipes;
   }
}
