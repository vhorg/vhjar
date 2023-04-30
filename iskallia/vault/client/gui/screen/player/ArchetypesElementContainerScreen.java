package iskallia.vault.client.gui.screen.player;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.screen.player.legacy.LegacySkillTreeElementContainerScreen;
import iskallia.vault.client.gui.screen.player.legacy.SplitTabContent;
import iskallia.vault.client.gui.screen.player.legacy.TabContent;
import iskallia.vault.client.gui.screen.player.legacy.tab.split.dialog.ArchetypeDialog;
import iskallia.vault.client.gui.screen.player.legacy.tab.split.pan.ArchetypePanRegion;
import iskallia.vault.container.NBTElementContainer;
import iskallia.vault.skill.archetype.ArchetypeContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;

public class ArchetypesElementContainerScreen extends LegacySkillTreeElementContainerScreen<ArchetypeContainer> {
   public static final int TAB_INDEX = 4;

   public ArchetypesElementContainerScreen(NBTElementContainer<ArchetypeContainer> container, Inventory inventory, Component title) {
      super(container, inventory, title, ScreenRenderers.getImmediate());
   }

   public ArchetypeContainer getArchetypeContainer() {
      return (ArchetypeContainer)((NBTElementContainer)this.menu).getData();
   }

   @Override
   public int getTabIndex() {
      return 4;
   }

   @Override
   public MutableComponent getTabTitle() {
      return new TextComponent("Archetypes");
   }

   @Override
   public TabContent getTabContent() {
      ArchetypeDialog dialog = new ArchetypeDialog(this.getArchetypeContainer(), this);
      ArchetypePanRegion panRegion = new ArchetypePanRegion(dialog, this);
      return new SplitTabContent(this, dialog, panRegion);
   }

   @Override
   protected void renderPointOverlay(PoseStack matrixStack) {
      this.renderArchetypePointOverlay(matrixStack);
   }
}
