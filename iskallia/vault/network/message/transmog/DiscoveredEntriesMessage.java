package iskallia.vault.network.message.transmog;

import iskallia.vault.client.ClientDiscoveredEntriesData;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent.Context;

public record DiscoveredEntriesMessage(DiscoveredEntriesMessage.Type type, Set<ResourceLocation> discoveredEntries) {
   public static void encode(DiscoveredEntriesMessage message, FriendlyByteBuf buffer) {
      buffer.writeEnum(message.type);
      buffer.writeInt(message.discoveredEntries.size());
      message.discoveredEntries.forEach(buffer::writeResourceLocation);
   }

   public static DiscoveredEntriesMessage decode(FriendlyByteBuf buffer) {
      DiscoveredEntriesMessage.Type type = (DiscoveredEntriesMessage.Type)buffer.readEnum(DiscoveredEntriesMessage.Type.class);
      int size = buffer.readInt();
      Set<ResourceLocation> ids = new HashSet<>();

      for (int i = 0; i < size; i++) {
         ids.add(buffer.readResourceLocation());
      }

      return new DiscoveredEntriesMessage(type, ids);
   }

   public static void handle(DiscoveredEntriesMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientDiscoveredEntriesData.receiveMessage(message));
      context.setPacketHandled(true);
   }

   public static enum Type {
      MODELS,
      TRINKETS;
   }
}
