package iskallia.vault.gear.trinket;

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

public class TrinketEffectRegistry {
   public static ResourceKey<Registry<TrinketEffect<?>>> TRINKET_REGISTRY_KEY = ResourceKey.createRegistryKey(VaultMod.id("trinket"));
   private static IForgeRegistry<TrinketEffect<?>> trinketRegistry;

   public static IForgeRegistry<TrinketEffect<?>> getRegistry() {
      return trinketRegistry;
   }

   @Nullable
   public static TrinketEffect<?> getEffect(ResourceLocation key) {
      return (TrinketEffect<?>)trinketRegistry.getValue(key);
   }

   public static List<TrinketEffect<?>> getOrderedEntries() {
      return Streams.stream(getRegistry()).sorted(Comparator.comparing(set -> set.getRegistryName().getPath())).toList();
   }

   public static List<ResourceLocation> getOrderedKeys() {
      return getOrderedEntries().stream().<ResourceLocation>map(ForgeRegistryEntry::getRegistryName).toList();
   }

   public static void buildRegistry(NewRegistryEvent event) {
      event.create(
         new RegistryBuilder().setName(TRINKET_REGISTRY_KEY.location()).setType(MiscUtils.cast(TrinketEffect.class)).disableSaving().disableOverrides(),
         registry -> trinketRegistry = registry
      );
   }
}
