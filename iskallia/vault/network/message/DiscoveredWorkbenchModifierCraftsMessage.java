package iskallia.vault.network.message;

import iskallia.vault.client.ClientDiscoveredEntriesData;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.NetworkEvent.Context;
import net.minecraftforge.registries.ForgeRegistries;

public class DiscoveredWorkbenchModifierCraftsMessage {
   private final Map<Item, Set<ResourceLocation>> itemCrafts;

   public DiscoveredWorkbenchModifierCraftsMessage(Map<Item, Set<ResourceLocation>> itemCrafts) {
      this.itemCrafts = itemCrafts;
   }

   public static void encode(DiscoveredWorkbenchModifierCraftsMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.itemCrafts.size());

      for (Item item : message.itemCrafts.keySet()) {
         buffer.writeResourceLocation(item.getRegistryName());
         buffer.writeCollection(message.itemCrafts.get(item), FriendlyByteBuf::writeResourceLocation);
      }
   }

   public static DiscoveredWorkbenchModifierCraftsMessage decode(FriendlyByteBuf buffer) {
      Map<Item, Set<ResourceLocation>> crafts = new LinkedHashMap<>();
      int size = buffer.readInt();

      for (int i = 0; i < size; i++) {
         Item item = (Item)ForgeRegistries.ITEMS.getValue(buffer.readResourceLocation());
         Set<ResourceLocation> keys = (Set<ResourceLocation>)buffer.readCollection(j -> new HashSet(), FriendlyByteBuf::readResourceLocation);
         if (item != null) {
            crafts.put(item, keys);
         }
      }

      return new DiscoveredWorkbenchModifierCraftsMessage(crafts);
   }

   public static void handle(DiscoveredWorkbenchModifierCraftsMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientDiscoveredEntriesData.WorkbenchCrafts.receiveMessage(message.itemCrafts));
      context.setPacketHandled(true);
   }
}
