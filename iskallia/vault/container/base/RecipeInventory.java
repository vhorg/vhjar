package iskallia.vault.container.base;

import java.util.function.Consumer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public abstract class RecipeInventory implements IInventory {
   protected final NonNullList<ItemStack> slots;

   public RecipeInventory(int inputCount) {
      this.slots = NonNullList.func_191197_a(inputCount + 1, ItemStack.field_190927_a);
   }

   public int func_70302_i_() {
      return this.slots.size();
   }

   public boolean func_191420_l() {
      return this.slots.isEmpty();
   }

   public ItemStack func_70301_a(int index) {
      return (ItemStack)this.slots.get(index);
   }

   public ItemStack func_70298_a(int index, int count) {
      ItemStack itemStack = (ItemStack)this.slots.get(index);
      if (index == this.outputSlotIndex() && !itemStack.func_190926_b()) {
         ItemStack andSplit = ItemStackHelper.func_188382_a(this.slots, index, itemStack.func_190916_E());
         this.consumeIngredients();
         this.updateResult();
         return andSplit;
      } else {
         ItemStack splitStack = ItemStackHelper.func_188382_a(this.slots, index, count);
         this.updateResult();
         return splitStack;
      }
   }

   public ItemStack func_70304_b(int index) {
      ItemStack andRemove = ItemStackHelper.func_188383_a(this.slots, index);
      this.updateResult();
      return andRemove;
   }

   public void func_70299_a(int index, ItemStack stack) {
      this.slots.set(index, stack);
      this.updateResult();
   }

   public void func_70296_d() {
   }

   public boolean func_70300_a(PlayerEntity playerEntity) {
      return true;
   }

   public void func_174888_l() {
      this.slots.clear();
   }

   public final void updateResult() {
      ItemStack outputItemStack = this.func_70301_a(this.outputSlotIndex());
      if (this.recipeFulfilled()) {
         this.slots.set(this.outputSlotIndex(), this.resultingItemStack());
      } else if (!outputItemStack.func_190926_b()) {
         this.slots.set(this.outputSlotIndex(), ItemStack.field_190927_a);
      }
   }

   public void consumeIngredients() {
      this.forEachInput(inputIndex -> this.func_70298_a(inputIndex, 1));
   }

   public abstract boolean recipeFulfilled();

   public abstract ItemStack resultingItemStack();

   public boolean isIngredientSlotsFilled() {
      for (int i = 0; i < this.slots.size() - 1; i++) {
         ItemStack ingredientStack = this.func_70301_a(i);
         if (ingredientStack.func_190926_b()) {
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
