package iskallia.vault.network.message.bounty;

import iskallia.vault.container.BountyContainer;
import iskallia.vault.world.data.BountyData;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public record ServerboundAbandonBountyMessage(UUID bountyId) {
   public static void encode(ServerboundAbandonBountyMessage message, FriendlyByteBuf buffer) {
      buffer.writeUUID(message.bountyId);
   }

   public static ServerboundAbandonBountyMessage decode(FriendlyByteBuf buffer) {
      UUID bountyId = buffer.readUUID();
      return new ServerboundAbandonBountyMessage(bountyId);
   }

   public static void handle(ServerboundAbandonBountyMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer serverPlayer = context.getSender();
         if (serverPlayer != null) {
            BountyData.get().abandon(serverPlayer, message.bountyId);
            if (serverPlayer.containerMenu instanceof BountyContainer container) {
               container.replaceComplete(BountyData.get().getAllCompletedFor(serverPlayer.getUUID()));
               container.broadcastChanges();
            }
         }
      });
      context.setPacketHandled(true);
   }
}
