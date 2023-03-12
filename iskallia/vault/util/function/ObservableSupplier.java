package iskallia.vault.util.function;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ObservableSupplier<T> implements Supplier<T> {
   private static final ObservableSupplier<?> EMPTY = new ObservableSupplier<>(() -> null, (o1, o2) -> true);
   protected T previousValue;
   protected final Supplier<T> supplier;
   protected final BiPredicate<T, T> equivalenceTest;

   public static <T> ObservableSupplier<T> empty() {
      return (ObservableSupplier<T>)EMPTY;
   }

   public static <T> ObservableSupplier<T> ofIdentity(Supplier<T> supplier) {
      return new ObservableSupplier<>(supplier, Objects::equals);
   }

   public static <T> ObservableSupplier<T> of(Supplier<T> supplier, BiPredicate<T, T> equivalenceFunction) {
      return new ObservableSupplier<>(supplier, equivalenceFunction);
   }

   protected ObservableSupplier(Supplier<T> supplier, BiPredicate<T, T> equivalenceTest) {
      this.supplier = supplier;
      this.equivalenceTest = equivalenceTest;
   }

   protected boolean hasChanged(T currentValue) {
      if (this.previousValue != currentValue && (this.previousValue == null || !this.equivalenceTest.test(this.previousValue, currentValue))) {
         this.previousValue = currentValue;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public T get() {
      return this.supplier.get();
   }

   public boolean hasChanged() {
      return this.hasChanged(this.get());
   }

   public void ifChanged(Consumer<? super T> action) {
      T value = this.get();
      if (this.hasChanged(value)) {
         action.accept(value);
      }
   }

   public void ifChangedOrElse(Consumer<? super T> action, Consumer<? super T> unchangedAction) {
      T value = this.get();
      boolean hasChanged = this.hasChanged(value);
      if (hasChanged) {
         action.accept(value);
      } else {
         unchangedAction.accept(value);
      }
   }
}
