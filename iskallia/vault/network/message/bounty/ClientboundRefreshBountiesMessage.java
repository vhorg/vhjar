package iskallia.vault.network.message.bounty;

import iskallia.vault.bounty.BountyList;
import iskallia.vault.client.gui.screen.bounty.BountyScreen;
import iskallia.vault.container.BountyContainer;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public record ClientboundRefreshBountiesMessage(BountyList active, BountyList available, BountyList complete, BountyList legendary) {
   public static void encode(ClientboundRefreshBountiesMessage message, FriendlyByteBuf buffer) {
      buffer.writeNbt(message.active.serializeNBT());
      buffer.writeNbt(message.available.serializeNBT());
      buffer.writeNbt(message.complete.serializeNBT());
      buffer.writeNbt(message.legendary.serializeNBT());
   }

   public static ClientboundRefreshBountiesMessage decode(FriendlyByteBuf buffer) {
      BountyList active = new BountyList(buffer.readNbt());
      BountyList available = new BountyList(buffer.readNbt());
      BountyList complete = new BountyList(buffer.readNbt());
      BountyList legendary = new BountyList(buffer.readNbt());
      return new ClientboundRefreshBountiesMessage(active, available, complete, legendary);
   }

   public static void handle(ClientboundRefreshBountiesMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      LocalPlayer player = Minecraft.getInstance().player;
      Screen screen = Minecraft.getInstance().screen;
      if (player != null && player.containerMenu instanceof BountyContainer container) {
         container.replaceActive(message.active);
         container.replaceAvailable(message.available);
         container.replaceComplete(message.complete);
         container.replaceLegendary(message.legendary);
         container.broadcastChanges();
         if (screen instanceof BountyScreen bountyScreen) {
            bountyScreen.getBountyTableElement().refreshBountySelection();
         }
      }

      context.setPacketHandled(true);
   }
}
