package iskallia.vault.container.spi;

import iskallia.vault.container.oversized.OverSizedInventory;
import java.util.function.Consumer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class RecipeInventory extends OverSizedInventory {
   public RecipeInventory(int inputCount, BlockEntity tileEntity) {
      super(inputCount + 1, tileEntity);
   }

   public int getInputSlotCount() {
      return this.getContents().size() - 1;
   }

   @Override
   public int getContainerSize() {
      return this.getContents().size();
   }

   @Override
   public void setChanged() {
      super.setChanged();
   }

   public void updateResult() {
      ItemStack outputItemStack = this.getItem(this.outputSlotIndex());
      if (this.recipeFulfilled()) {
         this.getContents().set(this.outputSlotIndex(), this.resultingItemStack());
      } else if (!outputItemStack.isEmpty()) {
         this.getContents().set(this.outputSlotIndex(), ItemStack.EMPTY);
      }
   }

   public void consumeIngredients() {
      this.forEachInput(inputIndex -> this.removeItem(inputIndex, 1));
   }

   public abstract boolean recipeFulfilled();

   public abstract ItemStack resultingItemStack();

   public boolean isIngredientSlotsFilled() {
      for (int i = 0; i < this.getContents().size() - 1; i++) {
         ItemStack ingredientStack = this.getItem(i);
         if (ingredientStack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public void forEachInput(Consumer<Integer> inputConsumer) {
      for (int i = 0; i < this.getContents().size() - 1; i++) {
         inputConsumer.accept(i);
      }
   }

   public int outputSlotIndex() {
      return this.getContents().size() - 1;
   }

   public boolean isIngredientIndex(int index) {
      return index < this.outputSlotIndex();
   }
}
