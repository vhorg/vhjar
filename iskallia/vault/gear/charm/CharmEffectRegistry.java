package iskallia.vault.gear.charm;

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

public class CharmEffectRegistry {
   public static ResourceKey<Registry<CharmEffect<?>>> CHARM_REGISTRY_KEY = ResourceKey.createRegistryKey(VaultMod.id("charm"));
   private static IForgeRegistry<CharmEffect<?>> charmRegistry;

   public static IForgeRegistry<CharmEffect<?>> getRegistry() {
      return charmRegistry;
   }

   @Nullable
   public static CharmEffect<?> getEffect(ResourceLocation key) {
      return (CharmEffect<?>)charmRegistry.getValue(key);
   }

   public static List<CharmEffect<?>> getOrderedEntries() {
      return Streams.stream(getRegistry()).sorted(Comparator.comparing(set -> set.getRegistryName().getPath())).toList();
   }

   public static List<ResourceLocation> getOrderedKeys() {
      return getOrderedEntries().stream().<ResourceLocation>map(ForgeRegistryEntry::getRegistryName).toList();
   }

   public static void buildRegistry(NewRegistryEvent event) {
      event.create(
         new RegistryBuilder().setName(CHARM_REGISTRY_KEY.location()).setType(MiscUtils.cast(CharmEffect.class)).disableSaving().disableOverrides(),
         registry -> charmRegistry = registry
      );
   }
}
