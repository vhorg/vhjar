package iskallia.vault.container.inventory;

import iskallia.vault.block.entity.TransmogTableTileEntity;
import iskallia.vault.container.spi.RecipeInventory;
import java.util.List;
import net.minecraft.world.item.ItemStack;

public class TransmogTableInventory extends RecipeInventory {
   public static final int GEAR_SLOT = 0;
   public static final int BRONZE_SLOT = 1;

   public TransmogTableInventory(TransmogTableTileEntity tileEntity) {
      super(2, tileEntity);
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
