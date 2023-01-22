package iskallia.vault.integration.jei;

import iskallia.vault.VaultMod;
import iskallia.vault.client.gui.screen.player.AbstractSkillTabElementContainerScreen;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import iskallia.vault.integration.jei.lootinfo.LootInfoFactory;
import iskallia.vault.integration.jei.lootinfo.LootInfoGroupDefinition;
import iskallia.vault.integration.jei.lootinfo.LootInfoGroupDefinitionRegistry;
import iskallia.vault.integration.jei.lootinfo.LootInfoRecipeCategory;
import iskallia.vault.recipe.CatalystInfusionTableRecipe;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

@JeiPlugin
public class IntegrationJEI implements IModPlugin {
   public void registerItemSubtypes(ISubtypeRegistration registration) {
      registration.useNbtForSubtypes(new Item[]{ModItems.RESPEC_FLASK, ModItems.FACETED_FOCUS, ModItems.TRINKET, ModItems.ETCHING});
   }

   public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
      registration.addRecipeCatalyst(new ItemStack(ModBlocks.CATALYST_INFUSION_TABLE), new RecipeType[]{CatalystInfusionTableRecipeCategory.RECIPE_TYPE});
      registration.addRecipeCatalyst(new ItemStack(ModBlocks.VAULT_RECYCLER), new RecipeType[]{VaultRecyclerRecipeJEICategory.RECIPE_TYPE});

      for (LootInfoGroupDefinition definition : LootInfoGroupDefinitionRegistry.get().values()) {
         registration.addRecipeCatalyst(definition.itemStack(), new RecipeType[]{definition.recipeType()});
      }
   }

   public void registerCategories(IRecipeCategoryRegistration registration) {
      IJeiHelpers jeiHelpers = registration.getJeiHelpers();
      IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
      registration.addRecipeCategories(new IRecipeCategory[]{new CatalystInfusionTableRecipeCategory(guiHelper)});
      registration.addRecipeCategories(new IRecipeCategory[]{new VaultRecyclerRecipeJEICategory(guiHelper)});

      for (LootInfoGroupDefinition definition : LootInfoGroupDefinitionRegistry.get().values()) {
         registration.addRecipeCategories(
            new IRecipeCategory[]{new LootInfoRecipeCategory(guiHelper, definition.recipeType(), definition.itemStack(), definition.titleComponent())}
         );
      }
   }

   public void registerRecipes(IRecipeRegistration registration) {
      RecipeManager rm = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
      registration.addRecipes(CatalystInfusionTableRecipeCategory.RECIPE_TYPE, List.of(new CatalystInfusionTableRecipe()));
      List<VaultRecyclerRecipeJEI> recipes = VaultRecyclerRecipeJEI.getRecipeList();
      registration.addRecipes(VaultRecyclerRecipeJEICategory.RECIPE_TYPE, recipes);
      LootInfoGroupDefinitionRegistry.get()
         .forEach((resourceLocation, definition) -> registration.addRecipes(definition.recipeType(), LootInfoFactory.create(resourceLocation)));
      IVanillaRecipeFactory vanillaRecipeFactory = registration.getVanillaRecipeFactory();
      registration.addRecipes(RecipeTypes.ANVIL, AnvilRecipesJEI.getAnvilRecipes(vanillaRecipeFactory));
   }

   public void registerGuiHandlers(IGuiHandlerRegistration registration) {
      registration.addGenericGuiContainerHandler(AbstractSkillTabElementContainerScreen.class, new RemoveJEIContainerHandler());
   }

   @Nonnull
   public ResourceLocation getPluginUid() {
      return VaultMod.id("jei_integration");
   }
}
