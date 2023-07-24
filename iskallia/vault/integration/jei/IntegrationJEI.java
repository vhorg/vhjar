package iskallia.vault.integration.jei;

import iskallia.vault.VaultMod;
import iskallia.vault.client.gui.screen.player.AbstractSkillTabElementContainerScreen;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import iskallia.vault.integration.jei.lootinfo.LootInfoFactory;
import iskallia.vault.integration.jei.lootinfo.LootInfoGroupDefinition;
import iskallia.vault.integration.jei.lootinfo.LootInfoGroupDefinitionRegistry;
import iskallia.vault.integration.jei.lootinfo.LootInfoRecipeCategory;
import iskallia.vault.item.tool.Pulverizing;
import iskallia.vault.item.tool.Smelting;
import iskallia.vault.recipe.CatalystInfusionTableRecipe;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
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
import net.minecraftforge.registries.ForgeRegistries;

@JeiPlugin
public class IntegrationJEI implements IModPlugin {
   public void registerItemSubtypes(ISubtypeRegistration registration) {
      registration.useNbtForSubtypes(
         new Item[]{
            ModItems.RESPEC_FLASK,
            ModItems.FACETED_FOCUS,
            ModItems.TRINKET,
            ModItems.ETCHING,
            ModItems.GOD_BLESSING,
            ModItems.TOOL,
            ModItems.AUGMENT,
            ModBlocks.ASHIUM_ORE.asItem(),
            ModBlocks.ALEXANDRITE_ORE.asItem(),
            ModBlocks.BLACK_OPAL_ORE.asItem(),
            ModBlocks.BOMIGNITE_ORE.asItem(),
            ModBlocks.ECHO_ORE.asItem(),
            ModBlocks.GORGINITE_ORE.asItem(),
            ModBlocks.ISKALLIUM_ORE.asItem(),
            ModBlocks.LARIMAR_ORE.asItem(),
            ModBlocks.PAINITE_ORE.asItem(),
            ModBlocks.PETZANITE_ORE.asItem(),
            ModBlocks.PUFFIUM_ORE.asItem(),
            ModBlocks.SPARKLETINE_ORE.asItem(),
            ModBlocks.TUBIUM_ORE.asItem(),
            ModBlocks.UPALINE_ORE.asItem(),
            ModBlocks.WUTODIE_ORE.asItem(),
            ModBlocks.XENIUM_ORE.asItem(),
            ModBlocks.PLACEHOLDER.asItem()
         }
      );
      registration.registerSubtypeInterpreter(
         VanillaTypes.ITEM_STACK,
         ModItems.BOTTLE,
         (stack, context) -> stack.getOrCreateTag().getString("type") + "_" + stack.getOrCreateTag().getInt("charges")
      );
      Item item = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation("ispawner", "spawn_egg"));
      if (item != null) {
         registration.useNbtForSubtypes(new Item[]{item});
      }
   }

   public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
      registration.addRecipeCatalyst(new ItemStack(ModBlocks.CATALYST_INFUSION_TABLE), new RecipeType[]{CatalystInfusionTableRecipeCategory.RECIPE_TYPE});
      registration.addRecipeCatalyst(new ItemStack(ModBlocks.VAULT_RECYCLER), new RecipeType[]{VaultRecyclerRecipeJEICategory.RECIPE_TYPE});

      for (LootInfoGroupDefinition definition : LootInfoGroupDefinitionRegistry.get().values()) {
         registration.addRecipeCatalyst(definition.itemStack(), new RecipeType[]{definition.recipeType()});
      }

      Pulverizing.register(registration);
      Smelting.register(registration);
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

      Pulverizing.register(registration);
      Smelting.register(registration);
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
      Pulverizing.register(registration);
      Smelting.register(registration);
   }

   public void registerGuiHandlers(IGuiHandlerRegistration registration) {
      registration.addGenericGuiContainerHandler(AbstractSkillTabElementContainerScreen.class, new RemoveJEIContainerHandler());
   }

   @Nonnull
   public ResourceLocation getPluginUid() {
      return VaultMod.id("jei_integration");
   }
}
