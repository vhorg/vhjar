package iskallia.vault.network.message.bounty;

import iskallia.vault.bounty.Bounty;
import iskallia.vault.bounty.BountyList;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.world.data.BountyData;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

public record ServerboundBountyProgressMessage() {
   public static void encode(ServerboundBountyProgressMessage message, FriendlyByteBuf buffer) {
   }

   public static ServerboundBountyProgressMessage decode(FriendlyByteBuf buffer) {
      return new ServerboundBountyProgressMessage();
   }

   public static void handle(ServerboundBountyProgressMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      ServerPlayer sender = context.getSender();
      if (sender != null) {
         BountyList bountyList = BountyData.get().getAllActiveFor(sender.getUUID());
         if (!bountyList.isEmpty()) {
            Bounty active = bountyList.get(0);
            ModNetwork.CHANNEL.sendTo(new ClientboundBountyProgressMessage(active), sender.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
         } else {
            ModNetwork.CHANNEL.sendTo(new ClientboundBountyProgressMessage(null), sender.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
         }
      }

      context.setPacketHandled(true);
   }
}
