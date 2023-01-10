package iskallia.vault.gear.crafting;

import iskallia.vault.block.entity.VaultForgeTileEntity;
import iskallia.vault.container.oversized.OverSizedItemStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class VaultForgeHelper {
   public static List<ItemStack> getMissingInputs(List<ItemStack> recipeInputs, Inventory playerInventory, VaultForgeTileEntity tile) {
      List<ItemStack> missing = new ArrayList<>();

      for (ItemStack input : recipeInputs) {
         int neededCount = input.getCount();

         for (OverSizedItemStack overSized : tile.getInventory().getOverSizedContents()) {
            if (isEqualCrafting(input, overSized.stack())) {
               neededCount -= overSized.amount();
            }
         }

         for (ItemStack plStack : playerInventory.items) {
            if (isEqualCrafting(input, plStack)) {
               neededCount -= plStack.getCount();
            }
         }

         if (neededCount > 0) {
            missing.add(input);
         }
      }

      return missing;
   }

   public static boolean consumeInputs(List<ItemStack> recipeInputs, Inventory playerInventory, VaultForgeTileEntity tile, boolean simulate) {
      boolean missingInput = true;

      for (ItemStack input : recipeInputs) {
         int neededCount = input.getCount();
         NonNullList<OverSizedItemStack> overSizedContents = tile.getInventory().getOverSizedContents();

         for (int slot = 0; slot < overSizedContents.size(); slot++) {
            OverSizedItemStack overSized = (OverSizedItemStack)overSizedContents.get(slot);
            if (neededCount <= 0) {
               break;
            }

            if (isEqualCrafting(input, overSized.stack())) {
               int deductedAmount = Math.min(neededCount, overSized.amount());
               if (!simulate) {
                  tile.getInventory().setOverSizedStack(slot, overSized.addCopy(-deductedAmount));
               }

               neededCount -= overSized.amount();
            }
         }

         for (ItemStack plStack : playerInventory.items) {
            if (neededCount <= 0) {
               break;
            }

            if (isEqualCrafting(input, plStack)) {
               int deductedAmount = Math.min(neededCount, plStack.getCount());
               if (!simulate) {
                  plStack.shrink(deductedAmount);
               }

               neededCount -= deductedAmount;
            }
         }

         if (neededCount > 0) {
            missingInput = false;
         }
      }

      return missingInput;
   }

   private static boolean isEqualCrafting(ItemStack thisStack, ItemStack thatStack) {
      return thisStack.getItem() == thatStack.getItem()
         && thisStack.getDamageValue() == thatStack.getDamageValue()
         && (thisStack.getTag() == null || thisStack.areShareTagsEqual(thatStack));
   }
}
