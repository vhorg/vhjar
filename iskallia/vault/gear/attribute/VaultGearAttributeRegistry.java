package iskallia.vault.gear.attribute;

import iskallia.vault.VaultMod;
import iskallia.vault.util.MiscUtils;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

public class VaultGearAttributeRegistry {
   public static ResourceKey<Registry<VaultGearAttribute<?>>> ATTRIBUTE_REGISTRY_KEY = ResourceKey.createRegistryKey(VaultMod.id("gear_attributes"));
   private static IForgeRegistry<VaultGearAttribute<?>> attributeRegistry;

   public static IForgeRegistry<VaultGearAttribute<?>> getRegistry() {
      return attributeRegistry;
   }

   @Nullable
   public static VaultGearAttribute<?> getAttribute(ResourceLocation key) {
      return (VaultGearAttribute<?>)attributeRegistry.getValue(key);
   }

   public static void buildRegistry(NewRegistryEvent event) {
      event.create(
         new RegistryBuilder().setName(ATTRIBUTE_REGISTRY_KEY.location()).setType(MiscUtils.cast(VaultGearAttribute.class)).disableSaving().disableOverrides(),
         registry -> attributeRegistry = registry
      );
   }
}
