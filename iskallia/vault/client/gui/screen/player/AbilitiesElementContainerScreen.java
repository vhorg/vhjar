package iskallia.vault.client.gui.screen.player;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.screen.player.legacy.LegacySkillTreeElementContainerScreen;
import iskallia.vault.client.gui.screen.player.legacy.SplitTabContent;
import iskallia.vault.client.gui.screen.player.legacy.TabContent;
import iskallia.vault.client.gui.screen.player.legacy.tab.split.dialog.AbilityDialog;
import iskallia.vault.client.gui.screen.player.legacy.tab.split.pan.AbilityPanRegion;
import iskallia.vault.container.NBTElementContainer;
import iskallia.vault.skill.tree.AbilityTree;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;

public class AbilitiesElementContainerScreen extends LegacySkillTreeElementContainerScreen<AbilityTree> {
   public static final int TAB_INDEX = 1;

   public AbilitiesElementContainerScreen(NBTElementContainer<AbilityTree> container, Inventory inventory, Component title) {
      super(container, inventory, title, ScreenRenderers.getImmediate());
   }

   public AbilityTree getAbilityTree() {
      return (AbilityTree)((NBTElementContainer)this.menu).getData();
   }

   @Override
   public int getTabIndex() {
      return 1;
   }

   @Override
   public MutableComponent getTabTitle() {
      return new TextComponent("Abilities");
   }

   @Override
   public TabContent getTabContent() {
      AbilityDialog abilityDialog = new AbilityDialog(this.getAbilityTree(), this);
      AbilityPanRegion abilityPanningContent = new AbilityPanRegion(abilityDialog, this);
      return new SplitTabContent(this, abilityDialog, abilityPanningContent);
   }

   @Override
   protected void renderPointOverlay(PoseStack matrixStack) {
      this.renderSkillPointOverlay(matrixStack);
      this.renderRegretPointOverlay(matrixStack);
   }
}
