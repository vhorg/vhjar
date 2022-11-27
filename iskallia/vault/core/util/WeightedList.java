package iskallia.vault.core.util;

import iskallia.vault.core.random.RandomSource;
import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.IntUnaryOperator;
import java.util.random.RandomGenerator;
import org.jetbrains.annotations.NotNull;

public class WeightedList<T> extends AbstractMap<T, Integer> {
   private final Map<T, Integer> delegate = new LinkedHashMap<>();

   @NotNull
   @Override
   public Set<Entry<T, Integer>> entrySet() {
      return this.delegate.entrySet();
   }

   public Integer put(T value, Integer weight) {
      return this.delegate.put(value, weight);
   }

   public WeightedList<T> add(T value, Integer weight) {
      int current = this.getOrDefault(value, Integer.valueOf(0));
      this.put(value, current + weight);
      return this;
   }

   public int getTotalWeight() {
      int sum = 0;

      for (int weight : this.values()) {
         sum += Math.max(weight, 0);
      }

      return sum;
   }

   public Optional<T> getRandom() {
      return this.getRandom(new Random());
   }

   public Optional<T> getRandom(RandomGenerator random) {
      return this.getRandom(random::nextInt);
   }

   public Optional<T> getRandom(RandomSource random) {
      return this.getRandom(random::nextInt);
   }

   public Optional<T> getRandom(IntUnaryOperator random) {
      int total = this.getTotalWeight();
      if (total <= 0) {
         return Optional.empty();
      } else {
         int index = random.applyAsInt(total);

         for (Entry<T, Integer> entry : this.delegate.entrySet()) {
            T value = entry.getKey();
            int weight = Math.max(entry.getValue(), 0);
            if (index < weight) {
               return Optional.ofNullable(value);
            }

            index -= weight;
         }

         return Optional.empty();
      }
   }
}
