package iskallia.vault.item.tool;

import iskallia.vault.VaultMod;
import iskallia.vault.core.Version;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.world.loot.LootTable;
import iskallia.vault.core.world.loot.entry.ItemLootEntry;
import iskallia.vault.core.world.loot.generator.LootTableGenerator;
import iskallia.vault.core.world.roll.IntRoll;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;

public class Pulverizing {
   public static void handle(List<ItemStack> loot) {
      for (int i = loot.size() - 1; i >= 0; i--) {
         ItemStack raw = loot.get(i);
         LootTable table = ModConfigs.TOOL_PULVERIZING.get(raw.getItem());
         if (table != null) {
            loot.remove(i);
            LootTableGenerator generator = new LootTableGenerator(Version.latest(), table, 0.0F);
            generator.generate(JavaRandom.ofNanoTime());
            generator.getItems().forEachRemaining(pulverized -> {
               pulverized.setCount(raw.getCount() * pulverized.getCount());
               loot.add(pulverized);
            });
         }
      }
   }

   public static void register(IRecipeCategoryRegistration registration) {
      registration.addRecipeCategories(new IRecipeCategory[]{new Pulverizing.Category(registration.getJeiHelpers().getGuiHelper())});
   }

   public static void register(IRecipeRegistration registration) {
      registration.addRecipes(Pulverizing.Category.RECIPE_TYPE, ModConfigs.TOOL_PULVERIZING.getLoot().entrySet().stream().flatMap(entry -> {
         Item item = (Item)ForgeRegistries.ITEMS.getValue(entry.getKey());
         return item != null && item != Items.AIR ? Stream.of(new Pulverizing.Recipe(new ItemStack(item), new LootTable(entry.getValue()))) : Stream.empty();
      }).toList());
   }

   public static void register(IRecipeCatalystRegistration registration) {
      ItemStack tool = new ItemStack(ModItems.TOOL);
      VaultGearData data = VaultGearData.read(tool);
      data.setState(VaultGearState.IDENTIFIED);
      data.addModifier(VaultGearModifier.AffixType.SUFFIX, new VaultGearModifier<>(ModGearAttributes.PULVERIZING, true));
      data.write(tool);
      registration.addRecipeCatalyst(tool, new RecipeType[]{Pulverizing.Category.RECIPE_TYPE});
   }

   private static class Category implements IRecipeCategory<Pulverizing.Recipe> {
      public static final RecipeType<Pulverizing.Recipe> RECIPE_TYPE = RecipeType.create("the_vault", "pulverizing", Pulverizing.Recipe.class);
      private static final ResourceLocation TEXTURE = VaultMod.id("textures/gui/vault_recycler_jei.png");
      private final IDrawable background;
      private final IDrawable icon;

      public Category(IGuiHelper guiHelper) {
         this.background = guiHelper.createDrawable(TEXTURE, 33, 30, 104, 26);
         this.icon = guiHelper.createDrawableIngredient(
            VanillaTypes.ITEM_STACK,
            JewelItem.create(data -> data.addModifier(VaultGearModifier.AffixType.PREFIX, new VaultGearModifier<>(ModGearAttributes.PULVERIZING, true)))
         );
      }

      @Nonnull
      public Component getTitle() {
         return new TextComponent("Pulverizing");
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
      public void setRecipe(IRecipeLayoutBuilder builder, Pulverizing.Recipe recipe, IFocusGroup focuses) {
         builder.addSlot(RecipeIngredientRole.INPUT, 1, 5).addItemStack(recipe.getInput());

         for (LootTable.Entry entry : recipe.getOutput().getEntries()) {
            WeightedList<ItemLootEntry> entries = new WeightedList<>();
            entry.getPool().getChildren().forEach((loot, weightx) -> {
               if (loot instanceof ItemLootEntry) {
                  entries.put((ItemLootEntry)loot, weightx);
               }
            });
            int index = 0;

            for (Entry<ItemLootEntry, Double> e : entries.entrySet()) {
               ItemLootEntry item = e.getKey();
               double weight = e.getValue();
               ItemStack min = new ItemStack(item.getItem());
               ItemStack max = new ItemStack(item.getItem());
               min.setCount(IntRoll.getMin(item.getCount()));
               max.setCount(IntRoll.getMax(item.getCount()));
               if (item.getNbt() != null) {
                  min.setTag(item.getNbt().copy());
                  max.setTag(item.getNbt().copy());
               }

               int probability = (int)Math.round(weight / entries.getTotalWeight() * 100.0);
               min.setHoverName(min.getHoverName().copy().append(new TextComponent(" (%d%%)".formatted(probability))));
               max.setHoverName(max.getHoverName().copy().append(new TextComponent(" (%d%%)".formatted(probability))));
               builder.addSlot(RecipeIngredientRole.OUTPUT, 49 + index++ * 18, 5).addIngredients(Ingredient.of(new ItemStack[]{min, max}));
            }
         }
      }

      @Nonnull
      public RecipeType<Pulverizing.Recipe> getRecipeType() {
         return RECIPE_TYPE;
      }

      @Nonnull
      public ResourceLocation getUid() {
         return this.getRecipeType().getUid();
      }

      @Nonnull
      public Class<? extends Pulverizing.Recipe> getRecipeClass() {
         return this.getRecipeType().getRecipeClass();
      }
   }

   private static class Recipe {
      private final ItemStack input;
      private final LootTable output;

      public Recipe(ItemStack input, LootTable output) {
         this.input = input;
         this.output = output;
      }

      public ItemStack getInput() {
         return this.input;
      }

      public LootTable getOutput() {
         return this.output;
      }
   }
}
