package iskallia.vault.gear.attribute.config;

import java.util.Random;

public abstract class NumberRangeGenerator<T extends Number, C extends NumberRangeGenerator.NumberRange<T>> extends ConfigurableAttributeGenerator<T, C> {
   public T generateRandomValue(C object, Random random) {
      return object.generateNumber(random);
   }

   public abstract static class NumberRange<T extends Number> {
      public abstract T generateNumber(Random var1);
   }
}
