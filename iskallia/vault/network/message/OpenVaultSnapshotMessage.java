package iskallia.vault.network.message;

import iskallia.vault.client.gui.screen.summary.VaultEndScreen;
import iskallia.vault.core.net.ArrayBitBuffer;
import iskallia.vault.core.vault.stat.VaultSnapshot;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public class OpenVaultSnapshotMessage {
   public static class S2C {
      private final VaultSnapshot snapshot;
      private final UUID uuid;

      public S2C(VaultSnapshot snapshot, UUID uuid) {
         this.snapshot = snapshot;
         this.uuid = uuid;
      }

      public static void encode(OpenVaultSnapshotMessage.S2C message, FriendlyByteBuf buffer) {
         ArrayBitBuffer buffer2 = ArrayBitBuffer.empty();
         message.snapshot.write(buffer2);
         buffer.writeLongArray(buffer2.toLongArray());
         buffer.writeUUID(message.uuid);
      }

      public static OpenVaultSnapshotMessage.S2C decode(FriendlyByteBuf buffer) {
         ArrayBitBuffer buffer2 = ArrayBitBuffer.backing(buffer.readLongArray(), 0);
         return new OpenVaultSnapshotMessage.S2C(new VaultSnapshot(buffer2), buffer.readUUID());
      }

      public static void handle(OpenVaultSnapshotMessage.S2C message, Supplier<Context> contextSupplier) {
         Context context = contextSupplier.get();
         context.enqueueWork(() -> openEndScreen(message));
         context.setPacketHandled(true);
      }

      @OnlyIn(Dist.CLIENT)
      private static void openEndScreen(OpenVaultSnapshotMessage.S2C message) {
         Minecraft.getInstance().setScreen(new VaultEndScreen(message.snapshot, new TextComponent("Vault Exit"), message.uuid, true, true));
      }
   }
}
