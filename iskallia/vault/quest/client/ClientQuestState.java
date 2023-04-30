package iskallia.vault.quest.client;

import iskallia.vault.config.quest.QuestConfig;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.quest.QuestState;

public class ClientQuestState {
   public static final ClientQuestState INSTANCE = new ClientQuestState();
   public static boolean debugMode = false;
   private QuestState state;

   public void updateState(QuestState state) {
      this.state = state;
   }

   public QuestState getState() {
      return this.state;
   }

   public <C extends QuestConfig> C getConfig() {
      return (C)(this.state.isSkyVaultWorld() ? ModConfigs.SKY_QUESTS : ModConfigs.QUESTS);
   }
}
