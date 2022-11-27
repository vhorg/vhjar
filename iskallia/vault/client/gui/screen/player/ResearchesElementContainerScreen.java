package iskallia.vault.client.gui.screen.player;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.screen.player.legacy.LegacySkillTreeElementContainerScreen;
import iskallia.vault.client.gui.screen.player.legacy.SplitTabContent;
import iskallia.vault.client.gui.screen.player.legacy.TabContent;
import iskallia.vault.client.gui.screen.player.legacy.tab.split.dialog.ResearchDialog;
import iskallia.vault.client.gui.screen.player.legacy.tab.split.pan.ResearchPanRegion;
import iskallia.vault.container.NBTElementContainer;
import iskallia.vault.research.ResearchTree;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;

public class ResearchesElementContainerScreen extends LegacySkillTreeElementContainerScreen<ResearchTree> {
   public static final int TAB_INDEX = 4;

   public ResearchesElementContainerScreen(NBTElementContainer<ResearchTree> container, Inventory inventory, Component title) {
      super(container, inventory, title, ScreenRenderers.getImmediate());
   }

   @Override
   public int getTabIndex() {
      return 4;
   }

   public ResearchTree getResearchTree() {
      return (ResearchTree)((NBTElementContainer)this.menu).getData();
   }

   @Override
   public MutableComponent getTabTitle() {
      return new TextComponent("Researches");
   }

   @Override
   public TabContent getTabContent() {
      ResearchDialog researchDialog = new ResearchDialog(this.getResearchTree(), this);
      ResearchPanRegion researchPanningContent = new ResearchPanRegion(researchDialog, this);
      return new SplitTabContent(this, researchDialog, researchPanningContent);
   }

   @Override
   protected void renderPointOverlay(PoseStack matrixStack) {
      this.renderKnowledgePointOverlay(matrixStack);
   }
}
