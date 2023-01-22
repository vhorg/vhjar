package iskallia.vault.recipe;

import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModRecipes;
import iskallia.vault.item.VaultDollItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class InitDollRecipe extends CustomRecipe {
   public InitDollRecipe(ResourceLocation id) {
      super(id);
   }

   public boolean matches(CraftingContainer inv, Level level) {
      boolean foundDoll = false;

      for (int i = 0; i < inv.getContainerSize(); i++) {
         ItemStack stack = inv.getItem(i);
         if (!stack.isEmpty()) {
            if (foundDoll || stack.getItem() != ModItems.VAULT_DOLL || !VaultDollItem.getPlayerGameProfile(stack).isEmpty()) {
               return false;
            }

            foundDoll = true;
         }
      }

      return foundDoll;
   }

   public ItemStack assemble(CraftingContainer inv) {
      for (int i = 0; i < inv.getContainerSize(); i++) {
         ItemStack stack = inv.getItem(i);
         if (!stack.isEmpty() && stack.getItem() == ModItems.VAULT_DOLL && VaultDollItem.getPlayerGameProfile(stack).isEmpty()) {
            return stack;
         }
      }

      return ItemStack.EMPTY;
   }

   public boolean canCraftInDimensions(int width, int height) {
      return width * height >= 1;
   }

   public RecipeSerializer<?> getSerializer() {
      return ModRecipes.Serializer.INIT_DOLL_RECIPE;
   }
}
