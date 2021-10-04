package iskallia.vault.util.data;

import com.google.gson.annotations.Expose;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class WeightedList<T> extends AbstractList<WeightedList.Entry<T>> implements RandomListAccess<T> {
   @Expose
   private final List<WeightedList.Entry<T>> entries = new ArrayList<>();

   public WeightedList() {
   }

   public WeightedList(Map<T, Integer> map) {
      this();
      map.forEach(this::add);
   }

   public WeightedList<T> add(T value, int weight) {
      this.add(new WeightedList.Entry<>(value, weight));
      return this;
   }

   @Override
   public int size() {
      return this.entries.size();
   }

   public WeightedList.Entry<T> get(int index) {
      return this.entries.get(index);
   }

   public boolean add(WeightedList.Entry<T> entry) {
      return this.entries.add(entry);
   }

   public WeightedList.Entry<T> remove(int index) {
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
   public boolean removeIf(Predicate<? super WeightedList.Entry<T>> filter) {
      return this.entries.removeIf(filter);
   }

   public int getTotalWeight() {
      return this.entries.stream().mapToInt(entry -> entry.weight).sum();
   }

   @Nullable
   @Override
   public T getRandom(Random random) {
      int totalWeight = this.getTotalWeight();
      return totalWeight <= 0 ? null : this.getWeightedAt(random.nextInt(totalWeight));
   }

   private T getWeightedAt(int index) {
      for (WeightedList.Entry<T> e : this.entries) {
         index -= e.weight;
         if (index < 0) {
            return e.value;
         }
      }

      return null;
   }

   public WeightedList<T> copy() {
      WeightedList<T> copy = new WeightedList<>();
      this.entries.forEach(entry -> copy.add(entry.value, entry.weight));
      return copy;
   }

   public WeightedList<T> copyFiltered(Predicate<T> filter) {
      WeightedList<T> copy = new WeightedList<>();
      this.entries.forEach(entry -> {
         if (filter.test(entry.value)) {
            copy.add((WeightedList.Entry<T>)entry);
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
      public int weight;

      public Entry(T value, int weight) {
         this.value = value;
         this.weight = weight;
      }
   }
}
