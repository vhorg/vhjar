package iskallia.vault.gear.attribute.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

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

   @Nullable
   public MutableComponent getConfigDisplay(VaultGearModifierReader<T> reader, CustomRegistryAttributeGenerator.RegistryLookup object) {
      return null;
   }

   public static class RegistryLookup {
      @Expose
      private ResourceLocation registryKey;
   }
}
