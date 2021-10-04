package iskallia.vault.client.gui.tab;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.client.gui.component.TalentDialog;
import iskallia.vault.client.gui.screen.SkillTreeScreen;
import iskallia.vault.client.gui.widget.TalentWidget;
import iskallia.vault.client.gui.widget.connect.ConnectorWidget;
import iskallia.vault.container.SkillTreeContainer;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.util.MiscUtils;
import java.awt.Color;
import java.awt.geom.Point2D.Float;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.minecraft.util.text.StringTextComponent;

public class TalentsTab extends SkillTab {
   private final Map<String, TalentWidget> talentWidgets = new HashMap<>();
   private final List<ConnectorWidget> talentConnectors = new LinkedList<>();
   private final TalentDialog talentDialog;
   private TalentWidget selectedWidget;

   public TalentsTab(TalentDialog talentDialog, SkillTreeScreen parentScreen) {
      super(parentScreen, new StringTextComponent("Talents Tab"));
      this.talentDialog = talentDialog;
   }

   @Override
   public void refresh() {
      this.talentWidgets.clear();
      TalentTree talentTree = ((SkillTreeContainer)this.parentScreen.func_212873_a_()).getTalentTree();
      ModConfigs.TALENTS_GUI
         .getStyles()
         .forEach((talentName, style) -> this.talentWidgets.put(talentName, new TalentWidget(ModConfigs.TALENTS.getByName(talentName), talentTree, style)));
      ModConfigs.TALENTS_GUI.getStyles().forEach((researchName, style) -> {
         TalentWidget target = this.talentWidgets.get(researchName);
         if (target != null) {
            ModConfigs.SKILL_GATES.getGates().getDependencyTalents(researchName).forEach(dependentOn -> {
               TalentWidget source = this.talentWidgets.get(dependentOn.getParentName());
               if (source != null) {
                  this.talentConnectors.add(new ConnectorWidget(source, target, ConnectorWidget.ConnectorType.ARROW));
               }
            });
            ModConfigs.SKILL_GATES.getGates().getLockedByTalents(researchName).forEach(dependentOn -> {
               TalentWidget source = this.talentWidgets.get(dependentOn.getParentName());
               if (source != null) {
                  ConnectorWidget widget = new ConnectorWidget(source, target, ConnectorWidget.ConnectorType.DOUBLE_ARROW);
                  widget.setColor(new Color(11272192));
                  this.talentConnectors.add(widget);
               }
            });
         }
      });
   }

   @Override
   public String getTabName() {
      return "Talents";
   }

   @Override
   public boolean func_231044_a_(double mouseX, double mouseY, int button) {
      boolean mouseClicked = super.func_231044_a_(mouseX, mouseY, button);
      Float midpoint = MiscUtils.getMidpoint(this.parentScreen.getContainerBounds());
      int containerMouseX = (int)((mouseX - midpoint.x) / this.viewportScale - this.viewportTranslation.field_189982_i);
      int containerMouseY = (int)((mouseY - midpoint.y) / this.viewportScale - this.viewportTranslation.field_189983_j);

      for (TalentWidget abilityWidget : this.talentWidgets.values()) {
         if (abilityWidget.func_231047_b_(containerMouseX, containerMouseY) && abilityWidget.func_231044_a_(containerMouseX, containerMouseY, button)) {
            if (this.selectedWidget != null) {
               this.selectedWidget.deselect();
            }

            this.selectedWidget = abilityWidget;
            this.selectedWidget.select();
            this.talentDialog.setTalentGroup(this.selectedWidget.getTalentGroup());
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

      for (ConnectorWidget talentConnector : this.talentConnectors) {
         talentConnector.renderConnection(renderStack, containerMouseX, containerMouseY, pTicks, this.viewportScale);
      }

      for (TalentWidget abilityWidget : this.talentWidgets.values()) {
         abilityWidget.renderWidget(renderStack, containerMouseX, containerMouseY, pTicks, postContainerRender);
      }

      renderStack.func_227865_b_();
   }
}
