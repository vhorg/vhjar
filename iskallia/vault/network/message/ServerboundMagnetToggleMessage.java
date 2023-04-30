package iskallia.vault.network.message;

import iskallia.vault.item.MagnetItem;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class ServerboundMagnetToggleMessage {
   public static final ServerboundMagnetToggleMessage INSTANCE = new ServerboundMagnetToggleMessage();

   public static void encode(ServerboundMagnetToggleMessage pkt, FriendlyByteBuf buffer) {
   }

   public static ServerboundMagnetToggleMessage decode(FriendlyByteBuf buffer) {
      return new ServerboundMagnetToggleMessage();
   }

   public static void handle(ServerboundMagnetToggleMessage pkt, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer sender = context.getSender();
         if (sender != null) {
            MagnetItem.toggleMagnet(sender);
         }
      });
      context.setPacketHandled(true);
   }
}
