package iskallia.vault.network.message.bounty;

import iskallia.vault.bounty.Bounty;
import iskallia.vault.bounty.BountyList;
import iskallia.vault.bounty.client.ClientBountyData;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public record ClientboundBountyAvailableMessage(List<Bounty> bounties) {
   public static void encode(ClientboundBountyAvailableMessage message, FriendlyByteBuf buffer) {
      if (message.bounties != null && !message.bounties.isEmpty()) {
         buffer.writeCollection(message.bounties, (friendlyByteBuf, bounty) -> friendlyByteBuf.writeNbt(bounty.serializeNBT()));
      }
   }

   public static ClientboundBountyAvailableMessage decode(FriendlyByteBuf buffer) {
      try {
         List<Bounty> bountyList = (List<Bounty>)buffer.readCollection(ArrayList::new, friendlyByteBuf -> new Bounty(friendlyByteBuf.readNbt()));
         return new ClientboundBountyAvailableMessage(bountyList);
      } catch (Exception var2) {
         return new ClientboundBountyAvailableMessage(null);
      }
   }

   public static void handle(ClientboundBountyAvailableMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      BountyList bounties = new BountyList();
      List<Bounty> bountiesSent = message.bounties;
      if (bountiesSent != null && !bountiesSent.isEmpty()) {
         bounties.addAll(bountiesSent);
      }

      ClientBountyData.INSTANCE.updateAvailableBounties(bounties);
      context.setPacketHandled(true);
   }
}
