package iskallia.vault.core.util;

import iskallia.vault.core.random.RandomSource;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Predicate;
import java.util.random.RandomGenerator;

public class WeightedTree<T> {
   private final WeightedList<Object> children = new WeightedList<>();

   public WeightedList<Object> getChildren() {
      return this.children;
   }

   public WeightedList<T> flatten() {
      return null;
   }

   public <W extends WeightedTree<T>> W addLeaf(T value, double weight) {
      this.children.add(value, weight);
      return (W)this;
   }

   public WeightedTree<T> addTree(WeightedTree<T> tree, double weight) {
      this.children.add(tree, weight);
      return this;
   }

   public WeightedTree<T> addTree(Consumer<WeightedTree<T>> root, double weight) {
      WeightedTree<T> tree = new WeightedTree<>();
      this.children.add(tree, weight);
      root.accept(tree);
      return this;
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
      Optional<Object> opt = this.children.getRandom(random);
      if (opt.isEmpty()) {
         return Optional.empty();
      } else {
         Object value = opt.get();
         return value instanceof WeightedTree ? ((WeightedTree)value).getRandom(random) : opt;
      }
   }

   public Optional<T> getRandom(DoubleUnaryOperator random, BiConsumer<WeightedList<Object>, Object> step) {
      Optional<Object> opt = this.children.getRandom(random);
      if (opt.isEmpty()) {
         return Optional.empty();
      } else {
         Object value = opt.get();
         step.accept(this.children, value);
         return value instanceof WeightedTree ? ((WeightedTree)value).getRandom(random) : opt;
      }
   }

   public void iterate(Predicate<T> action) {
      this.iterate(this, action);
   }

   protected boolean iterate(Object node, Predicate<T> action) {
      if (node instanceof WeightedTree<?> tree) {
         for (Object child : tree.children.keySet()) {
            if (!this.iterate(child, action)) {
               return false;
            }
         }

         return true;
      } else {
         return action.test((T)node);
      }
   }
}
