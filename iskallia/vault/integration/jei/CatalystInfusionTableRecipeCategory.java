package iskallia.vault.integration.jei;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.recipe.CatalystInfusionTableRecipe;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class CatalystInfusionTableRecipeCategory implements IRecipeCategory<CatalystInfusionTableRecipe> {
   public static final RecipeType<CatalystInfusionTableRecipe> RECIPE_TYPE = RecipeType.create(
      "the_vault", "catalyst_infusion_table", CatalystInfusionTableRecipe.class
   );
   private static final ResourceLocation TEXTURE = VaultMod.id("textures/gui/catalyst_infusion_table.png");
   private final IDrawable background;
   private final IDrawable icon;
   private final LoadingCache<Integer, IDrawableAnimated> cachedArrows;

   public CatalystInfusionTableRecipeCategory(final IGuiHelper guiHelper) {
      this.background = guiHelper.createDrawable(TEXTURE, 33, 30, 104, 26);
      this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.CATALYST_INFUSION_TABLE));
      this.cachedArrows = CacheBuilder.newBuilder().maximumSize(25L).build(new CacheLoader<Integer, IDrawableAnimated>() {
         @Nonnull
         public IDrawableAnimated load(@Nonnull Integer time) {
            return guiHelper.drawableBuilder(CatalystInfusionTableRecipeCategory.TEXTURE, 176, 0, 24, 17).buildAnimated(time, StartDirection.LEFT, false);
         }
      });
   }

   @Nonnull
   public Component getTitle() {
      return ModBlocks.CATALYST_INFUSION_TABLE.getName();
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
   public void draw(CatalystInfusionTableRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack poseStack, double mouseX, double mouseY) {
      IDrawableAnimated arrow = (IDrawableAnimated)this.cachedArrows.getUnchecked(ModConfigs.CATALYST_INFUSION_TABLE.getInfusionTimeTicks());
      arrow.draw(poseStack, 47, 4);
   }

   @ParametersAreNonnullByDefault
   public void setRecipe(IRecipeLayoutBuilder builder, CatalystInfusionTableRecipe recipe, IFocusGroup focuses) {
      builder.addSlot(RecipeIngredientRole.INPUT, 1, 5).addIngredients(recipe.getInfusionIngredient());
      builder.addSlot(RecipeIngredientRole.INPUT, 23, 5).addIngredients(recipe.getCatalystIngredient());
      builder.addSlot(RecipeIngredientRole.OUTPUT, 83, 5).addItemStack(recipe.getOutput());
   }

   @Nonnull
   public RecipeType<CatalystInfusionTableRecipe> getRecipeType() {
      return RECIPE_TYPE;
   }

   @Nonnull
   public ResourceLocation getUid() {
      return this.getRecipeType().getUid();
   }

   @Nonnull
   public Class<? extends CatalystInfusionTableRecipe> getRecipeClass() {
      return this.getRecipeType().getRecipeClass();
   }
}
