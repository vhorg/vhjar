package iskallia.vault.recipe;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class CatalystInfusionTableRecipe {
   private static final Ingredient CATALYST_INGREDIENT = Ingredient.of(new ItemStack[]{new ItemStack(ModItems.VAULT_CATALYST)});
   private static final Ingredient INFUSION_INGREDIENT = Ingredient.of(new ItemStack[]{ModConfigs.CATALYST_INFUSION_TABLE.getInfusionItem().copy()});
   private static final ItemStack OUTPUT = new ItemStack(ModItems.VAULT_CATALYST_INFUSED);

   public Ingredient getInfusionIngredient() {
      return INFUSION_INGREDIENT;
   }

   public Ingredient getCatalystIngredient() {
      return CATALYST_INGREDIENT;
   }

   public ItemStack getOutput() {
      return OUTPUT;
   }
}
