package iskallia.vault.network.message;

import iskallia.vault.util.PlayerRageHelper;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class RageSyncMessage {
   private final int rage;

   public RageSyncMessage(int rage) {
      this.rage = rage;
   }

   public int getRage() {
      return this.rage;
   }

   public static void encode(RageSyncMessage message, PacketBuffer buffer) {
      buffer.writeInt(message.rage);
   }

   public static RageSyncMessage decode(PacketBuffer buffer) {
      return new RageSyncMessage(buffer.readInt());
   }

   public static void handle(RageSyncMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> PlayerRageHelper.receiveRageUpdate(message));
      context.setPacketHandled(true);
   }
}
