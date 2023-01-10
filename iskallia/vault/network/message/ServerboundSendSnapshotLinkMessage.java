package iskallia.vault.network.message;

import iskallia.vault.command.OpenVaultSnapshotCommand;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class ServerboundSendSnapshotLinkMessage {
   private final UUID uuid;

   public ServerboundSendSnapshotLinkMessage(UUID uuid) {
      this.uuid = uuid;
   }

   public static void encode(ServerboundSendSnapshotLinkMessage message, FriendlyByteBuf buffer) {
      buffer.writeUUID(message.uuid);
   }

   public static ServerboundSendSnapshotLinkMessage decode(FriendlyByteBuf buffer) {
      return new ServerboundSendSnapshotLinkMessage(buffer.readUUID());
   }

   public static void handle(ServerboundSendSnapshotLinkMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer sender = context.getSender();
         if (sender != null) {
            OpenVaultSnapshotCommand.sendCommand(sender, message.uuid, sender.server);
         }
      });
      context.setPacketHandled(true);
   }
}
