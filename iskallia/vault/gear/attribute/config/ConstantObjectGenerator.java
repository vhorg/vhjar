package iskallia.vault.gear.attribute.config;

import java.util.Random;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

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
}
