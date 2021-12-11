package iskallia.vault.integration.jei;

import iskallia.vault.Vault;
import iskallia.vault.init.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class IntegrationJEI implements IModPlugin {
   public void registerItemSubtypes(ISubtypeRegistration registration) {
      registration.useNbtForSubtypes(new Item[]{ModItems.RESPEC_FLASK, ModItems.RESET_FLASK});
   }

   public void registerRecipes(IRecipeRegistration registration) {
      IVanillaRecipeFactory recipeFactory = registration.getVanillaRecipeFactory();
      registration.addRecipes(DummyRecipeProvider.getAnvilRecipes(recipeFactory), VanillaRecipeCategoryUid.ANVIL);
      registration.addRecipes(DummyRecipeProvider.getCustomCraftingRecipes(recipeFactory), VanillaRecipeCategoryUid.CRAFTING);
   }

   public ResourceLocation getPluginUid() {
      return Vault.id("jei_integration");
   }
}
