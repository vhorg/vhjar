package iskallia.vault.core.world.data.tile;

import iskallia.vault.core.world.data.entity.PartialCompoundNbt;
import iskallia.vault.core.world.data.item.ItemPredicate;
import iskallia.vault.core.world.data.item.PartialItem;
import java.util.Arrays;

public class OrItemPredicate implements ItemPredicate {
   private ItemPredicate[] children;

   public OrItemPredicate(ItemPredicate... children) {
      this.children = children;
   }

   public ItemPredicate[] getChildren() {
      return this.children;
   }

   @Override
   public boolean test(PartialItem item, PartialCompoundNbt nbt) {
      for (ItemPredicate child : this.children) {
         if (child.test(item, nbt)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public String toString() {
      return Arrays.toString((Object[])this.children);
   }
}
