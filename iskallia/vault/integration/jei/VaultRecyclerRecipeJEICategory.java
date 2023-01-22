package iskallia.vault.integration.jei;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.gear.TrinketItem;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class VaultRecyclerRecipeJEICategory implements IRecipeCategory<VaultRecyclerRecipeJEI> {
   public static final RecipeType<VaultRecyclerRecipeJEI> RECIPE_TYPE = RecipeType.create("the_vault", "vault_recycler", VaultRecyclerRecipeJEI.class);
   private static final ResourceLocation TEXTURE = VaultMod.id("textures/gui/vault_recycler_jei.png");
   private final IDrawable background;
   private final IDrawable icon;
   private final LoadingCache<Integer, IDrawableAnimated> cachedArrows;

   public VaultRecyclerRecipeJEICategory(final IGuiHelper guiHelper) {
      this.background = guiHelper.createDrawable(TEXTURE, 33, 30, 104, 26);
      this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.VAULT_RECYCLER));
      this.cachedArrows = CacheBuilder.newBuilder().maximumSize(25L).build(new CacheLoader<Integer, IDrawableAnimated>() {
         @Nonnull
         public IDrawableAnimated load(@Nonnull Integer time) {
            return guiHelper.drawableBuilder(VaultRecyclerRecipeJEICategory.TEXTURE, 176, 0, 24, 17).buildAnimated(time, StartDirection.LEFT, false);
         }
      });
   }

   @Nonnull
   public Component getTitle() {
      return ModBlocks.VAULT_RECYCLER.getName();
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
   public void draw(VaultRecyclerRecipeJEI recipe, IRecipeSlotsView recipeSlotsView, PoseStack poseStack, double mouseX, double mouseY) {
      IDrawableAnimated arrow = (IDrawableAnimated)this.cachedArrows.getUnchecked(ModConfigs.VAULT_RECYCLER.getProcessingTickTime());
      arrow.draw(poseStack, 21, 4);
   }

   @ParametersAreNonnullByDefault
   public void setRecipe(IRecipeLayoutBuilder builder, VaultRecyclerRecipeJEI recipe, IFocusGroup focuses) {
      boolean isTrinket = recipe.getInput().getItem() instanceof TrinketItem;
      ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.INPUT, 1, 5).addItemStack(recipe.getInput()))
         .addTooltipCallback(addTooltip(List.of(new TextComponent("Output item's chance and quantity is based off this items quality"))));
      if (!isTrinket) {
         ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.OUTPUT, 49, 5).addItemStack(recipe.getOutput1()))
            .addTooltipCallback(addTooltip(VaultRecyclerRecipeJEI.getRelatedTooltip(recipe.getInput(), ModConfigs.VAULT_RECYCLER.getGearRecyclingOutput(), 0)));
         ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.OUTPUT, 67, 5).addItemStack(recipe.getOutput2()))
            .addTooltipCallback(addTooltip(VaultRecyclerRecipeJEI.getRelatedTooltip(recipe.getInput(), ModConfigs.VAULT_RECYCLER.getGearRecyclingOutput(), 1)));
         ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.OUTPUT, 85, 5).addItemStack(recipe.getOutput3()))
            .addTooltipCallback(addTooltip(VaultRecyclerRecipeJEI.getRelatedTooltip(recipe.getInput(), ModConfigs.VAULT_RECYCLER.getGearRecyclingOutput(), 2)));
      } else {
         ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.OUTPUT, 49, 5).addItemStack(recipe.getOutput1()))
            .addTooltipCallback(
               addTooltip(VaultRecyclerRecipeJEI.getRelatedTooltip(recipe.getInput(), ModConfigs.VAULT_RECYCLER.getTrinketRecyclingOutput(), 0))
            );
         ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.OUTPUT, 67, 5).addItemStack(recipe.getOutput2()))
            .addTooltipCallback(
               addTooltip(VaultRecyclerRecipeJEI.getRelatedTooltip(recipe.getInput(), ModConfigs.VAULT_RECYCLER.getTrinketRecyclingOutput(), 1))
            );
         ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.OUTPUT, 85, 5).addItemStack(recipe.getOutput3()))
            .addTooltipCallback(
               addTooltip(VaultRecyclerRecipeJEI.getRelatedTooltip(recipe.getInput(), ModConfigs.VAULT_RECYCLER.getTrinketRecyclingOutput(), 2))
            );
      }
   }

   @Nonnull
   public RecipeType<VaultRecyclerRecipeJEI> getRecipeType() {
      return RECIPE_TYPE;
   }

   @Nonnull
   public ResourceLocation getUid() {
      return this.getRecipeType().getUid();
   }

   @Nonnull
   public Class<? extends VaultRecyclerRecipeJEI> getRecipeClass() {
      return this.getRecipeType().getRecipeClass();
   }

   public static IRecipeSlotTooltipCallback addTooltip(List<Component> list) {
      return (view, tooltip) -> tooltip.addAll(list);
   }
}
