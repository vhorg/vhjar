package iskallia.vault.client.gui.screen.player.legacy.tab.split.pan;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.screen.player.SkillsElementContainerScreen;
import iskallia.vault.client.gui.screen.player.legacy.tab.split.dialog.SkillDialog;
import iskallia.vault.client.gui.screen.player.legacy.tab.split.spi.AbstractPanRegion;
import iskallia.vault.client.gui.screen.player.legacy.widget.SkillWidget;
import iskallia.vault.client.gui.screen.player.legacy.widget.TalentGroupWidget;
import iskallia.vault.client.gui.screen.player.legacy.widget.connect.ConnectorWidget;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.tree.SkillTree;
import iskallia.vault.util.MiscUtils;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D.Float;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public abstract class SkillPanRegion<T extends SkillTree, S extends SkillsElementContainerScreen<T>, W extends SkillWidget<T>> extends AbstractPanRegion<S> {
   protected final Map<String, W> skillWidgets = new HashMap<>();
   protected final List<TalentGroupWidget> groupWidgets = new ArrayList<>();
   protected final List<ConnectorWidget> skillConnectors = new LinkedList<>();
   protected final SkillDialog<T, S> skillDialog;
   protected W selectedWidget;

   protected SkillPanRegion(S parentScreen, Component title, SkillDialog<T, S> skillDialog) {
      super(parentScreen, title);
      this.skillDialog = skillDialog;
   }

   @Override
   public void update() {
      this.skillWidgets.clear();
      this.groupWidgets.clear();
      T skillTree = this.parentScreen.getSkillTree();
      this.getStyles().forEach((skillName, style) -> this.initSkillWidget(skillTree, skillName, style, this.skillWidgets, this.groupWidgets));
      this.getStyles().forEach((skillName, style) -> {
         W target = this.skillWidgets.get(skillName);
         if (target != null) {
            ModConfigs.SKILL_GATES.getGates().getDependencySkills(skillName).forEach(dependentOn -> {
               W source = this.skillWidgets.get(dependentOn);
               if (source != null) {
                  if (ModConfigs.SKILL_GATES.getGates().shouldDrawArrow(skillName, dependentOn)) {
                     this.skillConnectors.add(new ConnectorWidget(source, target, ConnectorWidget.ConnectorType.ARROW));
                  }
               }
            });
            ModConfigs.SKILL_GATES.getGates().getLockedBySkills(skillName).forEach(dependentOn -> {
               W source = this.skillWidgets.get(dependentOn);
               if (source != null) {
                  if (ModConfigs.SKILL_GATES.getGates().shouldDrawArrow(skillName, dependentOn)) {
                     ConnectorWidget widget = new ConnectorWidget(source, target, ConnectorWidget.ConnectorType.DOUBLE_ARROW);
                     widget.setColor(new Color(11272192));
                     this.skillConnectors.add(widget);
                  }
               }
            });
         }
      });
      this.loadViewportTransforms(false);
   }

   @Override
   protected Collection<? extends AbstractWidget> getWidgets() {
      return this.skillWidgets.values();
   }

   protected abstract void initSkillWidget(T var1, String var2, SkillStyle var3, Map<String, W> var4, List<TalentGroupWidget> var5);

   protected abstract HashMap<String, SkillStyle> getStyles();

   protected void renderTooltip(PoseStack pPoseStack, ItemStack pItemStack, int pMouseX, int pMouseY) {
      super.renderTooltip(pPoseStack, pItemStack, pMouseX, pMouseY);
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (super.mouseClicked(mouseX, mouseY, button)) {
         return true;
      } else {
         Float midpoint = MiscUtils.getMidpoint(this.getBounds());
         int containerMouseX = (int)((mouseX - midpoint.x) / this.viewportScale - this.viewportTranslation.x);
         int containerMouseY = (int)((mouseY - midpoint.y) / this.viewportScale - this.viewportTranslation.y);
         boolean skillWidgetClicked = false;

         for (W skillWidget : this.skillWidgets.values()) {
            if (skillWidget.isMouseOver(containerMouseX, containerMouseY) && skillWidget.mouseClicked(containerMouseX, containerMouseY, button)) {
               if (this.selectedWidget != null) {
                  this.selectedWidget.deselect();
               }

               this.selectedWidget = skillWidget;
               this.selectedWidget.select();
               this.skillDialog.setSkillGroup(this.selectedWidget.getSkill());
               skillWidgetClicked = true;
               break;
            }
         }

         return skillWidgetClicked;
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

      for (TalentGroupWidget groupWidget : this.groupWidgets) {
         groupWidget.renderWidget(renderStack, containerBounds, mouseX, mouseY, containerMouseX, containerMouseY, pTicks, postContainerRender);
      }

      for (ConnectorWidget skillConnector : this.skillConnectors) {
         skillConnector.renderConnection(renderStack);
      }

      for (W skillWidget : this.skillWidgets.values()) {
         skillWidget.renderWidget(renderStack, containerBounds, mouseX, mouseY, containerMouseX, containerMouseY, pTicks, postContainerRender);
      }

      renderStack.popPose();
   }
}
