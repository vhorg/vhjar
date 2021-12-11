package iskallia.vault.network.message;

import iskallia.vault.container.VaultCharmControllerContainer;
import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class VaultCharmControllerScrollMessage {
   public float scroll;

   public VaultCharmControllerScrollMessage(float scroll) {
      this.scroll = scroll;
   }

   public static void encode(VaultCharmControllerScrollMessage message, PacketBuffer buffer) {
      buffer.writeFloat(message.scroll);
   }

   public static VaultCharmControllerScrollMessage decode(PacketBuffer buffer) {
      return new VaultCharmControllerScrollMessage(buffer.readFloat());
   }

   public static void handle(VaultCharmControllerScrollMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayerEntity sender = context.getSender();
         if (sender != null) {
            if (sender.field_71070_bA instanceof VaultCharmControllerContainer) {
               ((VaultCharmControllerContainer)sender.field_71070_bA).scrollTo(message.scroll);
            }
         }
      });
      context.setPacketHandled(true);
   }
}
