package iskallia.vault.recipe;

import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModRecipes;
import iskallia.vault.item.ItemVaultGem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class MysteryStewRecipe extends SpecialRecipe {
   public MysteryStewRecipe(ResourceLocation id) {
      super(id);
   }

   public boolean matches(CraftingInventory inv, World world) {
      int gemCount = 0;
      boolean hasBowl = false;
      boolean hasDiamond = false;
      boolean hasEye = false;
      boolean hasPizza = false;
      boolean hasSchroom = false;
      boolean hasKiwi = false;

      for (int i = 0; i < inv.func_70302_i_(); i++) {
         ItemStack stack = inv.func_70301_a(i);
         if (stack.func_77973_b() instanceof ItemVaultGem) {
            if (++gemCount > 3) {
               return false;
            }
         } else if (stack.func_77973_b() == Items.field_151054_z) {
            if (hasBowl) {
               return false;
            }

            hasBowl = true;
         } else if (stack.func_77973_b() == ModItems.VAULT_DIAMOND) {
            if (hasDiamond) {
               return false;
            }

            hasDiamond = true;
         } else if (stack.func_77973_b() == ModItems.HUNTER_EYE) {
            if (hasEye) {
               return false;
            }

            hasEye = true;
         } else if (stack.func_77973_b() == ModItems.OOZING_PIZZA) {
            if (hasPizza) {
               return false;
            }

            hasPizza = true;
         } else if (stack.func_77973_b() == ModItems.POISONOUS_MUSHROOM) {
            if (hasSchroom) {
               return false;
            }

            hasSchroom = true;
         } else if (stack.func_77973_b() == ModItems.SWEET_KIWI) {
            if (hasKiwi) {
               return false;
            }

            hasKiwi = true;
         }
      }

      return gemCount == 3 && hasBowl && hasDiamond && hasEye && hasPizza && hasSchroom && hasKiwi;
   }

   public ItemStack getCraftingResult(CraftingInventory inv) {
      return new ItemStack(ModItems.VAULT_STEW_MYSTERY);
   }

   public boolean func_194133_a(int width, int height) {
      return width * height >= 9;
   }

   public IRecipeSerializer<?> func_199559_b() {
      return ModRecipes.Serializer.CRAFTING_SPECIAL_MYSTERY_STEW;
   }
}
