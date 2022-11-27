package iskallia.vault.gear.attribute.config;

import com.google.gson.annotations.Expose;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class CustomRegistryAttributeGenerator<T> extends ConfigurableAttributeGenerator<T, CustomRegistryAttributeGenerator.RegistryLookup> {
   private final Function<ResourceLocation, T> registryLookup;

   public CustomRegistryAttributeGenerator(Function<ResourceLocation, T> registryLookup) {
      this.registryLookup = registryLookup;
   }

   @Nullable
   @Override
   public Class<CustomRegistryAttributeGenerator.RegistryLookup> getConfigurationObjectClass() {
      return CustomRegistryAttributeGenerator.RegistryLookup.class;
   }

   public T generateRandomValue(CustomRegistryAttributeGenerator.RegistryLookup object, Random random) {
      return this.registryLookup.apply(object.registryKey);
   }

   public static class RegistryLookup {
      @Expose
      private ResourceLocation registryKey;
   }
}
