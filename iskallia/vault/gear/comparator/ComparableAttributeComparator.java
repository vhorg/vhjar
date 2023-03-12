package iskallia.vault.gear.comparator;

import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.BiFunction;

public class ComparableAttributeComparator<T extends Comparable<T>> extends VaultGearAttributeComparator.Merger<T> {
   private final BiFunction<T, T, T> differenceFn;

   public ComparableAttributeComparator(VaultGearAttributeTypeMerger<T, T> merger, BiFunction<T, T, T> differenceFn) {
      super(merger);
      this.differenceFn = differenceFn;
   }

   public Optional<T> difference(T thisValue, T thatValue) {
      return this.compare(thisValue, thatValue) == 0 ? Optional.empty() : Optional.of(this.differenceFn.apply(thisValue, thatValue));
   }

   @Override
   public Comparator<T> getComparator() {
      return Comparator.naturalOrder();
   }
}
