package iskallia.vault.network.message.bounty;

import iskallia.vault.bounty.BountyList;
import iskallia.vault.container.BountyContainer;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.world.data.BountyData;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

public record ServerboundClaimRewardMessage(UUID bountyId) {
   public static void encode(ServerboundClaimRewardMessage message, FriendlyByteBuf buffer) {
      buffer.writeUUID(message.bountyId);
   }

   public static ServerboundClaimRewardMessage decode(FriendlyByteBuf buffer) {
      UUID bountyId = buffer.readUUID();
      return new ServerboundClaimRewardMessage(bountyId);
   }

   public static void handle(ServerboundClaimRewardMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(
         () -> {
            ServerPlayer serverPlayer = context.getSender();
            if (serverPlayer != null) {
               UUID playerId = serverPlayer.getUUID();
               BountyData.get().complete(serverPlayer, message.bountyId);
               if (serverPlayer.containerMenu instanceof BountyContainer container) {
                  container.replaceActive(new BountyList());
                  ModNetwork.CHANNEL
                     .sendTo(
                        new ClientboundRefreshBountiesMessage(
                           BountyData.get().getAllActiveFor(playerId),
                           BountyData.get().getAllAvailableFor(playerId),
                           BountyData.get().getAllCompletedFor(playerId)
                        ),
                        serverPlayer.connection.connection,
                        NetworkDirection.PLAY_TO_CLIENT
                     );
               }
            }
         }
      );
      context.setPacketHandled(true);
   }
}
