package iskallia.vault.client.gui.screen.player;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.gui.screen.player.legacy.SplitTabContent;
import iskallia.vault.client.gui.screen.player.legacy.TabContent;
import iskallia.vault.client.gui.screen.player.legacy.tab.split.dialog.ExpertiseDialog;
import iskallia.vault.client.gui.screen.player.legacy.tab.split.pan.ExpertisePanRegion;
import iskallia.vault.container.NBTElementContainer;
import iskallia.vault.skill.tree.ExpertiseTree;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;

public class ExpertisesElementContainerScreen extends SkillsElementContainerScreen<ExpertiseTree> {
   public static final int TAB_INDEX = 3;

   public ExpertisesElementContainerScreen(NBTElementContainer<ExpertiseTree> container, Inventory inventory, Component title) {
      super(container, inventory, title, ScreenRenderers.getImmediate());
   }

   @Override
   public int getTabIndex() {
      return 3;
   }

   @Override
   public MutableComponent getTabTitle() {
      return new TextComponent("Expertise");
   }

   @Override
   public TabContent getTabContent() {
      ExpertiseDialog expertiseDialog = new ExpertiseDialog(this.getSkillTree(), this);
      ExpertisePanRegion talentPanningContent = new ExpertisePanRegion(expertiseDialog, this);
      return new SplitTabContent(this, expertiseDialog, talentPanningContent);
   }

   @Override
   protected void renderPointOverlay(PoseStack matrixStack) {
      this.renderPointOverlay(
         matrixStack,
         VaultBarOverlay.unspentExpertisePoints,
         TextColor.fromRgb(16724414),
         " unspent expertise point" + (VaultBarOverlay.unspentSkillPoints == 1 ? "" : "s")
      );
   }
}
