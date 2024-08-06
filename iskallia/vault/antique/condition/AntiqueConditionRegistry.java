package iskallia.vault.antique.condition;

import iskallia.vault.VaultMod;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

public class AntiqueConditionRegistry {
   public static ResourceKey<Registry<AntiqueCondition.Provider>> REGISTRY_KEY = ResourceKey.createRegistryKey(VaultMod.id("antique_conditions"));
   private static IForgeRegistry<AntiqueCondition.Provider> registry;

   public static IForgeRegistry<AntiqueCondition.Provider> getRegistry() {
      return registry;
   }

   public static void buildRegistry(NewRegistryEvent event) {
      event.create(
         new RegistryBuilder().setName(REGISTRY_KEY.location()).setType(AntiqueCondition.Provider.class).disableSaving().disableOverrides(),
         registry -> AntiqueConditionRegistry.registry = registry
      );
   }
}
