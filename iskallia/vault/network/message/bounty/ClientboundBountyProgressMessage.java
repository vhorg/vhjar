package iskallia.vault.network.message.bounty;

import iskallia.vault.bounty.Bounty;
import iskallia.vault.bounty.BountyList;
import iskallia.vault.bounty.client.ClientBountyData;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public record ClientboundBountyProgressMessage(List<Bounty> bounties) {
   public static void encode(ClientboundBountyProgressMessage message, FriendlyByteBuf buffer) {
      if (message.bounties != null && !message.bounties.isEmpty()) {
         buffer.writeCollection(message.bounties, (friendlyByteBuf, bounty) -> friendlyByteBuf.writeNbt(bounty.serializeNBT()));
      }
   }

   public static ClientboundBountyProgressMessage decode(FriendlyByteBuf buffer) {
      try {
         List<Bounty> bountyList = (List<Bounty>)buffer.readCollection(ArrayList::new, friendlyByteBuf -> new Bounty(friendlyByteBuf.readNbt()));
         return new ClientboundBountyProgressMessage(bountyList);
      } catch (Exception var2) {
         return new ClientboundBountyProgressMessage(null);
      }
   }

   public static void handle(ClientboundBountyProgressMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      BountyList bounties = new BountyList();
      bounties.addAll(message.bounties);
      ClientBountyData.INSTANCE.updateBounties(bounties);
      context.setPacketHandled(true);
   }
}
