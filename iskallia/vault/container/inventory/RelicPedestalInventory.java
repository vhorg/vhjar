package iskallia.vault.container.inventory;

import iskallia.vault.container.spi.RecipeInventory;
import net.minecraft.world.item.ItemStack;

public class RelicPedestalInventory extends RecipeInventory {
   public RelicPedestalInventory() {
      super(5);
   }

   @Override
   public boolean recipeFulfilled() {
      return false;
   }

   @Override
   public ItemStack resultingItemStack() {
      return ItemStack.EMPTY;
   }

   @Override
   public void consumeIngredients() {
   }

   @Override
   public void updateResult() {
   }
}
