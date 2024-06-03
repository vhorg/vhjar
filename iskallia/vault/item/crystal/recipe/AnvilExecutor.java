package iskallia.vault.item.crystal.recipe;

import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.item.crystal.data.serializable.INbtSerializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;

public class AnvilExecutor {
   public static AnvilExecutor.Result test(Player player, ItemStack input, List<ItemStack> ingredients, List<ItemStack> uniqueIngredients) {
      AnvilExecutor.Result result = new AnvilExecutor.Result(ingredients.size());
      result.setOutput(input.copy());
      applyIngredient(player, result, 0, uniqueIngredients.get(0), result::setUniqueIngredientResult, false);
      applyIngredient(player, result, 1, uniqueIngredients.get(1), result::setUniqueIngredientResult, false);

      for (int index = 0; index < ingredients.size(); index++) {
         applyIngredient(player, result, index, ingredients.get(index).copy(), result::setIngredientResult, true);
      }

      applyIngredient(player, result, 2, uniqueIngredients.get(2), result::setUniqueIngredientResult, false);
      return result;
   }

   private static void applyIngredient(
      Player player, AnvilExecutor.Result result, int index, ItemStack ingredient, BiConsumer<Integer, ItemStack> setIngredientResult, boolean setUsedSlot
   ) {
      setIngredientResult.accept(index, ingredient);

      while (!ingredient.isEmpty()) {
         AnvilMenu anvil = new AnvilMenu(0, player.getInventory());
         AnvilMenuProxy.of(anvil).setFake(true);
         anvil.getSlot(0).set(result.getOutput());
         anvil.getSlot(1).set(ingredient.copy());
         anvil.createResult();
         ItemStack output = anvil.getSlot(2).safeTake(anvil.getSlot(2).getItem().getCount(), Integer.MAX_VALUE, player);
         if (output.isEmpty()) {
            break;
         }

         ingredient = anvil.getSlot(1).getItem();
         setIngredientResult.accept(index, ingredient);
         if (setUsedSlot) {
            result.addUsedSlot(index);
         }

         if (anvil.getSlot(0).getItem().isEmpty()) {
            result.setOutput(output);
         } else {
            result.addExtra(output);
            result.setOutput(anvil.getSlot(0).getItem().copy());
         }
      }
   }

   public static class Result implements INbtSerializable<CompoundTag> {
      protected ItemStack[] uniqueIngredients;
      protected ItemStack[] ingredients;
      protected Set<Integer> usedSlots;
      protected ItemStack output;
      private List<ItemStack> extra;

      public Result() {
      }

      public Result(int size) {
         this.ingredients = new ItemStack[size];
         this.uniqueIngredients = new ItemStack[3];
         this.usedSlots = new HashSet<>();
         this.output = ItemStack.EMPTY;
         this.extra = new ArrayList<>();
         Arrays.fill(this.ingredients, ItemStack.EMPTY);
      }

      public ItemStack[] getIngredients() {
         return this.ingredients;
      }

      public ItemStack getUniqueIngredient(int index) {
         return this.uniqueIngredients[index];
      }

      public boolean hasUsedSlot(int index) {
         return this.usedSlots.contains(index);
      }

      public ItemStack getOutput() {
         return this.output;
      }

      public List<ItemStack> getExtra() {
         return this.extra;
      }

      public void setIngredientResult(int index, ItemStack stack) {
         this.ingredients[index] = stack;
      }

      public void setUniqueIngredientResult(int index, ItemStack stack) {
         this.uniqueIngredients[index] = stack;
      }

      public void addUsedSlot(int index) {
         this.usedSlots.add(index);
      }

      public void setOutput(ItemStack stack) {
         this.output = stack;
      }

      public void addExtra(ItemStack stack) {
         this.extra.add(stack);
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         CompoundTag nbt = new CompoundTag();
         ListTag ingredients = new ListTag();

         for (ItemStack ingredient : this.ingredients) {
            Adapters.ITEM_STACK.writeNbt(ingredient).ifPresent(ingredients::add);
         }

         ListTag uniqueIngredients = new ListTag();

         for (ItemStack uniqueIngredient : this.uniqueIngredients) {
            Adapters.ITEM_STACK.writeNbt(uniqueIngredient).ifPresent(uniqueIngredients::add);
         }

         nbt.put("ingredients", ingredients);
         nbt.put("unique_ingredients", uniqueIngredients);
         nbt.put("used_slots", new IntArrayTag(new ArrayList<>(this.usedSlots)));
         Adapters.ITEM_STACK.writeNbt(this.output).ifPresent(output -> nbt.put("output", output));
         return Optional.of(nbt);
      }

      public void readNbt(CompoundTag nbt) {
         ListTag ingredients = (ListTag)nbt.get("ingredients");
         if (ingredients != null) {
            this.ingredients = new ItemStack[ingredients.size()];

            for (int i = 0; i < ingredients.size(); i++) {
               this.ingredients[i] = Adapters.ITEM_STACK.readNbt(ingredients.get(i)).orElse(ItemStack.EMPTY);
            }
         }

         ListTag uniqueIngredients = (ListTag)nbt.get("unique_ingredients");
         if (uniqueIngredients != null) {
            this.uniqueIngredients = new ItemStack[uniqueIngredients.size()];

            for (int i = 0; i < uniqueIngredients.size(); i++) {
               this.uniqueIngredients[i] = Adapters.ITEM_STACK.readNbt(uniqueIngredients.get(i)).orElse(ItemStack.EMPTY);
            }
         }

         int[] usedSlots = nbt.getIntArray("used_slots");
         this.usedSlots = new HashSet<>();

         for (int usedSlot : usedSlots) {
            this.usedSlots.add(usedSlot);
         }

         this.output = Adapters.ITEM_STACK.readNbt(nbt.get("output")).orElse(ItemStack.EMPTY);
      }

      public ItemStack[] getUniqueIngredients() {
         return this.uniqueIngredients;
      }
   }
}
