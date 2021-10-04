package iskallia.vault.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class ShapelessCopyNbtRecipe extends ShapelessRecipe {
   public ShapelessCopyNbtRecipe(ResourceLocation idIn, String groupIn, ItemStack recipeOutputIn, NonNullList<Ingredient> recipeItemsIn) {
      super(idIn, groupIn, recipeOutputIn, recipeItemsIn);
   }

   public ItemStack func_77572_b(CraftingInventory inv) {
      ItemStack input = ItemStack.field_190927_a;

      for (int i = 0; i < inv.func_70302_i_(); i++) {
         ItemStack stack = inv.func_70301_a(i);
         if (!stack.func_190926_b()) {
            input = stack;
            break;
         }
      }

      if (input.func_190926_b()) {
         return this.func_77571_b();
      } else {
         ItemStack out = this.func_77571_b();
         out.func_77982_d(input.func_77978_p());
         return out;
      }
   }

   public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ShapelessCopyNbtRecipe> {
      public ShapelessCopyNbtRecipe read(ResourceLocation recipeId, JsonObject json) {
         String s = JSONUtils.func_151219_a(json, "group", "");
         NonNullList<Ingredient> nonnulllist = readIngredients(JSONUtils.func_151214_t(json, "ingredients"));
         if (nonnulllist.isEmpty()) {
            throw new JsonParseException("No ingredients for shapeless recipe");
         } else if (nonnulllist.size() > 1) {
            throw new JsonParseException("Too many ingredients for blank nbt copy recipe. The max is 1");
         } else {
            ItemStack itemstack = ShapedRecipe.func_199798_a(JSONUtils.func_152754_s(json, "result"));
            return new ShapelessCopyNbtRecipe(recipeId, s, itemstack, nonnulllist);
         }
      }

      private static NonNullList<Ingredient> readIngredients(JsonArray ingredientArray) {
         NonNullList<Ingredient> nonnulllist = NonNullList.func_191196_a();

         for (int i = 0; i < ingredientArray.size(); i++) {
            Ingredient ingredient = Ingredient.func_199802_a(ingredientArray.get(i));
            if (!ingredient.func_203189_d()) {
               nonnulllist.add(ingredient);
            }
         }

         return nonnulllist;
      }

      public ShapelessCopyNbtRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
         String s = buffer.func_150789_c(32767);
         int i = buffer.func_150792_a();
         NonNullList<Ingredient> nonnulllist = NonNullList.func_191197_a(i, Ingredient.field_193370_a);

         for (int j = 0; j < nonnulllist.size(); j++) {
            nonnulllist.set(j, Ingredient.func_199566_b(buffer));
         }

         ItemStack itemstack = buffer.func_150791_c();
         return new ShapelessCopyNbtRecipe(recipeId, s, itemstack, nonnulllist);
      }

      public void write(PacketBuffer buffer, ShapelessCopyNbtRecipe recipe) {
         buffer.func_180714_a(recipe.func_193358_e());
         buffer.func_150787_b(recipe.func_192400_c().size());

         for (Ingredient ingredient : recipe.func_192400_c()) {
            ingredient.func_199564_a(buffer);
         }

         buffer.func_150788_a(recipe.func_77571_b());
      }
   }
}
