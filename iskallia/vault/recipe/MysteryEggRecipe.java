package iskallia.vault.recipe;

import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModRecipes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class MysteryEggRecipe extends CustomRecipe {
   public MysteryEggRecipe(ResourceLocation id) {
      super(id);
   }

   public boolean matches(CraftingContainer inv, Level world) {
      int foundEggs = 0;
      int foundAlex = 0;

      for (int i = 0; i < inv.getContainerSize(); i++) {
         ItemStack stack = inv.getItem(i);
         if (!stack.isEmpty()) {
            if (stack.getItem() == ModItems.PERFECT_WUTODIE) {
               foundAlex++;
            }

            if (stack.getItem() instanceof SpawnEggItem) {
               foundEggs++;
            }
         }
      }

      return foundEggs == 4 && foundAlex == 1;
   }

   public ItemStack assemble(CraftingContainer inv) {
      return new ItemStack(ModItems.MYSTERY_EGG, 4);
   }

   public boolean canCraftInDimensions(int width, int height) {
      return width * height >= 5;
   }

   public RecipeSerializer<?> getSerializer() {
      return ModRecipes.Serializer.MYSTERY_EGG_RECIPE;
   }
}
