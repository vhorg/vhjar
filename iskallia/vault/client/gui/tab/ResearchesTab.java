package iskallia.vault.client.gui.tab;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.client.gui.component.ResearchDialog;
import iskallia.vault.client.gui.screen.SkillTreeScreen;
import iskallia.vault.client.gui.widget.ResearchGroupWidget;
import iskallia.vault.client.gui.widget.ResearchWidget;
import iskallia.vault.client.gui.widget.connect.ConnectorWidget;
import iskallia.vault.config.entry.ResearchGroupStyle;
import iskallia.vault.container.SkillTreeContainer;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.util.MiscUtils;
import java.awt.geom.Point2D.Float;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.minecraft.util.text.StringTextComponent;

public class ResearchesTab extends SkillTab {
   private final List<ResearchGroupWidget> researchGroups = new LinkedList<>();
   private final Map<String, ResearchWidget> researchWidgets = new HashMap<>();
   private final List<ConnectorWidget> researchConnectors = new LinkedList<>();
   private final ResearchDialog researchDialog;
   private ResearchWidget selectedWidget;

   public ResearchesTab(ResearchDialog researchDialog, SkillTreeScreen parentScreen) {
      super(parentScreen, new StringTextComponent("Researches Tab"));
      this.researchDialog = researchDialog;
   }

   @Override
   public void refresh() {
      this.researchGroups.clear();
      this.researchWidgets.clear();
      this.researchConnectors.clear();
      ResearchTree researchTree = ((SkillTreeContainer)this.parentScreen.func_212873_a_()).getResearchTree();
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
                  this.researchConnectors.add(new ConnectorWidget(source, target, ConnectorWidget.ConnectorType.ARROW));
               }
            });
            ModConfigs.SKILL_GATES.getGates().getLockedByResearches(researchName).forEach(dependentOn -> {
               ResearchWidget source = this.researchWidgets.get(dependentOn.getName());
               if (source != null) {
                  this.researchConnectors.add(new ConnectorWidget(source, target, ConnectorWidget.ConnectorType.DOUBLE_ARROW));
               }
            });
         }
      });
   }

   @Override
   public String getTabName() {
      return "Researches";
   }

   @Override
   public boolean func_231044_a_(double mouseX, double mouseY, int button) {
      boolean mouseClicked = super.func_231044_a_(mouseX, mouseY, button);
      Float midpoint = MiscUtils.getMidpoint(this.parentScreen.getContainerBounds());
      int containerMouseX = (int)((mouseX - midpoint.x) / this.viewportScale - this.viewportTranslation.field_189982_i);
      int containerMouseY = (int)((mouseY - midpoint.y) / this.viewportScale - this.viewportTranslation.field_189983_j);

      for (ResearchWidget researchWidget : this.researchWidgets.values()) {
         if (researchWidget.func_231047_b_(containerMouseX, containerMouseY) && researchWidget.func_231044_a_(containerMouseX, containerMouseY, button)) {
            if (this.selectedWidget != null) {
               this.selectedWidget.deselect();
            }

            this.selectedWidget = researchWidget;
            this.selectedWidget.select();
            this.researchDialog.setResearchName(this.selectedWidget.getResearchName());
            break;
         }
      }

      return mouseClicked;
   }

   @Override
   public void renderTabForeground(MatrixStack renderStack, int mouseX, int mouseY, float pTicks, List<Runnable> postContainerRender) {
      RenderSystem.enableBlend();
      Float midpoint = MiscUtils.getMidpoint(this.parentScreen.getContainerBounds());
      renderStack.func_227860_a_();
      renderStack.func_227861_a_(midpoint.x, midpoint.y, 0.0);
      renderStack.func_227862_a_(this.viewportScale, this.viewportScale, 1.0F);
      renderStack.func_227861_a_(this.viewportTranslation.field_189982_i, this.viewportTranslation.field_189983_j, 0.0);
      int containerMouseX = (int)((mouseX - midpoint.x) / this.viewportScale - this.viewportTranslation.field_189982_i);
      int containerMouseY = (int)((mouseY - midpoint.y) / this.viewportScale - this.viewportTranslation.field_189983_j);

      for (ResearchGroupWidget researchGroupWidget : this.researchGroups) {
         researchGroupWidget.func_230430_a_(renderStack, containerMouseX, containerMouseY, pTicks);
      }

      for (ConnectorWidget researchConnector : this.researchConnectors) {
         researchConnector.renderConnection(renderStack, containerMouseX, containerMouseY, pTicks, this.viewportScale);
      }

      for (ResearchWidget researchWidget : this.researchWidgets.values()) {
         researchWidget.renderWidget(renderStack, containerMouseX, containerMouseY, pTicks, postContainerRender);
      }

      renderStack.func_227865_b_();
   }
}
