package iskallia.vault.container.inventory;

import iskallia.vault.container.base.RecipeInventory;
import iskallia.vault.init.ModConfigs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class KeyPressInventory extends RecipeInventory {
   public static final int KEY_SLOT = 0;
   public static final int CLUSTER_SLOT = 1;

   public KeyPressInventory() {
      super(2);
   }

   @Override
   public boolean recipeFulfilled() {
      Item keyItem = this.func_70301_a(0).func_77973_b();
      Item clusterItem = this.func_70301_a(1).func_77973_b();
      return !ModConfigs.KEY_PRESS.getResultFor(keyItem, clusterItem).func_190926_b();
   }

   @Override
   public ItemStack resultingItemStack() {
      Item keyItem = this.func_70301_a(0).func_77973_b();
      Item clusterItem = this.func_70301_a(1).func_77973_b();
      return ModConfigs.KEY_PRESS.getResultFor(keyItem, clusterItem);
   }
}
