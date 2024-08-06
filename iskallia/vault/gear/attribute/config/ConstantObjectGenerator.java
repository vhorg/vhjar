package iskallia.vault.gear.attribute.config;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public class ConstantObjectGenerator<T, C extends Supplier<T>> extends ConfigurableAttributeGenerator<T, C> {
   private final Class<C> configObjectClass;

   public ConstantObjectGenerator(Class<C> configObjectClass) {
      this.configObjectClass = configObjectClass;
   }

   @Nullable
   @Override
   public Class<C> getConfigurationObjectClass() {
      return this.configObjectClass;
   }

   public T generateRandomValue(C object, Random random) {
      return object.get();
   }

   @Override
   public Optional<T> getMinimumValue(List<C> configurations) {
      return configurations.stream().findFirst().map(Supplier::get);
   }

   @Override
   public Optional<T> getMaximumValue(List<C> configurations) {
      return configurations.stream().findFirst().map(Supplier::get);
   }
}
