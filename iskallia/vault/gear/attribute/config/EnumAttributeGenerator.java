package iskallia.vault.gear.attribute.config;

import iskallia.vault.gear.reader.VaultGearModifierReader;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.network.chat.MutableComponent;

@Deprecated(
   forRemoval = true
)
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

   @Nullable
   public MutableComponent getConfigDisplay(VaultGearModifierReader<T> reader, Integer object) {
      return null;
   }

   @Override
   public Optional<T> getMinimumValue(List<Integer> configurations) {
      return Optional.empty();
   }

   @Override
   public Optional<T> getMaximumValue(List<Integer> configurations) {
      return Optional.empty();
   }
}
