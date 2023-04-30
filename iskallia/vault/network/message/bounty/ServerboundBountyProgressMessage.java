package iskallia.vault.network.message.bounty;

import iskallia.vault.bounty.Bounty;
import iskallia.vault.bounty.BountyList;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.world.data.BountyData;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

public record ServerboundBountyProgressMessage() {
   public static final ServerboundBountyProgressMessage INSTANCE = new ServerboundBountyProgressMessage();

   public static void encode(ServerboundBountyProgressMessage message, FriendlyByteBuf buffer) {
   }

   public static ServerboundBountyProgressMessage decode(FriendlyByteBuf buffer) {
      return INSTANCE;
   }

   public static void handle(ServerboundBountyProgressMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      ServerPlayer sender = context.getSender();
      if (sender != null) {
         List<Bounty> bounties = new ArrayList<>();
         BountyList activeList = BountyData.get().getAllActiveFor(sender.getUUID());
         if (!activeList.isEmpty()) {
            bounties.addAll(activeList);
         }

         BountyList legendaryList = BountyData.get().getAllLegendaryFor(sender.getUUID());
         if (!legendaryList.isEmpty()) {
            bounties.addAll(legendaryList);
         }

         if (!bounties.isEmpty()) {
            ModNetwork.CHANNEL.sendTo(new ClientboundBountyProgressMessage(bounties), sender.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
         } else {
            ModNetwork.CHANNEL.sendTo(new ClientboundBountyProgressMessage(null), sender.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
         }
      }

      context.setPacketHandled(true);
   }
}
