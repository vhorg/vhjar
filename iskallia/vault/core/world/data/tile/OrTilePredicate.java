package iskallia.vault.core.world.data.tile;

import iskallia.vault.core.world.data.entity.PartialCompoundNbt;
import java.util.Arrays;

public class OrTilePredicate implements TilePredicate {
   private TilePredicate[] children;

   public OrTilePredicate(TilePredicate... children) {
      this.children = children;
   }

   public TilePredicate[] getChildren() {
      return this.children;
   }

   @Override
   public boolean test(PartialBlockState state, PartialCompoundNbt nbt) {
      for (TilePredicate child : this.children) {
         if (child.test(state, nbt)) {
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
