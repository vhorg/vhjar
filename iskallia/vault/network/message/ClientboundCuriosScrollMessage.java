package iskallia.vault.network.message;

import iskallia.vault.client.gui.framework.screen.layout.ScreenLayout;
import iskallia.vault.container.StatisticsTabContainer;
import iskallia.vault.init.ModNetwork;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.NetworkEvent.Context;

public class ClientboundCuriosScrollMessage {
   private final int targetContainerId;
   private final int targetIndex;

   private ClientboundCuriosScrollMessage(int targetContainerId, int targetIndex) {
      this.targetContainerId = targetContainerId;
      this.targetIndex = targetIndex;
   }

   public static void send(ServerPlayer serverPlayer, int containerId, int targetIndex) {
      ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new ClientboundCuriosScrollMessage(containerId, targetIndex));
   }

   public static void encode(ClientboundCuriosScrollMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.targetContainerId);
      buffer.writeInt(message.targetIndex);
   }

   public static ClientboundCuriosScrollMessage decode(FriendlyByteBuf buffer) {
      int targetWindowId = buffer.readInt();
      int targetIndex = buffer.readInt();
      return new ClientboundCuriosScrollMessage(targetWindowId, targetIndex);
   }

   public static void handle(ClientboundCuriosScrollMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         Minecraft minecraft = Minecraft.getInstance();
         LocalPlayer player = minecraft.player;
         if (player != null) {
            if (player.containerMenu instanceof StatisticsTabContainer menu && menu.containerId == message.targetContainerId) {
               menu.getCurioContainerHandler().scrollToIndex(message.targetIndex);
               ScreenLayout.requestLayout();
            }
         }
      });
      context.setPacketHandled(true);
   }
}
