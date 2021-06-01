package iskallia.vault.network.message;

import iskallia.vault.client.gui.overlay.VaultRaidOverlay;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class VaultRaidTickMessage {
   public int remainingTicks;

   public VaultRaidTickMessage() {
   }

   public VaultRaidTickMessage(int remainingTicks) {
      this.remainingTicks = remainingTicks;
   }

   public static void encode(VaultRaidTickMessage message, PacketBuffer buffer) {
      buffer.writeInt(message.remainingTicks);
   }

   public static VaultRaidTickMessage decode(PacketBuffer buffer) {
      VaultRaidTickMessage message = new VaultRaidTickMessage();
      message.remainingTicks = buffer.readInt();
      return message;
   }

   public static void handle(VaultRaidTickMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> VaultRaidOverlay.remainingTicks = message.remainingTicks);
      context.setPacketHandled(true);
   }
}
