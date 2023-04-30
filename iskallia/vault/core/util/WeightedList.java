package iskallia.vault.core.util;

import iskallia.vault.core.random.RandomSource;
import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.DoubleUnaryOperator;
import java.util.random.RandomGenerator;
import org.jetbrains.annotations.NotNull;

public class WeightedList<T> extends AbstractMap<T, Double> {
   private final Map<T, Double> delegate = new LinkedHashMap<>();

   @NotNull
   @Override
   public Set<Entry<T, Double>> entrySet() {
      return this.delegate.entrySet();
   }

   public Double put(T value, Double weight) {
      return this.delegate.put(value, weight);
   }

   public Double put(T value, Number weight) {
      return this.put(value, weight.doubleValue());
   }

   public WeightedList<T> add(T value, Double weight) {
      double current = this.getOrDefault(value, Double.valueOf(0.0));
      this.put(value, current + weight);
      return this;
   }

   public WeightedList<T> add(T value, Number weight) {
      return this.add(value, weight.doubleValue());
   }

   public double getTotalWeight() {
      double sum = 0.0;

      for (double weight : this.values()) {
         sum += Math.max(weight, 0.0);
      }

      return sum;
   }

   public Optional<T> getRandom() {
      return this.getRandom(new Random());
   }

   public Optional<T> getRandom(RandomGenerator random) {
      return this.getRandom(random::nextDouble);
   }

   public Optional<T> getRandom(RandomSource random) {
      return this.getRandom(random::nextDouble);
   }

   public Optional<T> getRandom(DoubleUnaryOperator random) {
      double total = this.getTotalWeight();
      if (total <= 0.0) {
         return Optional.empty();
      } else {
         double index = random.applyAsDouble(total);

         for (Entry<T, Double> entry : this.delegate.entrySet()) {
            T value = entry.getKey();
            double weight = Math.max(entry.getValue(), 0.0);
            if (index < weight) {
               return Optional.ofNullable(value);
            }

            index -= weight;
         }

         return Optional.empty();
      }
   }
}
