package iskallia.vault.gear.comparator;

import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import java.util.Comparator;
import java.util.Optional;
import javax.annotation.Nonnull;

public abstract class VaultGearAttributeComparator<T> {
   public static ComparableAttributeComparator<Float> floatComparator() {
      return new ComparableAttributeComparator<>(VaultGearAttributeTypeMerger.floatSum(), (f1, f2) -> f1 - f2);
   }

   public static ComparableAttributeComparator<Integer> intComparator() {
      return new ComparableAttributeComparator<>(VaultGearAttributeTypeMerger.intSum(), (f1, f2) -> f1 - f2);
   }

   public static ComparableAttributeComparator<Double> doubleComparator() {
      return new ComparableAttributeComparator<>(VaultGearAttributeTypeMerger.doubleSum(), (f1, f2) -> f1 - f2);
   }

   public static VaultGearAttributeComparator<Boolean> booleanComparator() {
      return new VaultGearAttributeComparator.Merger<Boolean>(VaultGearAttributeTypeMerger.anyTrue()) {
         @Deprecated
         public Optional<Boolean> difference(Boolean thisValue, Boolean thatValue) {
            return thisValue != thatValue ? Optional.of(thatValue) : Optional.empty();
         }

         @Nonnull
         @Override
         public Comparator<Boolean> getComparator() {
            return Comparator.naturalOrder();
         }
      };
   }

   public abstract T merge(T var1, T var2);

   @Deprecated
   public abstract Optional<T> difference(T var1, T var2);

   public int compare(T thisValue, T thatValue) {
      return this.getComparator().compare(thisValue, thatValue);
   }

   @Nonnull
   public abstract Comparator<T> getComparator();

   public abstract static class Merger<T> extends VaultGearAttributeComparator<T> {
      private final VaultGearAttributeTypeMerger<T, T> merger;

      protected Merger(VaultGearAttributeTypeMerger<T, T> merger) {
         this.merger = merger;
      }

      @Override
      public T merge(T thisValue, T thatValue) {
         return this.merger.merge(thisValue, thatValue);
      }
   }
}
