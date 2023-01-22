package iskallia.vault.container.inventory;

import iskallia.vault.block.entity.RelicPedestalTileEntity;
import iskallia.vault.container.spi.RecipeInventory;
import net.minecraft.world.item.ItemStack;

public class RelicPedestalInventory extends RecipeInventory {
   public RelicPedestalInventory(RelicPedestalTileEntity tileEntity) {
      super(5, tileEntity);
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
