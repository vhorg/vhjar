package iskallia.vault.recipe;

import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModRecipes;
import iskallia.vault.item.RelicPartItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class UnidentifiedRelicRecipe extends SpecialRecipe {
   public UnidentifiedRelicRecipe(ResourceLocation id) {
      super(id);
   }

   public boolean matches(CraftingInventory inv, World world) {
      RelicPartItem relic = null;
      int diamondCount = 0;

      for (int i = 0; i < inv.func_70302_i_(); i++) {
         ItemStack stack = inv.func_70301_a(i);
         if (stack.func_77973_b() == ModItems.VAULT_DIAMOND) {
            if (diamondCount++ == 8) {
               return false;
            }
         } else {
            if (!(stack.func_77973_b() instanceof RelicPartItem)) {
               return false;
            }

            if (relic != null) {
               return false;
            }

            relic = (RelicPartItem)stack.func_77973_b();
         }
      }

      return true;
   }

   public ItemStack getCraftingResult(CraftingInventory inv) {
      return new ItemStack(ModItems.UNIDENTIFIED_RELIC);
   }

   public boolean func_194133_a(int width, int height) {
      return width * height >= 9;
   }

   public IRecipeSerializer<?> func_199559_b() {
      return ModRecipes.Serializer.CRAFTING_SPECIAL_UNIDENTIFIED_RELIC;
   }
}
