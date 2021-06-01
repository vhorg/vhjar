package iskallia.vault.container.inventory;

import iskallia.vault.init.ModConfigs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class KeyPressInventory implements IInventory {
   public static final int KEY_SLOT = 0;
   public static final int CLUSTER_SLOT = 1;
   public static final int RESULT_SLOT = 2;
   private final NonNullList<ItemStack> slots = NonNullList.func_191197_a(3, ItemStack.field_190927_a);

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
         this.func_70298_a(0, 1);
         this.func_70298_a(1, 1);
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

   public boolean func_70300_a(PlayerEntity player) {
      return true;
   }

   public void updateResult() {
      Item keyItem = this.func_70301_a(0).func_77973_b();
      Item clusterItem = this.func_70301_a(1).func_77973_b();
      ItemStack result = ModConfigs.KEY_PRESS.getResultFor(keyItem, clusterItem);
      this.slots.set(2, result);
   }

   public void func_174888_l() {
      this.slots.clear();
   }
}
