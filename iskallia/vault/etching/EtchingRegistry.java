package iskallia.vault.etching;

import com.google.common.collect.Streams;
import iskallia.vault.VaultMod;
import iskallia.vault.util.MiscUtils;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

public class EtchingRegistry {
   public static ResourceKey<Registry<EtchingSet<?>>> ETCHING_REGISTRY_KEY = ResourceKey.createRegistryKey(VaultMod.id("etching"));
   private static IForgeRegistry<EtchingSet<?>> etchingRegistry;

   public static IForgeRegistry<EtchingSet<?>> getRegistry() {
      return etchingRegistry;
   }

   @Nullable
   public static EtchingSet<?> getEtchingSet(ResourceLocation key) {
      return (EtchingSet<?>)etchingRegistry.getValue(key);
   }

   public static List<EtchingSet<?>> getOrderedEntries() {
      return Streams.stream(getRegistry()).sorted(Comparator.comparing(set -> set.getRegistryName().getPath())).toList();
   }

   public static List<ResourceLocation> getOrderedKeys() {
      return getOrderedEntries().stream().<ResourceLocation>map(ForgeRegistryEntry::getRegistryName).toList();
   }

   public static void buildRegistry(NewRegistryEvent event) {
      event.create(
         new RegistryBuilder().setName(ETCHING_REGISTRY_KEY.location()).setType(MiscUtils.cast(EtchingSet.class)).disableSaving().disableOverrides(),
         registry -> etchingRegistry = registry
      );
   }
}
