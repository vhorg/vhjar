package iskallia.vault.network.message;

import iskallia.vault.container.StatisticsTabContainer;
import iskallia.vault.init.ModNetwork;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class ServerboundCuriosScrollMessage {
   private final int targetContainerId;
   private final int targetIndex;

   private ServerboundCuriosScrollMessage(int targetContainerId, int targetIndex) {
      this.targetContainerId = targetContainerId;
      this.targetIndex = targetIndex;
   }

   public static void send(int targetContainerId, int targetIndex) {
      ModNetwork.CHANNEL.sendToServer(new ServerboundCuriosScrollMessage(targetContainerId, targetIndex));
   }

   public static void encode(ServerboundCuriosScrollMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.targetContainerId);
      buffer.writeInt(message.targetIndex);
   }

   public static ServerboundCuriosScrollMessage decode(FriendlyByteBuf buffer) {
      int targetContainerId = buffer.readInt();
      int targetIndex = buffer.readInt();
      return new ServerboundCuriosScrollMessage(targetContainerId, targetIndex);
   }

   public static void handle(ServerboundCuriosScrollMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         ServerPlayer serverPlayer = context.getSender();
         if (serverPlayer != null) {
            if (serverPlayer.containerMenu instanceof StatisticsTabContainer menu && menu.containerId == message.targetContainerId) {
               menu.getCurioContainerHandler().scrollToIndex(message.targetIndex);
            }
         }
      });
      context.setPacketHandled(true);
   }
}
