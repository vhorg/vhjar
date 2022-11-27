package iskallia.vault.skill.archetype;

import iskallia.vault.VaultMod;
import iskallia.vault.util.MiscUtils;
import java.util.Collection;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

public class ArchetypeRegistry {
   public static ResourceKey<Registry<AbstractArchetypeConfig>> ARCHETYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(VaultMod.id("archetypes"));
   private static IForgeRegistry<AbstractArchetype<?>> forgeRegistry;
   private static AbstractArchetype<?> defaultArchetype;

   public static IForgeRegistry<AbstractArchetype<?>> getRegistry() {
      return forgeRegistry;
   }

   public static AbstractArchetype<?> getArchetype(ResourceLocation id) {
      AbstractArchetype<?> result = (AbstractArchetype<?>)forgeRegistry.getValue(id);
      return result == null ? defaultArchetype : result;
   }

   public static Collection<AbstractArchetype<?>> getArchetypes() {
      return forgeRegistry.getValues();
   }

   public static void registerDefaultArchetype(AbstractArchetype<?> defaultArchetype) {
      ArchetypeRegistry.defaultArchetype = defaultArchetype;
   }

   public static AbstractArchetype<?> getDefaultArchetype() {
      return defaultArchetype;
   }

   public static void buildRegistry(NewRegistryEvent event) {
      event.create(
         new RegistryBuilder().setName(ARCHETYPE_REGISTRY_KEY.location()).setType(MiscUtils.cast(AbstractArchetype.class)).disableSaving().disableOverrides(),
         registry -> forgeRegistry = registry
      );
   }
}
