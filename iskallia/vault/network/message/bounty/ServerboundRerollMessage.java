package iskallia.vault.network.message.bounty;

import iskallia.vault.bounty.Bounty;
import iskallia.vault.container.BountyContainer;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.world.data.BountyData;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

public record ServerboundRerollMessage(UUID bountyId) {
   public static void encode(ServerboundRerollMessage message, FriendlyByteBuf buffer) {
      buffer.writeUUID(message.bountyId);
   }

   public static ServerboundRerollMessage decode(FriendlyByteBuf buffer) {
      UUID bountyId = buffer.readUUID();
      return new ServerboundRerollMessage(bountyId);
   }

   public static void handle(ServerboundRerollMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(
         () -> {
            ServerPlayer serverPlayer = context.getSender();
            if (serverPlayer != null) {
               UUID playerId = serverPlayer.getUUID();
               if (serverPlayer.containerMenu instanceof BountyContainer container) {
                  Optional<Bounty> bounty = BountyData.get().getAllBountiesFor(serverPlayer.getUUID()).findById(message.bountyId);
                  if (bounty.isPresent()) {
                     ItemStack pearl = container.getBountyPearlSlot().getItem();
                     int cost = ModConfigs.BOUNTY_CONFIG.getCost(container.getVaultLevel());
                     pearl.shrink(cost);
                     container.getBountyPearlSlot().set(pearl);
                     BountyData.get().reroll(serverPlayer, message.bountyId);
                     ModNetwork.CHANNEL
                        .sendTo(
                           new ClientboundRefreshBountiesMessage(
                              BountyData.get().getAllActiveFor(playerId),
                              BountyData.get().getAllAvailableFor(playerId),
                              BountyData.get().getAllCompletedFor(playerId),
                              BountyData.get().getAllLegendaryFor(playerId)
                           ),
                           serverPlayer.connection.connection,
                           NetworkDirection.PLAY_TO_CLIENT
                        );
                  }
               }
            }
         }
      );
      context.setPacketHandled(true);
   }
}
