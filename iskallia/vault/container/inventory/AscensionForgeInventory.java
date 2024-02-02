package iskallia.vault.container.inventory;

import iskallia.vault.block.entity.AscensionForgeTileEntity;
import iskallia.vault.container.spi.RecipeInventory;
import java.util.List;
import net.minecraft.world.item.ItemStack;

public class AscensionForgeInventory extends RecipeInventory {
   public static final int EMBER_SLOT = 0;

   public AscensionForgeInventory(AscensionForgeTileEntity tileEntity) {
      super(1, tileEntity);
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

   public List<ItemStack> getSlots() {
      return super.getContents();
   }
}
