package iskallia.vault.gear.attribute.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import org.apache.commons.lang3.ObjectUtils;

public abstract class VaultGearAttributeTypeMerger<T, C> {
   public abstract C merge(C var1, T var2);

   public abstract C getBaseValue();

   public static <T, C> VaultGearAttributeTypeMerger<T, C> of(final Supplier<C> baseValueSupplier, final BiFunction<C, T, C> mergeFunction) {
      return new VaultGearAttributeTypeMerger<T, C>() {
         @Override
         public C merge(C merged, T other) {
            return mergeFunction.apply(merged, other);
         }

         @Override
         public C getBaseValue() {
            return baseValueSupplier.get();
         }
      };
   }

   public static VaultGearAttributeTypeMerger<Integer, Integer> intSum() {
      return of(() -> 0, Integer::sum);
   }

   public static VaultGearAttributeTypeMerger<Integer, Integer> intMax() {
      return of(() -> 0, Integer::max);
   }

   public static VaultGearAttributeTypeMerger<Float, Float> floatSum() {
      return of(() -> 0.0F, Float::sum);
   }

   public static VaultGearAttributeTypeMerger<Float, Float> floatMax() {
      return of(() -> 0.0F, Float::max);
   }

   public static VaultGearAttributeTypeMerger<Double, Double> doubleSum() {
      return of(() -> 0.0, Double::sum);
   }

   public static VaultGearAttributeTypeMerger<Double, Double> doubleMax() {
      return of(() -> 0.0, Double::max);
   }

   public static VaultGearAttributeTypeMerger<Boolean, Boolean> anyTrue() {
      return of(() -> false, (flag, element) -> flag || element);
   }

   public static VaultGearAttributeTypeMerger<Boolean, Boolean> anyFalse() {
      return of(() -> false, (flag, element) -> flag || !element);
   }

   public static VaultGearAttributeTypeMerger<String, String> concat() {
      return of(() -> "", String::concat);
   }

   public static VaultGearAttributeTypeMerger<String, String> joining(CharSequence delimiter) {
      return of(() -> "", (str, element) -> {
         if (!str.isEmpty()) {
            str = str + delimiter;
         }

         return str + element;
      });
   }

   public static <T> VaultGearAttributeTypeMerger<T, T> firstNonNull() {
      return of(() -> null, (xva$0, xva$1) -> (T)ObjectUtils.firstNonNull(new Object[]{xva$0, xva$1}));
   }

   public static <T> VaultGearAttributeTypeMerger<T, Set<T>> asSet() {
      return asCollection(HashSet::new);
   }

   public static <T> VaultGearAttributeTypeMerger<T, List<T>> asList() {
      return asCollection(ArrayList::new);
   }

   public static <T, C extends Collection<T>> VaultGearAttributeTypeMerger<T, C> asCollection(Supplier<C> listSupplier) {
      return of(listSupplier, (list, element) -> {
         list.add(element);
         return list;
      });
   }
}
