package iskallia.vault.container.slot;

import iskallia.vault.container.slot.spi.IGhostSlot;
import iskallia.vault.dynamodel.DynamicModelItem;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModRelics;
import iskallia.vault.item.RelicFragmentItem;
import java.util.LinkedList;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class RelicRecipeFragmentSlot extends Slot implements IGhostSlot {
   protected Supplier<ModRelics.RelicRecipe> recipe;

   public RelicRecipeFragmentSlot(Supplier<ModRelics.RelicRecipe> recipe, Container container, int pIndex, int pX, int pY) {
      super(container, pIndex, pX, pY);
      this.recipe = recipe;
   }

   public int getMaxStackSize() {
      return 1;
   }

   public boolean mayPlace(@Nonnull ItemStack itemStack) {
      if (!(itemStack.getItem() instanceof RelicFragmentItem)) {
         return false;
      } else {
         ResourceLocation fragmentId = DynamicModelItem.getGenericModelId(itemStack).orElse(null);
         if (fragmentId == null) {
            return false;
         } else {
            ModRelics.RelicRecipe relicRecipe = this.recipe.get();
            if (relicRecipe == null) {
               return false;
            } else {
               LinkedList<ResourceLocation> fragments = new LinkedList<>(relicRecipe.getFragments());
               return fragments.get(this.getSlotIndex()).equals(fragmentId);
            }
         }
      }
   }

   @Override
   public ItemStack getGhostItemStack() {
      ModRelics.RelicRecipe relicRecipe = this.recipe.get();
      if (relicRecipe == null) {
         return null;
      } else {
         LinkedList<ResourceLocation> fragments = new LinkedList<>(relicRecipe.getFragments());
         if (this.getSlotIndex() >= fragments.size()) {
            return null;
         } else {
            ResourceLocation fragmentId = fragments.get(this.getSlotIndex());
            ItemStack itemStack = new ItemStack(ModItems.RELIC_FRAGMENT);
            DynamicModelItem.setGenericModelId(itemStack, fragmentId);
            return itemStack;
         }
      }
   }
}
