package iskallia.vault.gear.modification;

import iskallia.vault.VaultMod;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

public class GearModificationRegistry {
   public static ResourceKey<Registry<GearModification>> MODIFICATION_REGISTRY_KEY = ResourceKey.createRegistryKey(VaultMod.id("gear_modification"));
   private static IForgeRegistry<GearModification> modificationRegistry;

   public static IForgeRegistry<GearModification> getRegistry() {
      return modificationRegistry;
   }

   @Nullable
   public static GearModification getModification(ResourceLocation id) {
      return (GearModification)modificationRegistry.getValue(id);
   }

   public static void buildRegistry(NewRegistryEvent event) {
      event.create(
         new RegistryBuilder().setName(MODIFICATION_REGISTRY_KEY.location()).setType(GearModification.class).disableSaving().disableOverrides(),
         registry -> modificationRegistry = registry
      );
   }
}
