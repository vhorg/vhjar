package iskallia.vault.util.data;

import com.google.gson.annotations.Expose;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class WeightedDoubleList<T> extends AbstractList<WeightedDoubleList.Entry<T>> implements RandomListAccess<T> {
   @Expose
   private final List<WeightedDoubleList.Entry<T>> entries = new ArrayList<>();

   public WeightedDoubleList<T> add(T value, double weight) {
      this.add(new WeightedDoubleList.Entry<>(value, weight));
      return this;
   }

   @Override
   public int size() {
      return this.entries.size();
   }

   public WeightedDoubleList.Entry<T> get(int index) {
      return this.entries.get(index);
   }

   public boolean add(WeightedDoubleList.Entry<T> entry) {
      return this.entries.add(entry);
   }

   public WeightedDoubleList.Entry<T> remove(int index) {
      return this.entries.remove(index);
   }

   @Override
   public boolean remove(Object o) {
      return this.entries.remove(o);
   }

   @Override
   public boolean removeEntry(T t) {
      return this.removeIf(entry -> entry.value.equals(t));
   }

   @Override
   public boolean removeAll(Collection<?> c) {
      return this.entries.removeAll(c);
   }

   @Override
   public boolean removeIf(Predicate<? super WeightedDoubleList.Entry<T>> filter) {
      return this.entries.removeIf(filter);
   }

   public double getTotalWeight() {
      return this.entries.stream().mapToDouble(entry -> entry.weight).sum();
   }

   @Nullable
   @Override
   public T getRandom(Random random) {
      double totalWeight = this.getTotalWeight();
      return totalWeight <= 0.0 ? null : this.getWeightedAt(random.nextDouble() * totalWeight);
   }

   private T getWeightedAt(double weight) {
      for (WeightedDoubleList.Entry<T> e : this.entries) {
         weight -= e.weight;
         if (weight < 0.0) {
            return e.value;
         }
      }

      return null;
   }

   public WeightedDoubleList<T> copy() {
      WeightedDoubleList<T> copy = new WeightedDoubleList<>();
      this.entries.forEach(entry -> copy.add(entry.value, entry.weight));
      return copy;
   }

   public WeightedDoubleList<T> copyFiltered(Predicate<T> filter) {
      WeightedDoubleList<T> copy = new WeightedDoubleList<>();
      this.entries.forEach(entry -> {
         if (filter.test(entry.value)) {
            copy.add((WeightedDoubleList.Entry<T>)entry);
         }
      });
      return copy;
   }

   public boolean containsValue(T value) {
      return this.stream().map(entry -> entry.value).anyMatch(t -> t.equals(value));
   }

   @Override
   public void forEach(BiConsumer<T, Number> weightEntryConsumer) {
      this.forEach(entry -> weightEntryConsumer.accept(entry.value, entry.weight));
   }

   public static class Entry<T> {
      @Expose
      public T value;
      @Expose
      public double weight;

      public Entry(T value, double weight) {
         this.value = value;
         this.weight = weight;
      }
   }
}
