package iskallia.vault.network.message.quest;

import iskallia.vault.quest.client.ClientQuestState;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public record QuestDebugModeMessage(boolean enable) {
   public static void encode(QuestDebugModeMessage pkt, FriendlyByteBuf buffer) {
      buffer.writeBoolean(pkt.enable());
   }

   public static QuestDebugModeMessage decode(FriendlyByteBuf buffer) {
      return new QuestDebugModeMessage(buffer.readBoolean());
   }

   public static void handle(QuestDebugModeMessage pkt, Supplier<Context> contextSupplier) {
      ClientQuestState.debugMode = pkt.enable;
      contextSupplier.get().setPacketHandled(true);
   }
}
