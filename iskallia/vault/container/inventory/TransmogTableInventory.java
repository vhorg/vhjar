package iskallia.vault.container.inventory;

import iskallia.vault.container.spi.RecipeInventory;
import net.minecraft.world.item.ItemStack;

public class TransmogTableInventory extends RecipeInventory {
   public static final int GEAR_SLOT = 0;
   public static final int BRONZE_SLOT = 1;

   public TransmogTableInventory() {
      super(2);
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
