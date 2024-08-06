package iskallia.vault.integration.jei;

import iskallia.vault.VaultMod;
import iskallia.vault.client.gui.screen.block.SkillAltarScreen;
import iskallia.vault.client.gui.screen.player.AbstractSkillTabElementContainerScreen;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import iskallia.vault.integration.jei.lootinfo.LootInfoFactory;
import iskallia.vault.integration.jei.lootinfo.LootInfoGroupDefinition;
import iskallia.vault.integration.jei.lootinfo.LootInfoGroupDefinitionRegistry;
import iskallia.vault.integration.jei.lootinfo.LootInfoRecipeCategory;
import iskallia.vault.item.crystal.recipe.AnvilRecipes;
import iskallia.vault.item.tool.Pulverizing;
import iskallia.vault.item.tool.Smelting;
import iskallia.vault.item.tool.ToolType;
import iskallia.vault.recipe.CatalystInfusionTableRecipe;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
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
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
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
            ModBlocks.PLACEHOLDER.asItem(),
            ModBlocks.HERALD_TROPHY_BLOCK_ITEM,
            ModBlocks.SOUL_PLAQUE_BLOCK_ITEM,
            ModBlocks.GOD_ALTAR.asItem(),
            ModItems.TITLE_SCROLL,
            ModItems.ANTIQUE
         }
      );
      registration.registerSubtypeInterpreter(
         VanillaTypes.ITEM_STACK,
         ModItems.BOTTLE,
         (stack, context) -> stack.getOrCreateTag().getString("type") + "_" + stack.getOrCreateTag().getString("recharge")
      );
      registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.JEWEL, (stack, context) -> {
         VaultGearData data = VaultGearData.read(stack);
         Set<String> modifiers = new HashSet<>();

         for (VaultGearModifier<?> modifier : data.getAllModifierAffixes()) {
            modifiers.add(modifier.getAttribute().getRegistryName().toString());
         }

         List<String> sorted = new ArrayList<>(modifiers);
         Collections.sort(sorted);
         return String.join(".", sorted);
      });
      registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.TOOL, (stack, context) -> {
         ToolType type = ToolType.of(stack);
         return type == null ? "" : type.getId();
      });
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
      AnvilRecipes.registerJEI(registration);
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
      registration.addGenericGuiContainerHandler(SkillAltarScreen.Default.class, new IGuiContainerHandler<AbstractContainerScreen<?>>() {
         public List<Rect2i> getGuiExtraAreas(AbstractContainerScreen<?> containerScreen) {
            return List.of(new Rect2i(containerScreen.getGuiLeft() + containerScreen.getXSize(), containerScreen.getGuiTop() + 7, 18, 62));
         }
      });
      registration.addGenericGuiContainerHandler(SkillAltarScreen.Import.class, new IGuiContainerHandler<AbstractContainerScreen<?>>() {
         public List<Rect2i> getGuiExtraAreas(AbstractContainerScreen<?> containerScreen) {
            return List.of(new Rect2i(containerScreen.getGuiLeft() + containerScreen.getXSize(), containerScreen.getGuiTop() + 44, 18, 18));
         }
      });
   }

   @Nonnull
   public ResourceLocation getPluginUid() {
      return VaultMod.id("jei_integration");
   }
}
