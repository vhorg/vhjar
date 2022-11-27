package iskallia.vault.network.message;

import iskallia.vault.client.ClientSandEventData;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent.Context;

public class SandEventContributorMessage {
   private final Component contributor;

   public SandEventContributorMessage(Component contributor) {
      this.contributor = contributor;
   }

   public static void encode(SandEventContributorMessage message, FriendlyByteBuf buffer) {
      buffer.writeComponent(message.contributor);
   }

   public static SandEventContributorMessage decode(FriendlyByteBuf buffer) {
      return new SandEventContributorMessage(buffer.readComponent());
   }

   public static void handle(SandEventContributorMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> ClientSandEventData.getInstance().addContributor(message.contributor));
      context.setPacketHandled(true);
   }
}
