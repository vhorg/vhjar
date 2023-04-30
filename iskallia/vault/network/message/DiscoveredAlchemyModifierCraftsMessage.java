package iskallia.vault.network.message;

import iskallia.vault.client.ClientDiscoveredEntriesData;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent.Context;

public class DiscoveredAlchemyModifierCraftsMessage {
   private final Set<ResourceLocation> itemCrafts;

   public DiscoveredAlchemyModifierCraftsMessage(Set<ResourceLocation> itemCrafts) {
      this.itemCrafts = itemCrafts;
   }

   public static void encode(DiscoveredAlchemyModifierCraftsMessage message, FriendlyByteBuf buffer) {
      buffer.writeCollection(message.itemCrafts, FriendlyByteBuf::writeResourceLocation);
   }

   public static DiscoveredAlchemyModifierCraftsMessage decode(FriendlyByteBuf buffer) {
      Set<ResourceLocation> crafts = (Set<ResourceLocation>)buffer.readCollection(j -> new HashSet(), FriendlyByteBuf::readResourceLocation);
      return new DiscoveredAlchemyModifierCraftsMessage(crafts);
   }

   public static void handle(DiscoveredAlchemyModifierCraftsMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientDiscoveredEntriesData.AlchemyCrafts.receiveMessage(message.itemCrafts));
      context.setPacketHandled(true);
   }
}
