package iskallia.vault.network.message;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.sync.SyncMode;
import iskallia.vault.core.data.sync.context.ClientSyncContext;
import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.event.ClientEvents;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.net.ArrayBitBuffer;
import iskallia.vault.core.vault.ClientVaults;
import iskallia.vault.core.vault.Vault;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class VaultMessage {
   public static class Sync {
      private final Version version;
      private final SyncMode mode;
      private final long[] payload;

      public Sync(ServerPlayer player, Vault vault, SyncMode mode) {
         this.version = vault.get(Vault.VERSION);
         this.mode = mode;
         SyncContext context = new ClientSyncContext(this.version, player.getUUID());
         ArrayBitBuffer buffer = ArrayBitBuffer.empty();
         switch (mode) {
            case FULL:
               vault.write(buffer, context);
               break;
            case DIFF:
               vault.writeDiff(buffer, context);
               break;
            case DIFF_TREE:
               vault.writeDiffTree(buffer, context);
         }

         this.payload = buffer.toLongArray();
      }

      public Sync(FriendlyByteBuf buf) {
         this.version = (Version)buf.readEnum(Version.class);
         this.mode = (SyncMode)buf.readEnum(SyncMode.class);
         this.payload = buf.readLongArray();
      }

      public Vault getUpdatedVault(Vault current) {
         ArrayBitBuffer buffer = ArrayBitBuffer.backing(this.payload, 0);
         ClientEvents.release(current);
         CommonEvents.release(current);

         current = switch (this.mode) {
            case FULL -> (Vault)current.read(buffer, new SyncContext(this.version));
            case DIFF -> (Vault)current.readDiff(buffer, new SyncContext(this.version));
            case DIFF_TREE -> (Vault)current.readDiffTree(buffer, new SyncContext(this.version));
         };
         current.initClient();
         return current;
      }

      public static void encode(VaultMessage.Sync message, FriendlyByteBuf buffer) {
         buffer.writeEnum(message.version);
         buffer.writeEnum(message.mode);
         buffer.writeLongArray(message.payload);
      }

      public static VaultMessage.Sync decode(FriendlyByteBuf buffer) {
         return new VaultMessage.Sync(buffer);
      }

      public static void handle(VaultMessage.Sync message, Supplier<Context> contextSupplier) {
         Context context = contextSupplier.get();
         context.enqueueWork(() -> ClientVaults.ACTIVE = message.getUpdatedVault(ClientVaults.ACTIVE));
         context.setPacketHandled(true);
      }
   }

   public static class Unload {
      private final UUID id;

      public Unload(Vault vault) {
         this.id = vault.get(Vault.ID);
      }

      public Unload(FriendlyByteBuf buf) {
         this.id = buf.readUUID();
      }

      public static void encode(VaultMessage.Unload message, FriendlyByteBuf buffer) {
         buffer.writeUUID(message.id);
      }

      public static VaultMessage.Unload decode(FriendlyByteBuf buffer) {
         return new VaultMessage.Unload(buffer);
      }

      public static void handle(VaultMessage.Unload message, Supplier<Context> contextSupplier) {
         Context context = contextSupplier.get();
         context.enqueueWork(() -> {
            if (message.id.equals(ClientVaults.ACTIVE.get(Vault.ID))) {
               ClientVaults.ACTIVE.releaseClient();
               ClientVaults.ACTIVE = new Vault();
            }
         });
         context.setPacketHandled(true);
      }
   }
}
