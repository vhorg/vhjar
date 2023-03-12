package iskallia.vault.core;

public interface IVersion<E extends Enum<E>> {
   E getThis();

   default boolean isNewerThan(E v) {
      return this.getThis().compareTo(v) > 0;
   }

   default boolean isNewerOrEqualTo(E v) {
      return this.getThis().compareTo(v) >= 0;
   }

   default boolean isOlderThan(E v) {
      return this.getThis().compareTo(v) < 0;
   }

   default boolean isOlderOrEqualTo(E v) {
      return this.getThis().compareTo(v) <= 0;
   }

   default boolean isEqualTo(E v) {
      return this.getThis().compareTo(v) == 0;
   }
}
