package iskallia.vault.network.message.transmog;

import iskallia.vault.container.TransmogTableContainer;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public record SelectDiscoveredModelMessage(ResourceLocation modelId) {
   public static void encode(SelectDiscoveredModelMessage message, FriendlyByteBuf buffer) {
      buffer.writeResourceLocation(message.modelId);
   }

   public static SelectDiscoveredModelMessage decode(FriendlyByteBuf buffer) {
      ResourceLocation modelId = buffer.readResourceLocation();
      return new SelectDiscoveredModelMessage(modelId);
   }

   public static void handle(SelectDiscoveredModelMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer sender = context.getSender();
         if (sender != null) {
            if (sender.containerMenu instanceof TransmogTableContainer container) {
               container.selectModelId(message.modelId());
            }
         }
      });
      context.setPacketHandled(true);
   }
}
