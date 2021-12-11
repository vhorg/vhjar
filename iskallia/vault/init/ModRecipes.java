package iskallia.vault.init;

import iskallia.vault.Vault;
import iskallia.vault.recipe.MysteryEggRecipe;
import iskallia.vault.recipe.NonRaffleCrystalShapedRecipe;
import iskallia.vault.recipe.RelicSetRecipe;
import iskallia.vault.recipe.ShapelessCopyNbtRecipe;
import iskallia.vault.recipe.UnidentifiedRelicRecipe;
import java.lang.reflect.Field;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.potion.PotionBrewing;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class ModRecipes {
   public static void initialize() {
      PotionBrewing.field_185214_b.removeIf(o -> {
         Field f = ObfuscationReflectionHelper.findField(o.getClass(), "field_185199_b");

         try {
            Ingredient i = (Ingredient)f.get(o);
            if (i.test(new ItemStack(Items.field_185157_bK))) {
               return true;
            }
         } catch (Exception var3) {
         }

         return false;
      });
   }

   public static class Serializer {
      public static SpecialRecipeSerializer<RelicSetRecipe> CRAFTING_SPECIAL_RELIC_SET;
      public static SpecialRecipeSerializer<UnidentifiedRelicRecipe> CRAFTING_SPECIAL_UNIDENTIFIED_RELIC;
      public static NonRaffleCrystalShapedRecipe.Serializer NON_RAFFLE_CRYSTAL_SHAPED;
      public static ShapelessCopyNbtRecipe.Serializer COPY_NBT_SHAPELESS;
      public static SpecialRecipeSerializer<MysteryEggRecipe> MYSTERY_EGG_RECIPE;

      public static void register(Register<IRecipeSerializer<?>> event) {
         CRAFTING_SPECIAL_RELIC_SET = register(event, "crafting_special_relic_set", new SpecialRecipeSerializer(RelicSetRecipe::new));
         CRAFTING_SPECIAL_UNIDENTIFIED_RELIC = register(event, "crafting_special_unidentified_relic", new SpecialRecipeSerializer(UnidentifiedRelicRecipe::new));
         NON_RAFFLE_CRYSTAL_SHAPED = register(event, "non_raffle_crystal_shaped", new NonRaffleCrystalShapedRecipe.Serializer());
         COPY_NBT_SHAPELESS = register(event, "crafting_shapeless_copy_nbt", new ShapelessCopyNbtRecipe.Serializer());
         MYSTERY_EGG_RECIPE = register(event, "mystery_egg", new SpecialRecipeSerializer(MysteryEggRecipe::new));
      }

      private static <S extends IRecipeSerializer<T>, T extends IRecipe<?>> S register(Register<IRecipeSerializer<?>> event, String name, S serializer) {
         serializer.setRegistryName(Vault.id(name));
         event.getRegistry().register(serializer);
         return serializer;
      }
   }
}
