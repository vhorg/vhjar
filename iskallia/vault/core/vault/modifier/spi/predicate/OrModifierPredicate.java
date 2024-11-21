package iskallia.vault.core.vault.modifier.spi.predicate;

import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import java.util.Arrays;

public class OrModifierPredicate implements ModifierPredicate {
   private final ModifierPredicate[] children;

   public OrModifierPredicate(ModifierPredicate... children) {
      this.children = children;
   }

   public ModifierPredicate[] getChildren() {
      return this.children;
   }

   @Override
   public boolean test(VaultModifier<?> modifier) {
      for (ModifierPredicate child : this.children) {
         if (child.test(modifier)) {
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
