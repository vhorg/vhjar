package iskallia.vault.client.gui.screen.quest;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.element.NineSliceButtonElement;
import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.element.spi.ILayoutStrategy;
import iskallia.vault.client.gui.framework.render.ScreenTooltipRenderer;
import iskallia.vault.client.gui.framework.screen.AbstractElementScreen;
import iskallia.vault.client.gui.framework.screen.layout.ScreenLayout;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.screen.bounty.element.HeaderElement;
import iskallia.vault.config.quest.QuestConfig;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModTextureAtlases;
import iskallia.vault.network.message.quest.QuestCompleteMessage;
import iskallia.vault.network.message.quest.QuestRequestSyncMessage;
import iskallia.vault.quest.QuestState;
import iskallia.vault.quest.base.Quest;
import iskallia.vault.quest.client.ClientQuestState;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

public class QuestOverviewElementScreen extends AbstractElementScreen {
   public static boolean DEBUG;
   private QuestDisplayContainer questDisplay;
   private Quest selectedQuest;
   private QuestListElement questListElement;
   private HeaderElement headerElement;
   private NineSliceButtonElement<?> completeButton;

   public QuestOverviewElementScreen() {
      super(new TextComponent("Quest Overview"), ScreenRenderers.getBuffered(), ScreenTooltipRenderer::create);
      this.setGuiSize(Spatials.size(400, 200));
      DEBUG = false;
      if (DEBUG) {
         this.enableDebugRendering();
      }

      this.initialize();
   }

   public void refreshScreen() {
      this.initialize();
   }

   private void initialize() {
      if (this.questListElement != null) {
         this.removeElement(this.questListElement);
      }

      if (this.headerElement != null) {
         this.removeElement(this.headerElement);
      }

      if (this.completeButton != null) {
         this.removeElement(this.completeButton);
      }

      this.updateSelectedQuest();
      int center = this.getGuiSpatial().width() / 2;
      NineSliceElement<?> background = new NineSliceElement<NineSliceElement<NineSliceElement<?>>>(
            Spatials.positionXY(0, 0).size(this.getGuiSpatial().width(), this.getGuiSpatial().height()), ScreenTextures.DEFAULT_WINDOW_BACKGROUND
         )
         .layout(this.translateWorldSpatial())
         .enableSpatialDebugRender(false, DEBUG);
      NineSliceElement<?> separator = new NineSliceElement<NineSliceElement<NineSliceElement<?>>>(
            Spatials.positionXY(center - 1, 1).width(3).height(this.getGuiSpatial().height() - 2), ScreenTextures.INSET_VERTICAL_SEPARATOR
         )
         .layout(this.translateWorldSpatial())
         .enableSpatialDebugRender(false, DEBUG);
      int questListWidth = this.getGuiSpatial().width() / 2 - 7;
      int questListHeight = this.getGuiSpatial().height() - 28;
      LabelElement<?> title = new LabelElement(
            Spatials.positionXY(7, 9).size(questListWidth, 20), this.title.copy().withStyle(ChatFormatting.BLACK), LabelTextStyle.defaultStyle()
         )
         .layout(this.translateWorldSpatial());
      this.questListElement = new QuestListElement(Spatials.positionXY(4, 24).size(questListWidth, questListHeight), this)
         .layout(this.translateWorldSpatial())
         .enableSpatialDebugRender(false, DEBUG);
      this.updateQuestHeader();
      this.updateCompleteButton();
      this.addElement(background);
      this.addElement(separator);
      this.addElement(title);
      this.addElement(this.questListElement);
      this.updateQuestDisplay();
   }

   private void complete() {
      ModNetwork.CHANNEL.sendToServer(new QuestCompleteMessage(this.selectedQuest.getId()));
   }

   public void selectQuest(Quest quest) {
      this.selectedQuest = quest;
      this.updateQuestHeader();
      this.updateCompleteButton();
      this.updateQuestDisplay();
      ScreenLayout.requestLayout();
   }

   public Quest getSelectedQuest() {
      if (this.selectedQuest == null) {
         this.updateSelectedQuest();
      }

      return this.selectedQuest;
   }

   public Quest updateSelectedQuest() {
      QuestState state = ClientQuestState.INSTANCE.getState();
      if (state == null) {
         this.onClose();
         ModNetwork.CHANNEL.sendToServer(new QuestRequestSyncMessage());
         return this.selectedQuest;
      } else {
         String id = state.getInProgress()
            .stream()
            .findFirst()
            .orElse(state.getReadyToComplete().stream().findFirst().orElse(state.getCompleted().stream().findFirst().orElse("")));
         if (id.isEmpty()) {
            ModNetwork.CHANNEL.sendToServer(new QuestRequestSyncMessage());
         } else {
            ClientQuestState.INSTANCE.<QuestConfig>getConfig().<Quest>getQuestById(id).ifPresent(quest -> this.selectedQuest = quest);
         }

         return this.selectedQuest;
      }
   }

   private void updateQuestDisplay() {
      if (this.questDisplay != null) {
         this.removeElement(this.questDisplay);
         this.questDisplay = null;
      }

      ISpatial guiSpatial = this.getGuiSpatial();
      int questDisplayWidth = guiSpatial.width() / 2 - 6;
      int questDisplayHeight = guiSpatial.height() - 48;
      int questDisplayX = guiSpatial.width() / 2;
      this.questDisplay = new QuestDisplayContainer(Spatials.positionXY(questDisplayX + 2, 23).size(questDisplayWidth, questDisplayHeight), this)
         .layout(this.translateWorldSpatial());
      this.addElement(this.questDisplay);
   }

   private void updateCompleteButton() {
      if (this.completeButton != null) {
         this.removeElement(this.completeButton);
      }

      QuestState state = ClientQuestState.INSTANCE.getState();
      MutableComponent buttonText = new TextComponent("Complete").withStyle(ChatFormatting.WHITE);
      if (state.getCompleted().contains(this.selectedQuest.getId())) {
         buttonText = new TextComponent("Completed").withStyle(ChatFormatting.GRAY);
      } else if (state.getInProgress().contains(this.selectedQuest.getId())) {
         buttonText = new TextComponent("Incomplete").withStyle(ChatFormatting.GRAY);
      } else if (state.getReadyToComplete().contains(this.selectedQuest.getId())) {
         buttonText = new TextComponent("Complete").withStyle(ChatFormatting.WHITE);
      }

      MutableComponent component = buttonText;
      this.completeButton = new NineSliceButtonElement(
            Spatials.positionXYZ(this.getGuiSpatial().width() / 2 + 2, this.getGuiSpatial().height() - 24, 5)
               .width(this.getGuiSpatial().width() / 2 - 6)
               .height(20),
            ScreenTextures.BUTTON_EMPTY_TEXTURES,
            this::complete
         )
         .<NineSliceButtonElement<?>>label(() -> component, LabelTextStyle.center().shadow())
         .<NineSliceButtonElement<NineSliceButtonElement<?>>>setDisabled(
            () -> !ClientQuestState.INSTANCE.getState().getReadyToComplete().contains(this.selectedQuest.getId())
         )
         .layout(this.translateWorldSpatial());
      this.addElement(this.completeButton);
   }

   private void updateQuestHeader() {
      if (this.headerElement != null) {
         this.removeElement(this.headerElement);
      }

      this.headerElement = new HeaderElement(
            Spatials.positionXY(this.getGuiSpatial().width() / 2 + 2, 5).width(this.getGuiSpatial().width() / 2 - 6).height(20),
            new TextComponent(this.selectedQuest.getName()),
            TextureAtlasRegion.of(ModTextureAtlases.QUESTS, this.selectedQuest.getIcon()),
            false
         )
         .layout(this.translateWorldSpatial());
      this.addElement(this.headerElement);
   }

   @NotNull
   private ILayoutStrategy translateWorldSpatial() {
      return (screen, gui, parent, world) -> world.translateXY(this.getGuiSpatial());
   }

   public boolean isPauseScreen() {
      return false;
   }

   public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
      Key key = InputConstants.getKey(pKeyCode, pScanCode);
      if (pKeyCode != 256 && !Minecraft.getInstance().options.keyInventory.isActiveAndMatches(key)) {
         return super.keyPressed(pKeyCode, pScanCode, pModifiers);
      } else {
         this.onClose();
         return true;
      }
   }
}
