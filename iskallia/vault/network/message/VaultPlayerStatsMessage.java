package iskallia.vault.network.message;

import iskallia.vault.core.net.ArrayBitBuffer;
import iskallia.vault.core.vault.stat.VaultSnapshot;
import iskallia.vault.util.scheduler.EndScreenScheduler;
import iskallia.vault.world.data.VaultPlayerStats;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public class VaultPlayerStatsMessage {
   public static class C2S {
      private UUID vaultId;

      public C2S(UUID vaultId) {
         this.vaultId = vaultId;
      }

      public static void encode(VaultPlayerStatsMessage.C2S message, FriendlyByteBuf buffer) {
         buffer.writeUUID(message.vaultId);
      }

      public static VaultPlayerStatsMessage.C2S decode(FriendlyByteBuf buffer) {
         return new VaultPlayerStatsMessage.C2S(buffer.readUUID());
      }

      public static void handle(VaultPlayerStatsMessage.C2S message, Supplier<Context> contextSupplier) {
         Context context = contextSupplier.get();
         context.enqueueWork(() -> {
            if (context.getSender() != null) {
               VaultPlayerStats.consume(context.getSender(), message.vaultId);
            }
         });
         context.setPacketHandled(true);
      }
   }

   public static class S2C {
      private VaultSnapshot snapshot;

      public S2C(VaultSnapshot snapshot) {
         this.snapshot = snapshot;
      }

      public static void encode(VaultPlayerStatsMessage.S2C message, FriendlyByteBuf buffer) {
         ArrayBitBuffer buffer2 = ArrayBitBuffer.empty();
         message.snapshot.writeBits(buffer2);
         buffer.writeLongArray(buffer2.toLongArray());
      }

      public static VaultPlayerStatsMessage.S2C decode(FriendlyByteBuf buffer) {
         ArrayBitBuffer buffer2 = ArrayBitBuffer.backing(buffer.readLongArray(), 0);
         return new VaultPlayerStatsMessage.S2C(new VaultSnapshot(buffer2));
      }

      public static void handle(VaultPlayerStatsMessage.S2C message, Supplier<Context> contextSupplier) {
         Context context = contextSupplier.get();
         context.enqueueWork(() -> openEndScreen(message));
         context.setPacketHandled(true);
      }

      @OnlyIn(Dist.CLIENT)
      private static void openEndScreen(VaultPlayerStatsMessage.S2C message) {
         EndScreenScheduler.getInstance().snapshot = message.snapshot;
      }
   }
}
