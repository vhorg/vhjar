package iskallia.vault.network.message;

import iskallia.vault.client.gui.overlay.CheerOverlay;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class CheerReceiveMessage {
   private final String nickname;
   private final boolean megahead;

   public CheerReceiveMessage(String nickname, boolean megahead) {
      this.nickname = nickname;
      this.megahead = megahead;
   }

   public static void encode(CheerReceiveMessage message, FriendlyByteBuf buffer) {
      buffer.writeUtf(message.nickname);
      buffer.writeBoolean(message.megahead);
   }

   public static CheerReceiveMessage decode(FriendlyByteBuf buffer) {
      String nickname = buffer.readUtf(32767);
      boolean megahead = buffer.readBoolean();
      return new CheerReceiveMessage(nickname, megahead);
   }

   public static void handle(CheerReceiveMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> CheerOverlay.receiveCheer(message.nickname, message.megahead));
      context.setPacketHandled(true);
   }
}
