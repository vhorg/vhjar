package iskallia.vault.network.message;

import iskallia.vault.client.ClientDiscoveredEntriesData;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class DiscoveredAlchemyEffectsMessage {
   private final Set<String> itemCrafts;

   public DiscoveredAlchemyEffectsMessage(Set<String> itemCrafts) {
      this.itemCrafts = itemCrafts;
   }

   public static void encode(DiscoveredAlchemyEffectsMessage message, FriendlyByteBuf buffer) {
      buffer.writeCollection(message.itemCrafts, FriendlyByteBuf::writeUtf);
   }

   public static DiscoveredAlchemyEffectsMessage decode(FriendlyByteBuf buffer) {
      Set<String> crafts = (Set<String>)buffer.readCollection(j -> new HashSet(), FriendlyByteBuf::readUtf);
      return new DiscoveredAlchemyEffectsMessage(crafts);
   }

   public static void handle(DiscoveredAlchemyEffectsMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientDiscoveredEntriesData.AlchemyEffects.receiveMessage(message.itemCrafts));
      context.setPacketHandled(true);
   }
}
