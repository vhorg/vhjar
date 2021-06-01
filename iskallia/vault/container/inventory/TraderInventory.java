package iskallia.vault.container.inventory;

import iskallia.vault.vending.Product;
import iskallia.vault.vending.Trade;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class TraderInventory implements IInventory {
   public static final int BUY_SLOT = 0;
   public static final int EXTRA_SLOT = 1;
   public static final int SELL_SLOT = 2;
   private final NonNullList<ItemStack> slots = NonNullList.func_191197_a(3, ItemStack.field_190927_a);
   private Trade selectedTrade;

   public void updateTrade(Trade core) {
      this.selectedTrade = core;
   }

   public Trade getSelectedTrade() {
      return this.selectedTrade;
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
      if (index == 2 && !itemStack.func_190926_b()) {
         ItemStack andSplit = ItemStackHelper.func_188382_a(this.slots, index, itemStack.func_190916_E());
         this.func_70298_a(0, this.selectedTrade.getBuy().getAmount());
         this.selectedTrade.onTraded();
         this.updateRecipe();
         return andSplit;
      } else {
         ItemStack splitStack = ItemStackHelper.func_188382_a(this.slots, index, count);
         this.updateRecipe();
         return splitStack;
      }
   }

   public ItemStack func_70304_b(int index) {
      ItemStack andRemove = ItemStackHelper.func_188383_a(this.slots, index);
      this.updateRecipe();
      return andRemove;
   }

   public void func_70299_a(int index, ItemStack stack) {
      this.slots.set(index, stack);
      this.updateRecipe();
   }

   public void func_70296_d() {
   }

   public boolean func_70300_a(PlayerEntity player) {
      return true;
   }

   public void updateRecipe() {
      if (this.selectedTrade != null) {
         Trade trade = this.selectedTrade;
         Product buy = trade.getBuy();
         Product sell = trade.getSell();
         if (((ItemStack)this.slots.get(0)).func_77973_b() != buy.getItem()) {
            this.slots.set(2, ItemStack.field_190927_a);
         } else if (((ItemStack)this.slots.get(0)).func_190916_E() < buy.getAmount()) {
            this.slots.set(2, ItemStack.field_190927_a);
         } else {
            this.slots.set(2, sell.toStack());
         }

         if (trade.getTradesLeft() == 0) {
            this.slots.set(2, ItemStack.field_190927_a);
         }
      }
   }

   public void func_174888_l() {
      this.slots.clear();
   }
}
