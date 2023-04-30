package iskallia.vault.network.message.quest;

import iskallia.vault.config.quest.QuestConfig;
import iskallia.vault.quest.QuestState;
import iskallia.vault.world.data.QuestStatesData;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class QuestProgressMessage {
   private final String questId;

   public QuestProgressMessage(String questId) {
      this.questId = questId;
   }

   public String getQuestId() {
      return this.questId;
   }

   public static void encode(QuestProgressMessage pkt, FriendlyByteBuf buffer) {
      String questId = pkt.getQuestId();
      buffer.writeUtf(questId);
   }

   public static QuestProgressMessage decode(FriendlyByteBuf buffer) {
      String state = buffer.readUtf();
      return new QuestProgressMessage(state);
   }

   public static void handle(QuestProgressMessage pkt, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      ServerPlayer player = context.getSender();
      if (player != null) {
         QuestState state = QuestStatesData.get().getState(player);
         state.addProgress(state.<QuestConfig>getConfig(player.getLevel()).getQuestById(pkt.getQuestId()), 1.0F);
         context.setPacketHandled(true);
      }
   }
}