package iskallia.vault.container.inventory;

import iskallia.vault.container.spi.RecipeInventory;
import iskallia.vault.init.ModConfigs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class KeyPressInventory extends RecipeInventory {
   public static final int KEY_SLOT = 0;
   public static final int CLUSTER_SLOT = 1;

   public KeyPressInventory() {
      super(2);
   }

   @Override
   public boolean recipeFulfilled() {
      Item keyItem = this.getItem(0).getItem();
      Item clusterItem = this.getItem(1).getItem();
      return !ModConfigs.KEY_PRESS.getResultFor(keyItem, clusterItem).isEmpty();
   }

   @Override
   public ItemStack resultingItemStack() {
      Item keyItem = this.getItem(0).getItem();
      Item clusterItem = this.getItem(1).getItem();
      return ModConfigs.KEY_PRESS.getResultFor(keyItem, clusterItem);
   }
}
