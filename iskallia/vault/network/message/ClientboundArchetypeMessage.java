package iskallia.vault.network.message;

import iskallia.vault.client.ClientArchetypeData;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent.Context;

public class ClientboundArchetypeMessage {
   private final ResourceLocation resourceLocation;

   public ClientboundArchetypeMessage(ResourceLocation resourceLocation) {
      this.resourceLocation = resourceLocation;
   }

   public static void encode(ClientboundArchetypeMessage message, FriendlyByteBuf buffer) {
      buffer.writeResourceLocation(message.resourceLocation);
   }

   public static ClientboundArchetypeMessage decode(FriendlyByteBuf buffer) {
      return new ClientboundArchetypeMessage(buffer.readResourceLocation());
   }

   public static void handle(ClientboundArchetypeMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientArchetypeData.setCurrentArchetype(message.resourceLocation));
      context.setPacketHandled(true);
   }
}
