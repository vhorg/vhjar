package iskallia.vault.init;

import iskallia.vault.gear.attribute.config.BooleanFlagGenerator;
import iskallia.vault.gear.attribute.config.ConfigurableAttributeGenerator;
import iskallia.vault.gear.attribute.config.CustomRegistryAttributeGenerator;
import iskallia.vault.gear.attribute.config.DoubleAttributeGenerator;
import iskallia.vault.gear.attribute.config.EnumAttributeGenerator;
import iskallia.vault.gear.attribute.config.FloatAttributeGenerator;
import iskallia.vault.gear.attribute.config.IdentityObjectGenerator;
import iskallia.vault.gear.attribute.config.IntegerAttributeGenerator;
import iskallia.vault.gear.attribute.config.RegistryAttributeGenerator;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class ModGearAttributeGenerators {
   public static IntegerAttributeGenerator intRange() {
      return new IntegerAttributeGenerator();
   }

   public static FloatAttributeGenerator floatRange() {
      return new FloatAttributeGenerator();
   }

   public static DoubleAttributeGenerator doubleRange() {
      return new DoubleAttributeGenerator();
   }

   public static BooleanFlagGenerator booleanFlag() {
      return new BooleanFlagGenerator();
   }

   public static IdentityObjectGenerator<String> stringConstant() {
      return new IdentityObjectGenerator<>(String.class);
   }

   public static <T extends IForgeRegistryEntry<T>> RegistryAttributeGenerator<T> registry(IForgeRegistry<T> registry) {
      return new RegistryAttributeGenerator<>(registry);
   }

   public static <T> CustomRegistryAttributeGenerator<T> customRegistry(Function<ResourceLocation, T> registryLookup) {
      return new CustomRegistryAttributeGenerator<>(registryLookup);
   }

   public static <T extends Enum<T>> EnumAttributeGenerator<T> enumGenerator(Class<T> enumClass) {
      return new EnumAttributeGenerator<>(enumClass);
   }

   public static <T, C> ConfigurableAttributeGenerator<T, C> noneGenerator() {
      return new ConfigurableAttributeGenerator<T, C>() {
         @Override
         public Class<C> getConfigurationObjectClass() {
            return null;
         }

         @Override
         public T generateRandomValue(C object, Random random) {
            return null;
         }
      };
   }
}
