package iskallia.vault.antique;

import iskallia.vault.VaultMod;
import iskallia.vault.antique.condition.DropConditionContext;
import iskallia.vault.config.AntiquesConfig;
import java.util.Comparator;
import java.util.stream.Stream;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

public class AntiqueRegistry {
   public static ResourceKey<Registry<Antique>> REGISTRY_KEY = ResourceKey.createRegistryKey(VaultMod.id("antiques"));
   private static IForgeRegistry<Antique> registry;

   public static IForgeRegistry<Antique> getRegistry() {
      return registry;
   }

   public static Stream<Antique> sorted() {
      Comparator<Antique> comparator = Comparator.comparing(antique -> {
         AntiquesConfig.Entry entry = antique.getConfig();
         return entry == null ? antique.getRegistryName().toString() : entry.getInfo().getName();
      });
      return registry.getValues().stream().sorted(comparator);
   }

   public static void buildRegistry(NewRegistryEvent event) {
      event.create(
         new RegistryBuilder().setName(REGISTRY_KEY.location()).setType(Antique.class).disableSaving().disableOverrides(),
         registry -> AntiqueRegistry.registry = registry
      );
   }

   public static Stream<Antique> getAntiquesMatchingCondition(DropConditionContext context) {
      return getRegistry().getValues().stream().filter(antique -> {
         AntiquesConfig.Entry cfgEntry = antique.getConfig();
         return cfgEntry != null && cfgEntry.getCondition().test(context);
      });
   }
}
