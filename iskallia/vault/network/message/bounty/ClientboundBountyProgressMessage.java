package iskallia.vault.network.message.bounty;

import iskallia.vault.bounty.Bounty;
import iskallia.vault.client.gui.screen.bounty.BountyProgressScreen;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
      context.enqueueWork(() -> openScreen(message.bounties));
      context.setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   private static void openScreen(List<Bounty> bounties) {
      Minecraft.getInstance().setScreen(new BountyProgressScreen(bounties));
   }
}
