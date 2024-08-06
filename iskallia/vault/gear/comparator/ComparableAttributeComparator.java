package iskallia.vault.gear.comparator;

import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.BiFunction;
import javax.annotation.Nonnull;

public class ComparableAttributeComparator<T extends Comparable<T>> extends VaultGearAttributeComparator.Merger<T> {
   private final BiFunction<T, T, T> differenceFn;
   private final BiFunction<T, T, T> addFn;

   public ComparableAttributeComparator(VaultGearAttributeTypeMerger<T, T> merger, BiFunction<T, T, T> differenceFn, BiFunction<T, T, T> addFn) {
      super(merger);
      this.differenceFn = differenceFn;
      this.addFn = addFn;
   }

   @Deprecated
   public Optional<T> difference(T thisValue, T thatValue) {
      return this.compare(thisValue, thatValue) == 0 ? Optional.empty() : Optional.of(this.differenceFn.apply(thisValue, thatValue));
   }

   @Nonnull
   @Override
   public Comparator<T> getComparator() {
      return Comparator.naturalOrder();
   }
}
