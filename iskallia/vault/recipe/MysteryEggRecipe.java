package iskallia.vault.recipe;

import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModRecipes;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class MysteryEggRecipe extends SpecialRecipe {
   public MysteryEggRecipe(ResourceLocation id) {
      super(id);
   }

   public boolean matches(CraftingInventory inv, World world) {
      int foundEggs = 0;
      int foundAlex = 0;

      for (int i = 0; i < inv.func_70302_i_(); i++) {
         ItemStack stack = inv.func_70301_a(i);
         if (!stack.func_190926_b()) {
            if (stack.func_77973_b() == ModItems.ALEXANDRITE_GEM) {
               foundAlex++;
            }

            if (stack.func_77973_b() instanceof SpawnEggItem) {
               foundEggs++;
            }
         }
      }

      return foundEggs == 4 && foundAlex == 1;
   }

   public ItemStack getCraftingResult(CraftingInventory inv) {
      return new ItemStack(ModItems.MYSTERY_EGG, 4);
   }

   public boolean func_194133_a(int width, int height) {
      return width * height >= 5;
   }

   public IRecipeSerializer<?> func_199559_b() {
      return ModRecipes.Serializer.MYSTERY_EGG_RECIPE;
   }
}
