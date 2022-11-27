package iskallia.vault.network.message.bounty;

import iskallia.vault.client.gui.component.toast.BountyToast;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent.Context;

public class ClientboundBountyCompleteMessage {
   private final ResourceLocation resourceLocation;

   public ClientboundBountyCompleteMessage(ResourceLocation resourceLocation) {
      this.resourceLocation = resourceLocation;
   }

   public static void encode(ClientboundBountyCompleteMessage message, FriendlyByteBuf buffer) {
      buffer.writeResourceLocation(message.resourceLocation);
   }

   public static ClientboundBountyCompleteMessage decode(FriendlyByteBuf buffer) {
      return new ClientboundBountyCompleteMessage(buffer.readResourceLocation());
   }

   public static void handle(ClientboundBountyCompleteMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(
         () -> BountyToast.add(new TextComponent("Bounty Complete!"), new TextComponent("Claim your reward at the Bounty Table!"), message.resourceLocation)
      );
      context.setPacketHandled(true);
   }
}
