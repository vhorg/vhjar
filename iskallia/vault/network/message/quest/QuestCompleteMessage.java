package iskallia.vault.network.message.quest;

import iskallia.vault.config.quest.QuestConfig;
import iskallia.vault.quest.QuestState;
import iskallia.vault.quest.base.Quest;
import iskallia.vault.world.data.QuestStatesData;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class QuestCompleteMessage {
   private final String questId;

   public QuestCompleteMessage(String questId) {
      this.questId = questId;
   }

   public String getQuestId() {
      return this.questId;
   }

   public static void encode(QuestCompleteMessage pkt, FriendlyByteBuf buffer) {
      String questId = pkt.getQuestId();
      buffer.writeUtf(questId);
   }

   public static QuestCompleteMessage decode(FriendlyByteBuf buffer) {
      String state = buffer.readUtf();
      return new QuestCompleteMessage(state);
   }

   public static void handle(QuestCompleteMessage pkt, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      ServerPlayer player = context.getSender();
      if (player != null) {
         QuestState state = QuestStatesData.get().getState(player);
         Optional<Quest> quest = state.<QuestConfig>getConfig(player.getLevel()).getQuestById(pkt.getQuestId());
         quest.ifPresent(state::setComplete);
         context.setPacketHandled(true);
      }
   }
}
