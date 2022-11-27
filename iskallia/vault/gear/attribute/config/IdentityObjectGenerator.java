package iskallia.vault.gear.attribute.config;

import java.util.Random;
import org.jetbrains.annotations.Nullable;

public class IdentityObjectGenerator<T> extends ConfigurableAttributeGenerator<T, T> {
   private final Class<T> configObjectClass;

   public IdentityObjectGenerator(Class<T> configObjectClass) {
      this.configObjectClass = configObjectClass;
   }

   @Nullable
   @Override
   public Class<T> getConfigurationObjectClass() {
      return this.configObjectClass;
   }

   @Override
   public T generateRandomValue(T object, Random random) {
      return object;
   }
}
