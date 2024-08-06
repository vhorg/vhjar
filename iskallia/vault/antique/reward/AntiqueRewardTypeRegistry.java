package iskallia.vault.antique.reward;

import iskallia.vault.VaultMod;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

public class AntiqueRewardTypeRegistry {
   public static ResourceKey<Registry<AntiqueReward.Provider>> REGISTRY_KEY = ResourceKey.createRegistryKey(VaultMod.id("antique_reward_types"));
   private static IForgeRegistry<AntiqueReward.Provider> registry;

   public static IForgeRegistry<AntiqueReward.Provider> getRegistry() {
      return registry;
   }

   public static void buildRegistry(NewRegistryEvent event) {
      event.create(
         new RegistryBuilder().setName(REGISTRY_KEY.location()).setType(AntiqueReward.Provider.class).disableSaving().disableOverrides(),
         registry -> AntiqueRewardTypeRegistry.registry = registry
      );
   }
}
