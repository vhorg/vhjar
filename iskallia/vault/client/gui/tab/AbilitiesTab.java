package iskallia.vault.client.gui.tab;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.client.gui.component.AbilityDialog;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.client.gui.screen.SkillTreeScreen;
import iskallia.vault.client.gui.widget.AbilityWidget;
import iskallia.vault.client.gui.widget.connect.ConnectorWidget;
import iskallia.vault.container.SkillTreeContainer;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.ability.AbilityRegistry;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.effect.AbilityEffect;
import iskallia.vault.util.MiscUtils;
import java.awt.Color;
import java.awt.geom.Point2D.Float;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.minecraft.util.text.StringTextComponent;

public class AbilitiesTab extends SkillTab {
   private final Map<String, AbilityWidget> abilityWidgets = new HashMap<>();
   private final List<ConnectorWidget> abilityConnectors = new LinkedList<>();
   private final AbilityDialog abilityDialog;
   private AbilityWidget selectedWidget;

   public AbilitiesTab(AbilityDialog abilityDialog, SkillTreeScreen parentScreen) {
      super(parentScreen, new StringTextComponent("Abilities Tab"));
      this.abilityDialog = abilityDialog;
   }

   @Override
   public void refresh() {
      this.abilityWidgets.clear();
      AbilityTree abilityTree = ((SkillTreeContainer)this.parentScreen.func_212873_a_()).getAbilityTree();
      ModConfigs.ABILITIES_GUI
         .getStyles()
         .forEach((abilityName, style) -> this.abilityWidgets.put(abilityName, new AbilityWidget(abilityName, abilityTree, style)));
      ModConfigs.ABILITIES_GUI.getStyles().forEach((abilityName, style) -> {
         AbilityEffect<?> ability = AbilityRegistry.getAbility(abilityName);
         if (!abilityName.equals(ability.getAbilityGroupName())) {
            AbilityWidget abilityGroup = this.abilityWidgets.get(ability.getAbilityGroupName());
            AbilityWidget thisAbility = this.abilityWidgets.get(abilityName);
            if (abilityGroup != null && thisAbility != null) {
               ConnectorWidget widget = new ConnectorWidget(abilityGroup, thisAbility, ConnectorWidget.ConnectorType.LINE);
               if (abilityName.equals(abilityTree.getNodeOf(ability).getSpecialization())) {
                  widget.setColor(new Color(13021470));
               } else {
                  widget.setColor(new Color(5592405));
               }

               this.abilityConnectors.add(widget);
            }
         }
      });
   }

   @Override
   public String getTabName() {
      return "Abilities";
   }

   @Override
   public boolean func_231044_a_(double mouseX, double mouseY, int button) {
      boolean mouseClicked = super.func_231044_a_(mouseX, mouseY, button);
      Float midpoint = MiscUtils.getMidpoint(this.parentScreen.getContainerBounds());
      int containerMouseX = (int)((mouseX - midpoint.x) / this.viewportScale - this.viewportTranslation.field_189982_i);
      int containerMouseY = (int)((mouseY - midpoint.y) / this.viewportScale - this.viewportTranslation.field_189983_j);

      for (AbilityWidget abilityWidget : this.abilityWidgets.values()) {
         if (abilityWidget.func_231047_b_(containerMouseX, containerMouseY) && abilityWidget.func_231044_a_(containerMouseX, containerMouseY, button)) {
            if (this.selectedWidget != null) {
               this.selectedWidget.deselect();
            }

            this.selectedWidget = abilityWidget;
            this.selectedWidget.select();
            this.abilityDialog.setAbilityWidget(this.selectedWidget.getAbilityName());
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
      renderStack.func_227860_a_();
      renderStack.func_227861_a_(0.0, 10.0, 0.0);
      renderStack.func_227862_a_(1.6F, 1.6F, 1.6F);
      UIHelper.drawFacingPlayer(renderStack, containerMouseX, containerMouseY);
      renderStack.func_227865_b_();

      for (ConnectorWidget researchConnector : this.abilityConnectors) {
         researchConnector.renderConnection(renderStack, containerMouseX, containerMouseY, pTicks, this.viewportScale);
      }

      for (AbilityWidget abilityWidget : this.abilityWidgets.values()) {
         abilityWidget.renderWidget(renderStack, containerMouseX, containerMouseY, pTicks, postContainerRender);
      }

      renderStack.func_227865_b_();
   }
}
