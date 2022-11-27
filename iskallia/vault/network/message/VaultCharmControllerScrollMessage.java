package iskallia.vault.network.message;

import iskallia.vault.container.VaultCharmControllerContainer;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class VaultCharmControllerScrollMessage {
   public float scroll;

   public VaultCharmControllerScrollMessage(float scroll) {
      this.scroll = scroll;
   }

   public static void encode(VaultCharmControllerScrollMessage message, FriendlyByteBuf buffer) {
      buffer.writeFloat(message.scroll);
   }

   public static VaultCharmControllerScrollMessage decode(FriendlyByteBuf buffer) {
      return new VaultCharmControllerScrollMessage(buffer.readFloat());
   }

   public static void handle(VaultCharmControllerScrollMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer sender = context.getSender();
         if (sender != null) {
            if (sender.containerMenu instanceof VaultCharmControllerContainer) {
               ((VaultCharmControllerContainer)sender.containerMenu).scrollTo(message.scroll);
            }
         }
      });
      context.setPacketHandled(true);
   }
}
