package iskallia.vault.init;

import iskallia.vault.VaultMod;
import iskallia.vault.recipe.InitDollRecipe;
import iskallia.vault.recipe.MysteryEggRecipe;
import iskallia.vault.recipe.NonGrantedCrystalShapedRecipe;
import iskallia.vault.recipe.ShapelessCopyNbtRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraftforge.event.RegistryEvent.Register;

public class ModRecipes {
   public static void initialize() {
      PotionBrewing.CONTAINER_MIXES.removeIf(o -> o.ingredient.test(new ItemStack(Items.DRAGON_BREATH)));
   }

   public static class Serializer {
      public static NonGrantedCrystalShapedRecipe.Serializer NON_RAFFLE_CRYSTAL_SHAPED;
      public static ShapelessCopyNbtRecipe.Serializer COPY_NBT_SHAPELESS;
      public static SimpleRecipeSerializer<MysteryEggRecipe> MYSTERY_EGG_RECIPE;
      public static SimpleRecipeSerializer<InitDollRecipe> INIT_DOLL_RECIPE;

      public static void register(Register<RecipeSerializer<?>> event) {
         NON_RAFFLE_CRYSTAL_SHAPED = register(event, "non_raffle_crystal_shaped", new NonGrantedCrystalShapedRecipe.Serializer());
         COPY_NBT_SHAPELESS = register(event, "crafting_shapeless_copy_nbt", new ShapelessCopyNbtRecipe.Serializer());
         MYSTERY_EGG_RECIPE = register(event, "mystery_egg", new SimpleRecipeSerializer(MysteryEggRecipe::new));
         INIT_DOLL_RECIPE = register(event, "init_doll", new SimpleRecipeSerializer(InitDollRecipe::new));
      }

      private static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(Register<RecipeSerializer<?>> event, String name, S serializer) {
         serializer.setRegistryName(VaultMod.id(name));
         event.getRegistry().register(serializer);
         return serializer;
      }
   }
}
