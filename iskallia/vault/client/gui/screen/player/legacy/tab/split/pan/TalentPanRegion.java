package iskallia.vault.client.gui.screen.player.legacy.tab.split.pan;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.screen.player.TalentsElementContainerScreen;
import iskallia.vault.client.gui.screen.player.legacy.tab.split.dialog.TalentDialog;
import iskallia.vault.client.gui.screen.player.legacy.tab.split.spi.AbstractPanRegion;
import iskallia.vault.client.gui.screen.player.legacy.widget.TalentWidget;
import iskallia.vault.client.gui.screen.player.legacy.widget.connect.ConnectorWidget;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.util.MiscUtils;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D.Float;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.TextComponent;

public class TalentPanRegion extends AbstractPanRegion<TalentsElementContainerScreen> {
   private final Map<String, TalentWidget> talentWidgets = new HashMap<>();
   private final List<ConnectorWidget> talentConnectors = new LinkedList<>();
   private final TalentDialog talentDialog;
   private TalentWidget selectedWidget;

   public TalentPanRegion(TalentDialog talentDialog, TalentsElementContainerScreen parentScreen) {
      super(parentScreen, new TextComponent("Talents Tab"));
      this.talentDialog = talentDialog;
   }

   @Override
   protected Collection<? extends AbstractWidget> getWidgets() {
      return this.talentWidgets.values();
   }

   @Override
   public void update() {
      this.talentWidgets.clear();
      TalentTree talentTree = this.parentScreen.getTalentTree();
      ModConfigs.TALENTS_GUI
         .getStyles()
         .forEach((talentName, style) -> this.talentWidgets.put(talentName, new TalentWidget(ModConfigs.TALENTS.getByName(talentName), talentTree, style)));
      ModConfigs.TALENTS_GUI.getStyles().forEach((talentName, style) -> {
         TalentWidget target = this.talentWidgets.get(talentName);
         if (target != null) {
            ModConfigs.SKILL_GATES.getGates().getDependencyTalents(talentName).forEach(dependentOn -> {
               TalentWidget source = this.talentWidgets.get(dependentOn.getParentName());
               if (source != null) {
                  if (ModConfigs.SKILL_GATES.getGates().shouldDrawArrow(talentName, dependentOn.getParentName())) {
                     this.talentConnectors.add(new ConnectorWidget(source, target, ConnectorWidget.ConnectorType.ARROW));
                  }
               }
            });
            ModConfigs.SKILL_GATES.getGates().getLockedByTalents(talentName).forEach(dependentOn -> {
               TalentWidget source = this.talentWidgets.get(dependentOn.getParentName());
               if (source != null) {
                  if (ModConfigs.SKILL_GATES.getGates().shouldDrawArrow(talentName, dependentOn.getParentName())) {
                     ConnectorWidget widget = new ConnectorWidget(source, target, ConnectorWidget.ConnectorType.DOUBLE_ARROW);
                     widget.setColor(new Color(11272192));
                     this.talentConnectors.add(widget);
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
         boolean talentWidgetClicked = false;

         for (TalentWidget abilityWidget : this.talentWidgets.values()) {
            if (abilityWidget.isMouseOver(containerMouseX, containerMouseY) && abilityWidget.mouseClicked(containerMouseX, containerMouseY, button)) {
               if (this.selectedWidget != null) {
                  this.selectedWidget.deselect();
               }

               this.selectedWidget = abilityWidget;
               this.selectedWidget.select();
               this.talentDialog.setTalentGroup(this.selectedWidget.getTalentGroup());
               talentWidgetClicked = true;
               break;
            }
         }

         return talentWidgetClicked;
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

      for (ConnectorWidget talentConnector : this.talentConnectors) {
         talentConnector.renderConnection(renderStack);
      }

      for (TalentWidget abilityWidget : this.talentWidgets.values()) {
         abilityWidget.renderWidget(renderStack, containerBounds, mouseX, mouseY, containerMouseX, containerMouseY, pTicks, postContainerRender);
      }

      renderStack.popPose();
   }
}
