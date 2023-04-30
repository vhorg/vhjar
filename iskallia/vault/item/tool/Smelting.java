package iskallia.vault.item.tool;

import iskallia.vault.VaultMod;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;

public class Smelting {
   public static void handle(ServerLevel world, List<ItemStack> loot) {
      for (int i = loot.size() - 1; i >= 0; i--) {
         ItemStack raw = loot.get(i);
         Optional<SmeltingRecipe> recipe = world.getRecipeManager()
            .getRecipeFor(RecipeType.SMELTING, new SimpleContainer(new ItemStack[]{raw}), world.getLevel());
         if (recipe.isPresent()) {
            ItemStack smelted = recipe.get().getResultItem().copy();
            smelted.setCount(raw.getCount() * smelted.getCount());
            loot.set(i, smelted);
         }
      }
   }

   public static void register(IRecipeCategoryRegistration registration) {
      if (FMLEnvironment.dist == Dist.CLIENT) {
         registration.addRecipeCategories(new IRecipeCategory[]{new Smelting.Category(registration.getJeiHelpers().getGuiHelper())});
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void register(IRecipeRegistration registration) {
      ClientLevel world = Objects.requireNonNull(Minecraft.getInstance().level);
      List<Smelting.Recipe> recipes = new ArrayList<>();

      for (Item item : ForgeRegistries.ITEMS) {
         Optional<SmeltingRecipe> opt = world.getRecipeManager()
            .getRecipeFor(RecipeType.SMELTING, new SimpleContainer(new ItemStack[]{new ItemStack(item)}), world);
         opt.ifPresent(recipe -> recipes.add(new Smelting.Recipe(new ItemStack(item), recipe.getResultItem().copy())));
      }

      registration.addRecipes(Smelting.Category.RECIPE_TYPE, recipes);
   }

   public static void register(IRecipeCatalystRegistration registration) {
      ItemStack tool = new ItemStack(ModItems.TOOL);
      VaultGearData data = VaultGearData.read(tool);
      data.setState(VaultGearState.IDENTIFIED);
      data.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.SMELTING, true));
      data.write(tool);
      registration.addRecipeCatalyst(tool, new mezz.jei.api.recipe.RecipeType[]{Smelting.Category.RECIPE_TYPE});
   }

   private static class Category implements IRecipeCategory<Smelting.Recipe> {
      public static final mezz.jei.api.recipe.RecipeType<Smelting.Recipe> RECIPE_TYPE = mezz.jei.api.recipe.RecipeType.create(
         "the_vault", "smelting", Smelting.Recipe.class
      );
      private static final ResourceLocation TEXTURE = VaultMod.id("textures/gui/vault_recycler_jei.png");
      private final IDrawable background;
      private final IDrawable icon;

      public Category(IGuiHelper guiHelper) {
         this.background = guiHelper.createDrawable(TEXTURE, 33, 30, 104, 26);
         this.icon = guiHelper.createDrawableIngredient(
            VanillaTypes.ITEM_STACK,
            JewelItem.create(data -> data.addModifier(VaultGearModifier.AffixType.PREFIX, new VaultGearModifier<>(ModGearAttributes.SMELTING, true)))
         );
      }

      @Nonnull
      public Component getTitle() {
         return new TextComponent("Smelting");
      }

      @Nonnull
      public IDrawable getBackground() {
         return this.background;
      }

      @Nonnull
      public IDrawable getIcon() {
         return this.icon;
      }

      @ParametersAreNonnullByDefault
      public void setRecipe(IRecipeLayoutBuilder builder, Smelting.Recipe recipe, IFocusGroup focuses) {
         builder.addSlot(RecipeIngredientRole.INPUT, 1, 5).addItemStack(recipe.getInput());
         builder.addSlot(RecipeIngredientRole.OUTPUT, 49, 5).addItemStack(recipe.getOutput());
      }

      @Nonnull
      public mezz.jei.api.recipe.RecipeType<Smelting.Recipe> getRecipeType() {
         return RECIPE_TYPE;
      }

      @Nonnull
      public ResourceLocation getUid() {
         return this.getRecipeType().getUid();
      }

      @Nonnull
      public Class<? extends Smelting.Recipe> getRecipeClass() {
         return this.getRecipeType().getRecipeClass();
      }
   }

   private static class Recipe {
      private final ItemStack input;
      private final ItemStack output;

      public Recipe(ItemStack input, ItemStack output) {
         this.input = input;
         this.output = output;
      }

      public ItemStack getInput() {
         return this.input;
      }

      public ItemStack getOutput() {
         return this.output;
      }
   }
}
