package iskallia.vault.client;

import iskallia.vault.network.message.transmog.DiscoveredEntriesMessage;
import iskallia.vault.util.function.ObservableSupplier;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ClientDiscoveredEntriesData {
   public static void receiveMessage(DiscoveredEntriesMessage message) {
      switch (message.type()) {
         case MODELS:
            ClientDiscoveredEntriesData.Models.receiveMessage(message.discoveredEntries());
            break;
         case TRINKETS:
            ClientDiscoveredEntriesData.Trinkets.receiveMessage(message.discoveredEntries());
      }
   }

   public static class Models {
      private static final Set<ResourceLocation> discoveredModels = new HashSet<>();

      public static Set<ResourceLocation> getDiscoveredModels() {
         return new HashSet<>(discoveredModels);
      }

      public static ObservableSupplier<Set<ResourceLocation>> getObserverModels() {
         return ObservableSupplier.of(ClientDiscoveredEntriesData.Models::getDiscoveredModels, (modelSet, newModelSet) -> modelSet.size() == newModelSet.size());
      }

      private static void receiveMessage(Set<ResourceLocation> models) {
         discoveredModels.clear();
         discoveredModels.addAll(models);
      }
   }

   public static class Trinkets {
      private static final Set<ResourceLocation> discoveredTrinkets = new HashSet<>();

      public static Set<ResourceLocation> getDiscoveredTrinkets() {
         return new HashSet<>(discoveredTrinkets);
      }

      public static ObservableSupplier<Set<ResourceLocation>> getObserverTrinkets() {
         return ObservableSupplier.of(
            ClientDiscoveredEntriesData.Trinkets::getDiscoveredTrinkets, (modelSet, newModelSet) -> modelSet.size() == newModelSet.size()
         );
      }

      private static void receiveMessage(Set<ResourceLocation> trinkets) {
         discoveredTrinkets.clear();
         discoveredTrinkets.addAll(trinkets);
      }
   }

   public static class WorkbenchCrafts {
      private static Map<Item, Set<ResourceLocation>> discoveredWorkbenchCrafts = new HashMap<>();

      public static Set<ResourceLocation> getDiscoveredWorkbenchCrafts(Item gearItem) {
         return new HashSet<>(discoveredWorkbenchCrafts.getOrDefault(gearItem, Collections.emptySet()));
      }

      public static ObservableSupplier<Set<ResourceLocation>> getObserverWorkbenchCrafts(Item gearItem) {
         return ObservableSupplier.of(() -> getDiscoveredWorkbenchCrafts(gearItem), (crafts, newCrafts) -> crafts.size() == newCrafts.size());
      }

      public static void receiveMessage(Map<Item, Set<ResourceLocation>> workbenchCrafts) {
         discoveredWorkbenchCrafts = workbenchCrafts;
      }
   }
}
