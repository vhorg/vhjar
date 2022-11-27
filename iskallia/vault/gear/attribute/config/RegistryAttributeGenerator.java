package iskallia.vault.gear.attribute.config;

import com.google.gson.annotations.Expose;
import java.util.Random;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;

public class RegistryAttributeGenerator<T extends IForgeRegistryEntry<T>> extends ConfigurableAttributeGenerator<T, RegistryAttributeGenerator.RegistryLookup> {
   private final IForgeRegistry<T> registry;

   public RegistryAttributeGenerator(IForgeRegistry<T> registry) {
      this.registry = registry;
   }

   @Nullable
   @Override
   public Class<RegistryAttributeGenerator.RegistryLookup> getConfigurationObjectClass() {
      return RegistryAttributeGenerator.RegistryLookup.class;
   }

   public T generateRandomValue(RegistryAttributeGenerator.RegistryLookup object, Random random) {
      return (T)this.registry.getValue(object.registryKey);
   }

   public static class RegistryLookup {
      @Expose
      private ResourceLocation registryKey;
   }
}
