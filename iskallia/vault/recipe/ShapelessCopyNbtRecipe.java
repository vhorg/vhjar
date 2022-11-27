package iskallia.vault.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class ShapelessCopyNbtRecipe extends ShapelessRecipe {
   public ShapelessCopyNbtRecipe(ResourceLocation idIn, String groupIn, ItemStack recipeOutputIn, NonNullList<Ingredient> recipeItemsIn) {
      super(idIn, groupIn, recipeOutputIn, recipeItemsIn);
   }

   public ItemStack assemble(CraftingContainer inv) {
      ItemStack input = ItemStack.EMPTY;

      for (int i = 0; i < inv.getContainerSize(); i++) {
         ItemStack stack = inv.getItem(i);
         if (!stack.isEmpty()) {
            input = stack;
            break;
         }
      }

      if (input.isEmpty()) {
         return this.getResultItem();
      } else {
         ItemStack out = this.getResultItem();
         out.setTag(input.getTag());
         return out;
      }
   }

   public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<ShapelessCopyNbtRecipe> {
      public ShapelessCopyNbtRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
         String s = GsonHelper.getAsString(json, "group", "");
         NonNullList<Ingredient> nonnulllist = readIngredients(GsonHelper.getAsJsonArray(json, "ingredients"));
         if (nonnulllist.isEmpty()) {
            throw new JsonParseException("No ingredients for shapeless recipe");
         } else if (nonnulllist.size() > 1) {
            throw new JsonParseException("Too many ingredients for blank nbt copy recipe. The max is 1");
         } else {
            ItemStack itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            return new ShapelessCopyNbtRecipe(recipeId, s, itemstack, nonnulllist);
         }
      }

      private static NonNullList<Ingredient> readIngredients(JsonArray ingredientArray) {
         NonNullList<Ingredient> nonnulllist = NonNullList.create();

         for (int i = 0; i < ingredientArray.size(); i++) {
            Ingredient ingredient = Ingredient.fromJson(ingredientArray.get(i));
            if (!ingredient.isEmpty()) {
               nonnulllist.add(ingredient);
            }
         }

         return nonnulllist;
      }

      public ShapelessCopyNbtRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
         String s = buffer.readUtf(32767);
         int i = buffer.readVarInt();
         NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);

         for (int j = 0; j < nonnulllist.size(); j++) {
            nonnulllist.set(j, Ingredient.fromNetwork(buffer));
         }

         ItemStack itemstack = buffer.readItem();
         return new ShapelessCopyNbtRecipe(recipeId, s, itemstack, nonnulllist);
      }

      public void toNetwork(FriendlyByteBuf buffer, ShapelessCopyNbtRecipe recipe) {
         buffer.writeUtf(recipe.getGroup());
         buffer.writeVarInt(recipe.getIngredients().size());

         for (Ingredient ingredient : recipe.getIngredients()) {
            ingredient.toNetwork(buffer);
         }

         buffer.writeItem(recipe.getResultItem());
      }
   }
}
