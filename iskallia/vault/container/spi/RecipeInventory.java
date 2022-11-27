package iskallia.vault.container.spi;

import java.util.function.Consumer;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public abstract class RecipeInventory implements Container {
   protected final NonNullList<ItemStack> slots;

   public RecipeInventory(int inputCount) {
      this.slots = NonNullList.withSize(inputCount + 1, ItemStack.EMPTY);
   }

   public int getInputSlotCount() {
      return this.slots.size() - 1;
   }

   public int getContainerSize() {
      return this.slots.size();
   }

   public boolean isEmpty() {
      return this.slots.isEmpty();
   }

   public ItemStack getItem(int index) {
      return (ItemStack)this.slots.get(index);
   }

   public ItemStack removeItem(int index, int count) {
      ItemStack itemStack = (ItemStack)this.slots.get(index);
      if (index == this.outputSlotIndex() && !itemStack.isEmpty()) {
         ItemStack andSplit = ContainerHelper.removeItem(this.slots, index, itemStack.getCount());
         this.consumeIngredients();
         this.updateResult();
         return andSplit;
      } else {
         ItemStack splitStack = ContainerHelper.removeItem(this.slots, index, count);
         this.updateResult();
         return splitStack;
      }
   }

   public ItemStack removeItemNoUpdate(int index) {
      ItemStack andRemove = ContainerHelper.takeItem(this.slots, index);
      this.updateResult();
      return andRemove;
   }

   public void setItem(int index, ItemStack stack) {
      this.slots.set(index, stack);
      this.updateResult();
   }

   public void setChanged() {
   }

   public boolean stillValid(Player playerEntity) {
      return true;
   }

   public void clearContent() {
      this.slots.clear();
   }

   public void updateResult() {
      ItemStack outputItemStack = this.getItem(this.outputSlotIndex());
      if (this.recipeFulfilled()) {
         this.slots.set(this.outputSlotIndex(), this.resultingItemStack());
      } else if (!outputItemStack.isEmpty()) {
         this.slots.set(this.outputSlotIndex(), ItemStack.EMPTY);
      }
   }

   public void consumeIngredients() {
      this.forEachInput(inputIndex -> this.removeItem(inputIndex, 1));
   }

   public abstract boolean recipeFulfilled();

   public abstract ItemStack resultingItemStack();

   public boolean isIngredientSlotsFilled() {
      for (int i = 0; i < this.slots.size() - 1; i++) {
         ItemStack ingredientStack = this.getItem(i);
         if (ingredientStack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public void forEachInput(Consumer<Integer> inputConsumer) {
      for (int i = 0; i < this.slots.size() - 1; i++) {
         inputConsumer.accept(i);
      }
   }

   public int outputSlotIndex() {
      return this.slots.size() - 1;
   }

   public boolean isIngredientIndex(int index) {
      return index < this.outputSlotIndex();
   }
}
