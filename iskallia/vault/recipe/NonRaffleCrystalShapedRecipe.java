package iskallia.vault.recipe;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.crystal.VaultCrystalItem;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class NonRaffleCrystalShapedRecipe extends ShapedRecipe {
   static int MAX_WIDTH = 3;
   static int MAX_HEIGHT = 3;

   public NonRaffleCrystalShapedRecipe(
      ResourceLocation idIn, String groupIn, int recipeWidthIn, int recipeHeightIn, NonNullList<Ingredient> recipeItemsIn, ItemStack recipeOutputIn
   ) {
      super(idIn, groupIn, recipeWidthIn, recipeHeightIn, recipeItemsIn, recipeOutputIn);
   }

   public boolean func_77569_a(CraftingInventory inv, World worldIn) {
      for (int i = 0; i < inv.func_70302_i_(); i++) {
         ItemStack stack = inv.func_70301_a(i);
         if (stack.func_77973_b() instanceof VaultCrystalItem) {
            CrystalData data = VaultCrystalItem.getData(stack);
            if (data.getType() == CrystalData.Type.RAFFLE) {
               return false;
            }
         }
      }

      return super.func_77569_a(inv, worldIn);
   }

   public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ShapedRecipe> {
      public ShapedRecipe read(ResourceLocation recipeId, JsonObject json) {
         String s = JSONUtils.func_151219_a(json, "group", "");
         Map<String, Ingredient> map = deserializeKey(JSONUtils.func_152754_s(json, "key"));
         String[] astring = shrink(patternFromJson(JSONUtils.func_151214_t(json, "pattern")));
         int i = astring[0].length();
         int j = astring.length;
         NonNullList<Ingredient> nonnulllist = deserializeIngredients(astring, map, i, j);
         ItemStack itemstack = ShapedRecipe.func_199798_a(JSONUtils.func_152754_s(json, "result"));
         return new NonRaffleCrystalShapedRecipe(recipeId, s, i, j, nonnulllist, itemstack);
      }

      @Nullable
      public ShapedRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
         int i = buffer.func_150792_a();
         int j = buffer.func_150792_a();
         String s = buffer.func_150789_c(32767);
         NonNullList<Ingredient> nonnulllist = NonNullList.func_191197_a(i * j, Ingredient.field_193370_a);

         for (int k = 0; k < nonnulllist.size(); k++) {
            nonnulllist.set(k, Ingredient.func_199566_b(buffer));
         }

         ItemStack itemstack = buffer.func_150791_c();
         return new NonRaffleCrystalShapedRecipe(recipeId, s, i, j, nonnulllist, itemstack);
      }

      public void write(PacketBuffer buffer, ShapedRecipe recipe) {
         buffer.func_150787_b(recipe.getRecipeWidth());
         buffer.func_150787_b(recipe.getRecipeHeight());
         buffer.func_180714_a(recipe.func_193358_e());

         for (Ingredient ingredient : recipe.func_192400_c()) {
            ingredient.func_199564_a(buffer);
         }

         buffer.func_150788_a(recipe.func_77571_b());
      }

      private static NonNullList<Ingredient> deserializeIngredients(String[] pattern, Map<String, Ingredient> keys, int patternWidth, int patternHeight) {
         NonNullList<Ingredient> nonnulllist = NonNullList.func_191197_a(patternWidth * patternHeight, Ingredient.field_193370_a);
         Set<String> set = Sets.newHashSet(keys.keySet());
         set.remove(" ");

         for (int i = 0; i < pattern.length; i++) {
            for (int j = 0; j < pattern[i].length(); j++) {
               String s = pattern[i].substring(j, j + 1);
               Ingredient ingredient = keys.get(s);
               if (ingredient == null) {
                  throw new JsonSyntaxException("Pattern references symbol '" + s + "' but it's not defined in the key");
               }

               set.remove(s);
               nonnulllist.set(j + patternWidth * i, ingredient);
            }
         }

         if (!set.isEmpty()) {
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
         } else {
            return nonnulllist;
         }
      }

      private static String[] shrink(String... toShrink) {
         int i = Integer.MAX_VALUE;
         int j = 0;
         int k = 0;
         int l = 0;

         for (int i1 = 0; i1 < toShrink.length; i1++) {
            String s = toShrink[i1];
            i = Math.min(i, firstNonSpace(s));
            int j1 = lastNonSpace(s);
            j = Math.max(j, j1);
            if (j1 < 0) {
               if (k == i1) {
                  k++;
               }

               l++;
            } else {
               l = 0;
            }
         }

         if (toShrink.length == l) {
            return new String[0];
         } else {
            String[] astring = new String[toShrink.length - l - k];

            for (int k1 = 0; k1 < astring.length; k1++) {
               astring[k1] = toShrink[k1 + k].substring(i, j + 1);
            }

            return astring;
         }
      }

      private static int firstNonSpace(String str) {
         int i = 0;

         while (i < str.length() && str.charAt(i) == ' ') {
            i++;
         }

         return i;
      }

      private static int lastNonSpace(String str) {
         int i = str.length() - 1;

         while (i >= 0 && str.charAt(i) == ' ') {
            i--;
         }

         return i;
      }

      private static String[] patternFromJson(JsonArray jsonArr) {
         String[] astring = new String[jsonArr.size()];
         if (astring.length > NonRaffleCrystalShapedRecipe.MAX_HEIGHT) {
            throw new JsonSyntaxException("Invalid pattern: too many rows, " + NonRaffleCrystalShapedRecipe.MAX_HEIGHT + " is maximum");
         } else if (astring.length == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
         } else {
            for (int i = 0; i < astring.length; i++) {
               String s = JSONUtils.func_151206_a(jsonArr.get(i), "pattern[" + i + "]");
               if (s.length() > NonRaffleCrystalShapedRecipe.MAX_WIDTH) {
                  throw new JsonSyntaxException("Invalid pattern: too many columns, " + NonRaffleCrystalShapedRecipe.MAX_WIDTH + " is maximum");
               }

               if (i > 0 && astring[0].length() != s.length()) {
                  throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
               }

               astring[i] = s;
            }

            return astring;
         }
      }

      private static Map<String, Ingredient> deserializeKey(JsonObject json) {
         Map<String, Ingredient> map = Maps.newHashMap();

         for (Entry<String, JsonElement> entry : json.entrySet()) {
            if (entry.getKey().length() != 1) {
               throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only).");
            }

            if (" ".equals(entry.getKey())) {
               throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }

            map.put(entry.getKey(), Ingredient.func_199802_a(entry.getValue()));
         }

         map.put(" ", Ingredient.field_193370_a);
         return map;
      }
   }
}
