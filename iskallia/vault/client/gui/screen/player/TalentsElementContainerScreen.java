package iskallia.vault.client.gui.screen.player;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.screen.player.legacy.SplitTabContent;
import iskallia.vault.client.gui.screen.player.legacy.TabContent;
import iskallia.vault.client.gui.screen.player.legacy.tab.split.dialog.TalentDialog;
import iskallia.vault.client.gui.screen.player.legacy.tab.split.pan.TalentPanRegion;
import iskallia.vault.container.NBTElementContainer;
import iskallia.vault.skill.tree.TalentTree;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;

public class TalentsElementContainerScreen extends SkillsElementContainerScreen<TalentTree> {
   public static final int TAB_INDEX = 2;

   public TalentsElementContainerScreen(NBTElementContainer<TalentTree> container, Inventory inventory, Component title) {
      super(container, inventory, title, ScreenRenderers.getImmediate());
   }

   @Override
   public int getTabIndex() {
      return 2;
   }

   @Override
   public MutableComponent getTabTitle() {
      return new TextComponent("Talents");
   }

   @Override
   public TabContent getTabContent() {
      TalentDialog talentDialog = new TalentDialog(this.getSkillTree(), this);
      TalentPanRegion talentPanningContent = new TalentPanRegion(talentDialog, this);
      return new SplitTabContent(this, talentDialog, talentPanningContent);
   }

   @Override
   protected void renderPointOverlay(PoseStack matrixStack) {
      this.renderSkillPointOverlay(matrixStack);
      this.renderRegretPointOverlay(matrixStack);
   }
}
