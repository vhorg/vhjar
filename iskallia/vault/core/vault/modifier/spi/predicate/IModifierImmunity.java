package iskallia.vault.core.vault.modifier.spi.predicate;

public interface IModifierImmunity {
   ModifierPredicate getImmunity();

   void setImmunity(ModifierPredicate var1);

   static ModifierPredicate of(Object object) {
      if (object instanceof IModifierImmunity proxy) {
         return proxy.getImmunity() == null ? ModifierPredicate.FALSE : proxy.getImmunity();
      } else {
         return ModifierPredicate.FALSE;
      }
   }
}
