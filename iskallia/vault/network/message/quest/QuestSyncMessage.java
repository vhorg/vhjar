package iskallia.vault.network.message.quest;

import iskallia.vault.client.gui.framework.screen.layout.ScreenLayout;
import iskallia.vault.client.gui.screen.quest.QuestOverviewElementScreen;
import iskallia.vault.quest.QuestState;
import iskallia.vault.quest.client.ClientQuestState;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public class QuestSyncMessage {
   private final QuestState state;

   public QuestSyncMessage(QuestState state) {
      this.state = state;
   }

   public QuestState getState() {
      return this.state;
   }

   public static void encode(QuestSyncMessage pkt, FriendlyByteBuf buffer) {
      QuestState state = pkt.getState();
      buffer.writeUUID(state.getPlayerId());
      buffer.writeNbt(state.serializeNBT());
   }

   public static QuestSyncMessage decode(FriendlyByteBuf buffer) {
      UUID playerId = buffer.readUUID();
      CompoundTag stateData = buffer.readNbt();
      QuestState state = new QuestState(playerId);
      state.deserializeNBT(stateData);
      return new QuestSyncMessage(state);
   }

   public static void handle(QuestSyncMessage pkt, Supplier<Context> contextSupplier) {
      ClientQuestState.INSTANCE.updateState(pkt.getState());
      refreshScreen();
      contextSupplier.get().setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   private static void refreshScreen() {
      if (Minecraft.getInstance().screen instanceof QuestOverviewElementScreen screen) {
         screen.refreshScreen();
         ScreenLayout.requestLayout();
      }
   }
}
