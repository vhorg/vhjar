package iskallia.vault.item.gear.attribute;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import iskallia.vault.util.gson.IgnoreEmpty;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.ToIntBiFunction;
import java.util.stream.IntStream;
import net.minecraft.item.ItemStack;

public abstract class PooledAttribute<T> extends ItemAttribute.Instance<T> {
   protected PooledAttribute() {
   }

   protected PooledAttribute(ItemAttribute.Modifier<T> modifier) {
      super(modifier);
   }

   public abstract static class Generator<T, O extends PooledAttribute.Generator.Operator<T>> implements ItemAttribute.Instance.Generator<T> {
      @Expose
      public List<PooledAttribute.Pool<T, O>> pools = new ArrayList<>();
      @Expose
      public O collector;

      public PooledAttribute.Generator<T, O> add(T base, PooledAttribute.Rolls rolls, Consumer<PooledAttribute.Pool<T, O>> pool) {
         if (this.pools == null) {
            this.pools = new ArrayList<>();
         }

         PooledAttribute.Pool<T, O> generated = new PooledAttribute.Pool<>(base, rolls);
         this.pools.add(generated);
         pool.accept(generated);
         return this;
      }

      public PooledAttribute.Generator<T, O> collect(O collector) {
         this.collector = collector;
         return this;
      }

      public abstract T getDefaultValue(Random var1);

      @Override
      public T generate(ItemStack stack, Random random) {
         if (this.pools.size() == 0) {
            return this.getDefaultValue(random);
         } else {
            T value = this.pools.get(0).generate(random);

            for (int i = 1; i < this.pools.size(); i++) {
               value = this.collector.apply(value, this.pools.get(i).generate(random));
            }

            return value;
         }
      }

      public abstract static class Operator<T> extends PooledAttribute.Pool.Operator<T> {
      }
   }

   public static class Pool<T, O extends PooledAttribute.Pool.Operator<T>> {
      @Expose
      public T base;
      @Expose
      public PooledAttribute.Rolls rolls;
      @Expose
      public List<PooledAttribute.Pool.Entry<T, O>> entries = new ArrayList<>();
      private int totalWeight;

      public Pool(T base, PooledAttribute.Rolls rolls) {
         this.base = base;
         this.rolls = rolls;
      }

      public PooledAttribute.Pool<T, O> add(T value, O operator, int weight) {
         if (this.entries == null) {
            this.entries = new ArrayList<>();
         }

         PooledAttribute.Pool.Entry<T, O> entry = new PooledAttribute.Pool.Entry<>(value, operator, weight);
         this.entries.add(entry);
         return this;
      }

      public T generate(Random random) {
         if (!this.entries.isEmpty() && !this.rolls.type.equals(PooledAttribute.Rolls.Type.EMPTY.name)) {
            int roll = this.rolls.getRolls(random);
            T value = this.base;

            for (int i = 0; i < roll; i++) {
               PooledAttribute.Pool.Entry<T, O> entry = this.getRandom(random);
               value = entry.operator.apply(value, entry.value);
            }

            return value;
         } else {
            return this.base;
         }
      }

      public PooledAttribute.Pool.Entry<T, O> getRandom(Random random) {
         return this.entries.size() == 0 ? null : this.getWeightedAt(random.nextInt(this.getTotalWeight()));
      }

      public PooledAttribute.Pool.Entry<T, O> getWeightedAt(int index) {
         PooledAttribute.Pool.Entry<T, O> current = null;

         for (PooledAttribute.Pool.Entry<T, O> entry : this.entries) {
            current = entry;
            index -= entry.weight;
            if (index < 0) {
               break;
            }
         }

         return current;
      }

      private int getTotalWeight() {
         if (this.totalWeight == 0) {
            this.entries.forEach(entry -> this.totalWeight = this.totalWeight + entry.weight);
         }

         return this.totalWeight;
      }

      public static class Entry<T, O extends PooledAttribute.Pool.Operator<T>> {
         @Expose
         public final T value;
         @Expose
         public final O operator;
         @Expose
         public final int weight;

         public Entry(T value, O operator, int weight) {
            this.value = value;
            this.operator = operator;
            this.weight = weight;
         }
      }

      public abstract static class Operator<T> {
         public abstract T apply(T var1, T var2);
      }
   }

   public static class Rolls {
      @Expose
      public String type;
      @Expose
      @JsonAdapter(IgnoreEmpty.IntegerAdapter.class)
      public int value;
      @Expose
      @JsonAdapter(IgnoreEmpty.IntegerAdapter.class)
      public int min;
      @Expose
      @JsonAdapter(IgnoreEmpty.IntegerAdapter.class)
      public int max;
      @Expose
      @JsonAdapter(IgnoreEmpty.DoubleAdapter.class)
      public double chance;
      @Expose
      @JsonAdapter(IgnoreEmpty.IntegerAdapter.class)
      public int trials;
      @Expose
      @JsonAdapter(IgnoreEmpty.DoubleAdapter.class)
      public double probability;

      public static PooledAttribute.Rolls ofEmpty() {
         PooledAttribute.Rolls rolls = new PooledAttribute.Rolls();
         rolls.type = PooledAttribute.Rolls.Type.EMPTY.name;
         return rolls;
      }

      public static PooledAttribute.Rolls ofConstant(int value) {
         PooledAttribute.Rolls rolls = new PooledAttribute.Rolls();
         rolls.type = PooledAttribute.Rolls.Type.CONSTANT.name;
         rolls.value = value;
         return rolls;
      }

      public static PooledAttribute.Rolls ofUniform(int min, int max) {
         PooledAttribute.Rolls rolls = new PooledAttribute.Rolls();
         rolls.type = PooledAttribute.Rolls.Type.UNIFORM.name;
         rolls.min = min;
         rolls.max = max;
         return rolls;
      }

      public static PooledAttribute.Rolls ofChance(double chance, int value) {
         PooledAttribute.Rolls rolls = new PooledAttribute.Rolls();
         rolls.type = PooledAttribute.Rolls.Type.CHANCE.name;
         rolls.value = value;
         rolls.chance = chance;
         return rolls;
      }

      public static PooledAttribute.Rolls ofBinomial(int trials, double probability) {
         PooledAttribute.Rolls rolls = new PooledAttribute.Rolls();
         rolls.type = PooledAttribute.Rolls.Type.BINOMIAL.name;
         rolls.trials = trials;
         rolls.probability = probability;
         return rolls;
      }

      public int getRolls(Random random) {
         PooledAttribute.Rolls.Type type = PooledAttribute.Rolls.Type.getByName(this.type);
         if (type == null) {
            throw new IllegalStateException("Unknown rolls type \"" + this.type + "\"");
         } else {
            return type.function.applyAsInt(this, random);
         }
      }

      public static enum Type {
         EMPTY("empty", (rolls, random) -> 0),
         CONSTANT("constant", (rolls, random) -> rolls.value),
         UNIFORM("uniform", (rolls, random) -> random.nextInt(rolls.max - rolls.min + 1) + rolls.min),
         CHANCE("chance", (rolls, random) -> random.nextDouble() < rolls.chance ? rolls.value : 0),
         BINOMIAL("binomial", (rolls, random) -> (int)IntStream.range(0, rolls.trials).filter(i -> random.nextDouble() < rolls.probability).count());

         public final String name;
         private final ToIntBiFunction<PooledAttribute.Rolls, Random> function;

         private Type(String name, ToIntBiFunction<PooledAttribute.Rolls, Random> function) {
            this.name = name;
            this.function = function;
         }

         public static PooledAttribute.Rolls.Type getByName(String name) {
            for (PooledAttribute.Rolls.Type value : values()) {
               if (value.name.equals(name)) {
                  return value;
               }
            }

            return null;
         }
      }
   }
}
