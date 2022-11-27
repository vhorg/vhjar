package iskallia.vault.gear.attribute.config;

import java.util.Random;
import org.jetbrains.annotations.Nullable;

public class EnumAttributeGenerator<T extends Enum<T>> extends ConfigurableAttributeGenerator<T, Integer> {
   private final Class<T> enumClass;

   public EnumAttributeGenerator(Class<T> enumClass) {
      this.enumClass = enumClass;
   }

   @Nullable
   @Override
   public Class<Integer> getConfigurationObjectClass() {
      return Integer.class;
   }

   public T generateRandomValue(Integer object, Random random) {
      return this.enumClass.getEnumConstants()[object];
   }
}
