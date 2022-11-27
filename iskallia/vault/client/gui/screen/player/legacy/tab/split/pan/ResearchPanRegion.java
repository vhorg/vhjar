package iskallia.vault.client.gui.screen.player.legacy.tab.split.pan;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.screen.player.ResearchesElementContainerScreen;
import iskallia.vault.client.gui.screen.player.legacy.tab.split.dialog.ResearchDialog;
import iskallia.vault.client.gui.screen.player.legacy.tab.split.spi.AbstractPanRegion;
import iskallia.vault.client.gui.screen.player.legacy.widget.ResearchGroupWidget;
import iskallia.vault.client.gui.screen.player.legacy.widget.ResearchWidget;
import iskallia.vault.client.gui.screen.player.legacy.widget.connect.ConnectorWidget;
import iskallia.vault.config.entry.ResearchGroupStyle;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.util.MiscUtils;
import java.awt.Rectangle;
import java.awt.geom.Point2D.Float;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.TextComponent;

public class ResearchPanRegion extends AbstractPanRegion<ResearchesElementContainerScreen> {
   private final List<ResearchGroupWidget> researchGroups = new LinkedList<>();
   private final Map<String, ResearchWidget> researchWidgets = new HashMap<>();
   private final List<ConnectorWidget> researchConnectors = new LinkedList<>();
   private final ResearchDialog researchDialog;
   private ResearchWidget selectedWidget;

   public ResearchPanRegion(ResearchDialog researchDialog, ResearchesElementContainerScreen parentScreen) {
      super(parentScreen, new TextComponent("Researches Tab"));
      this.researchDialog = researchDialog;
   }

   @Override
   protected Collection<? extends AbstractWidget> getWidgets() {
      return this.researchWidgets.values();
   }

   @Override
   public void update() {
      this.researchGroups.clear();
      this.researchWidgets.clear();
      this.researchConnectors.clear();
      ResearchTree researchTree = this.parentScreen.getResearchTree();
      ModConfigs.RESEARCH_GROUPS.getGroups().forEach((groupId, group) -> {
         ResearchGroupStyle style = ModConfigs.RESEARCH_GROUP_STYLES.getStyle(groupId);
         if (style != null) {
            this.researchGroups.add(new ResearchGroupWidget(style, researchTree, () -> this.selectedWidget));
         }
      });
      ModConfigs.RESEARCHES_GUI
         .getStyles()
         .forEach((researchName, style) -> this.researchWidgets.put(researchName, new ResearchWidget(researchName, researchTree, style)));
      ModConfigs.RESEARCHES_GUI.getStyles().forEach((researchName, style) -> {
         ResearchWidget target = this.researchWidgets.get(researchName);
         if (target != null) {
            ModConfigs.SKILL_GATES.getGates().getDependencyResearches(researchName).forEach(dependentOn -> {
               ResearchWidget source = this.researchWidgets.get(dependentOn.getName());
               if (source != null) {
                  if (ModConfigs.SKILL_GATES.getGates().shouldDrawArrow(researchName, dependentOn.getName())) {
                     this.researchConnectors.add(new ConnectorWidget(source, target, ConnectorWidget.ConnectorType.ARROW));
                  }
               }
            });
            ModConfigs.SKILL_GATES.getGates().getLockedByResearches(researchName).forEach(dependentOn -> {
               ResearchWidget source = this.researchWidgets.get(dependentOn.getName());
               if (source != null) {
                  if (ModConfigs.SKILL_GATES.getGates().shouldDrawArrow(researchName, dependentOn.getName())) {
                     this.researchConnectors.add(new ConnectorWidget(source, target, ConnectorWidget.ConnectorType.DOUBLE_ARROW));
                  }
               }
            });
         }
      });
      this.loadViewportTransforms(false);
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (super.mouseClicked(mouseX, mouseY, button)) {
         return true;
      } else {
         Float midpoint = MiscUtils.getMidpoint(this.getBounds());
         int containerMouseX = (int)((mouseX - midpoint.x) / this.viewportScale - this.viewportTranslation.x);
         int containerMouseY = (int)((mouseY - midpoint.y) / this.viewportScale - this.viewportTranslation.y);
         boolean researchWidgetClicked = false;

         for (ResearchWidget researchWidget : this.researchWidgets.values()) {
            if (researchWidget.isMouseOver(containerMouseX, containerMouseY) && researchWidget.mouseClicked(containerMouseX, containerMouseY, button)) {
               if (this.selectedWidget != null) {
                  this.selectedWidget.deselect();
               }

               this.selectedWidget = researchWidget;
               this.selectedWidget.select();
               this.researchDialog.setResearchName(this.selectedWidget.getResearchName());
               researchWidgetClicked = true;
               break;
            }
         }

         return researchWidgetClicked;
      }
   }

   @Override
   public void renderTabForeground(PoseStack renderStack, Rectangle containerBounds, int mouseX, int mouseY, float pTicks, List<Runnable> postContainerRender) {
      RenderSystem.enableBlend();
      Float midpoint = MiscUtils.getMidpoint(this.getBounds());
      renderStack.pushPose();
      renderStack.translate(midpoint.x, midpoint.y, 0.0);
      renderStack.scale(this.viewportScale, this.viewportScale, 1.0F);
      renderStack.translate(this.viewportTranslation.x, this.viewportTranslation.y, 0.0);
      int containerMouseX = (int)((mouseX - midpoint.x) / this.viewportScale - this.viewportTranslation.x);
      int containerMouseY = (int)((mouseY - midpoint.y) / this.viewportScale - this.viewportTranslation.y);

      for (ResearchGroupWidget researchGroupWidget : this.researchGroups) {
         researchGroupWidget.render(renderStack, containerMouseX, containerMouseY, pTicks);
      }

      for (ConnectorWidget researchConnector : this.researchConnectors) {
         researchConnector.renderConnection(renderStack);
      }

      for (ResearchWidget researchWidget : this.researchWidgets.values()) {
         researchWidget.renderWidget(renderStack, containerBounds, mouseX, mouseY, containerMouseX, containerMouseY, pTicks, postContainerRender);
      }

      renderStack.popPose();
   }
}
