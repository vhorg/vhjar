package iskallia.vault.network.message.quest;

import iskallia.vault.world.data.QuestStatesData;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class QuestRequestSyncMessage {
   public static void encode(QuestRequestSyncMessage pkt, FriendlyByteBuf buffer) {
   }

   public static QuestRequestSyncMessage decode(FriendlyByteBuf buffer) {
      return new QuestRequestSyncMessage();
   }

   public static void handle(QuestRequestSyncMessage pkt, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      ServerPlayer player = context.getSender();
      if (player != null) {
         QuestStatesData.get().getState(player).syncAndPersist();
      }

      context.setPacketHandled(true);
   }
}
