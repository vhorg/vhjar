package iskallia.vault.task.renderer;

import com.google.gson.JsonObject;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.task.CraftingTask;
import iskallia.vault.task.renderer.context.AchievementRendererContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CraftingTaskRenderer {
   public static class Achievement extends AchievementRenderer.Base<CraftingTask, AchievementRendererContext> {
      protected List<Recipe<?>> cache = null;

      public List<Recipe<?>> getRecipes(CraftingTask task) {
         if (this.cache == null) {
            this.cache = new ArrayList<>();

            for (Recipe<?> recipe : this.getRecipeManager().getRecipes()) {
               if (task.getConfig().filter.test(recipe.getResultItem())) {
                  this.cache.add(recipe);
               }
            }
         }

         return this.cache;
      }

      @OnlyIn(Dist.CLIENT)
      public void onRenderDetails(CraftingTask task, AchievementRendererContext context) {
         super.onRenderDetails(task, context);
         int height = 64;
         int backgroundWidth = (int)context.getSize().getX() - 3;
         context.drawNineSlice(ScreenTextures.DEFAULT_WINDOW_BACKGROUND, 0, 0, backgroundWidth, height);
         List<Recipe<?>> recipes = this.getRecipes(task);
         if (!recipes.isEmpty()) {
            context.push();
            context.translate(backgroundWidth / 2.0 - 45.0, 5.0, 0.0);
            Recipe<?> recipe = recipes.get(0);
            RecipeType<?> type = recipe.getType();
            if (type == RecipeType.CRAFTING) {
               this.renderSlots(context);
               if (recipe instanceof ShapedRecipe shapedRecipe) {
                  this.renderShaped(context, shapedRecipe);
               }

               if (recipe instanceof ShapelessRecipe shapeless) {
                  this.renderShapeless(context, shapeless);
               }

               ItemStack resultItem = recipe.getResultItem();
               if (resultItem.isEmpty()) {
                  return;
               }

               this.renderResult(context, resultItem);
            }

            context.pop();
         }

         context.translate(0.0, height + 2, 0.0);
      }

      @OnlyIn(Dist.CLIENT)
      private void renderResult(AchievementRendererContext context, ItemStack resultItem) {
         int x = 72;
         int y = 18;
         context.push();
         context.translate(x, y, 0.0);
         context.renderStack(resultItem, 0, 0, 1.0F, true, true);
         context.pop();
      }

      @OnlyIn(Dist.CLIENT)
      private void renderShapeless(AchievementRendererContext context, ShapelessRecipe shapeless) {
         NonNullList<Ingredient> ingredients = shapeless.getIngredients();
         boolean large = ingredients.size() > 4;
         int index = 0;
         context.push();

         for (int column = 0; column < (large ? 3 : 2); column++) {
            context.push();

            for (int row = 0; row < (large ? 3 : 2); row++) {
               if (ingredients.size() > index) {
                  if (!ingredients.isEmpty() && ((Ingredient)ingredients.get(index)).getItems().length > 0) {
                     ItemStack stack = ((Ingredient)ingredients.get(index)).getItems()[0];
                     context.renderStack(stack, 0, 0, 1.0F, true, true);
                     context.translate(18.0, 0.0, 0.0);
                  }

                  index++;
               }
            }

            context.pop();
            context.translate(0.0, 18.0, 0.0);
         }

         context.pop();
      }

      @OnlyIn(Dist.CLIENT)
      private void renderSlots(AchievementRendererContext context) {
         context.push();

         for (int row = 0; row < 3; row++) {
            context.push();

            for (int column = 0; column < 3; column++) {
               context.renderStack(ItemStack.EMPTY, 0, 0, 1.0F, true, false);
               context.translate(0.0, 18.0, 0.0);
            }

            context.pop();
            context.translate(18.0, 0.0, 0.0);
         }

         context.pop();
      }

      @OnlyIn(Dist.CLIENT)
      private void renderShaped(AchievementRendererContext context, ShapedRecipe shapedRecipe) {
         int width = shapedRecipe.getWidth();
         int height = shapedRecipe.getHeight();
         NonNullList<Ingredient> ingredients = shapedRecipe.getIngredients();
         int index = 0;
         context.push();

         for (int row = 0; row < height; row++) {
            context.push();

            for (int column = 0; column < width; column++) {
               if (!ingredients.isEmpty()) {
                  if (!((Ingredient)ingredients.get(index)).isEmpty()) {
                     ItemStack stack = ((Ingredient)ingredients.get(index)).getItems()[0];
                     context.renderStack(stack, 0, 0, 1.0F, true, true);
                  }

                  context.translate(18.0, 0.0, 0.0);
               }

               index++;
            }

            context.pop();
            context.translate(0.0, 18.0, 0.0);
         }

         context.pop();
      }

      protected RecipeManager getRecipeManager() {
         ClientLevel level = Minecraft.getInstance().level;
         if (level == null) {
            throw new IllegalStateException("Level is null when attempting to instantiate RecipeRenderer");
         } else {
            return level.getRecipeManager();
         }
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         super.writeBits(buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         super.readBits(buffer);
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return super.writeNbt();
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         super.readNbt(nbt);
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return super.writeJson();
      }

      @Override
      public void readJson(JsonObject json) {
         super.readJson(json);
      }
   }
}
