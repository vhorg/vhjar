package iskallia.vault.client.gui.screen.quest;

import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.VerticalScrollClipContainer;
import iskallia.vault.client.gui.framework.render.Tooltips;
import iskallia.vault.client.gui.framework.spatial.Padding;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.config.quest.QuestConfig;
import iskallia.vault.quest.QuestState;
import iskallia.vault.quest.base.Quest;
import iskallia.vault.quest.client.ClientQuestState;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

public class QuestListElement extends VerticalScrollClipContainer<QuestListElement> {
   private final QuestOverviewElementScreen parent;

   public QuestListElement(ISpatial spatial, QuestOverviewElementScreen parent) {
      super(spatial, Padding.ZERO, ScreenTextures.INSET_BLACK_BACKGROUND);
      this.parent = parent;
      this.initializeQuests();
   }

   private void initializeQuests() {
      int buttonWidth = this.innerWidth();
      int buttonHeight = 20;
      int x = 0;
      int y = 0;
      QuestState state = ClientQuestState.INSTANCE.getState();
      List<Quest> sorted = this.splitCompleted(state);
      boolean debug = ClientQuestState.debugMode;

      for (Quest quest : sorted) {
         QuestButtonElement button = (QuestButtonElement)new QuestButtonElement(
               Spatials.positionXY(x, y).size(buttonWidth, buttonHeight), quest, this.selectQuest(quest)
            )
            .setDisabled(
               () -> debug
                  ? false
                  : !state.getCompleted().contains(quest.getId())
                     && !state.getInProgress().contains(quest.getId())
                     && !state.getReadyToComplete().contains(quest.getId())
            )
            .tooltip(
               Tooltips.shift(
                  Tooltips.empty(),
                  Tooltips.multi(
                     () -> Minecraft.getInstance()
                        .font
                        .getSplitter()
                        .splitLines(quest.getTypeDescription().getContents(), 140, Style.EMPTY)
                        .stream()
                        .map(formattedText -> new TextComponent(formattedText.getString()))
                        .toList()
                  )
               )
            );
         this.addElement(button).enableSpatialDebugRender(false, QuestOverviewElementScreen.DEBUG);
         y += buttonHeight;
      }
   }

   @NotNull
   private List<Quest> splitCompleted(QuestState state) {
      List<Quest> quests = new ArrayList<>(ClientQuestState.INSTANCE.<QuestConfig>getConfig().getQuests());
      List<Quest> sorted = new ArrayList<>();
      List<Quest> incomplete = new ArrayList<>();
      List<Quest> complete = new ArrayList<>();

      for (Quest q : quests) {
         if (!state.getCompleted().contains(q.getId())) {
            incomplete.add(q);
         } else {
            complete.add(q);
         }
      }

      sorted.addAll(incomplete);
      sorted.addAll(complete);
      return sorted;
   }

   @NotNull
   private Runnable selectQuest(Quest quest) {
      return () -> this.parent.selectQuest(quest);
   }
}
