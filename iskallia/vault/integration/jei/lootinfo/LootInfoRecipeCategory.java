package iskallia.vault.integration.jei.lootinfo;

import iskallia.vault.VaultMod;
import java.util.List;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class LootInfoRecipeCategory implements IRecipeCategory<LootInfo> {
   private static final ResourceLocation TEXTURE = VaultMod.id("textures/gui/jei/loot_info.png");
   private final RecipeType<LootInfo> recipeType;
   private final Component titleComponent;
   private final IDrawable background;
   private final IDrawable icon;

   public LootInfoRecipeCategory(IGuiHelper guiHelper, RecipeType<LootInfo> recipeType, ItemStack iconItemStack, Component titleComponent) {
      this.recipeType = recipeType;
      this.titleComponent = titleComponent;
      this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 162, 108);
      this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, iconItemStack);
   }

   @Nonnull
   public Component getTitle() {
      return this.titleComponent;
   }

   @Nonnull
   public IDrawable getBackground() {
      return this.background;
   }

   @Nonnull
   public IDrawable getIcon() {
      return this.icon;
   }

   @Nonnull
   public RecipeType<LootInfo> getRecipeType() {
      return this.recipeType;
   }

   @Nonnull
   public ResourceLocation getUid() {
      return this.getRecipeType().getUid();
   }

   @Nonnull
   public Class<? extends LootInfo> getRecipeClass() {
      return this.getRecipeType().getRecipeClass();
   }

   @ParametersAreNonnullByDefault
   public void setRecipe(IRecipeLayoutBuilder builder, LootInfo recipe, IFocusGroup focuses) {
      List<ItemStack> itemList = recipe.itemStackList();
      int count = itemList.size();

      for (int i = 0; i < count; i++) {
         builder.addSlot(RecipeIngredientRole.OUTPUT, 1 + 18 * (i % 9), 1 + 18 * (i / 9)).addItemStack(itemList.get(i));
      }
   }
}
