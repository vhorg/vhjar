package iskallia.vault.client.gui.tab;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.client.gui.screen.SkillTreeScreen;
import iskallia.vault.client.gui.widget.AbilityWidget;
import iskallia.vault.container.SkillTreeContainer;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.ability.AbilityTree;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.text.StringTextComponent;

public class AbilitiesTab extends SkillTab {
   private List<AbilityWidget> abilityWidgets = new LinkedList<>();
   private AbilityWidget selectedWidget;

   public AbilitiesTab(SkillTreeScreen parentScreen) {
      super(parentScreen, new StringTextComponent("Abilities Tab"));
   }

   @Override
   public void refresh() {
      this.abilityWidgets.clear();
      AbilityTree abilityTree = ((SkillTreeContainer)this.parentScreen.func_212873_a_()).getAbilityTree();
      ModConfigs.ABILITIES_GUI
         .getStyles()
         .forEach((abilityName, style) -> this.abilityWidgets.add(new AbilityWidget(ModConfigs.ABILITIES.getByName(abilityName), abilityTree, style)));
   }

   @Override
   public boolean func_231044_a_(double mouseX, double mouseY, int button) {
      boolean mouseClicked = super.func_231044_a_(mouseX, mouseY, button);
      Vector2f midpoint = this.parentScreen.getContainerBounds().midpoint();
      int containerMouseX = (int)((mouseX - midpoint.field_189982_i) / this.viewportScale - this.viewportTranslation.field_189982_i);
      int containerMouseY = (int)((mouseY - midpoint.field_189983_j) / this.viewportScale - this.viewportTranslation.field_189983_j);

      for (AbilityWidget abilityWidget : this.abilityWidgets) {
         if (abilityWidget.func_231047_b_(containerMouseX, containerMouseY) && abilityWidget.func_231044_a_(containerMouseX, containerMouseY, button)) {
            if (this.selectedWidget != null) {
               this.selectedWidget.deselect();
            }

            this.selectedWidget = abilityWidget;
            this.selectedWidget.select();
            this.parentScreen.getAbilityDialog().setAbilityGroup(this.selectedWidget.getAbilityGroup());
            break;
         }
      }

      return mouseClicked;
   }

   public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      RenderSystem.enableBlend();
      Vector2f midpoint = this.parentScreen.getContainerBounds().midpoint();
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(midpoint.field_189982_i, midpoint.field_189983_j, 0.0);
      matrixStack.func_227862_a_(this.viewportScale, this.viewportScale, 1.0F);
      matrixStack.func_227861_a_(this.viewportTranslation.field_189982_i, this.viewportTranslation.field_189983_j, 0.0);
      int containerMouseX = (int)((mouseX - midpoint.field_189982_i) / this.viewportScale - this.viewportTranslation.field_189982_i);
      int containerMouseY = (int)((mouseY - midpoint.field_189983_j) / this.viewportScale - this.viewportTranslation.field_189983_j);

      for (AbilityWidget abilityWidget : this.abilityWidgets) {
         abilityWidget.func_230430_a_(matrixStack, containerMouseX, containerMouseY, partialTicks);
      }

      matrixStack.func_227865_b_();
   }
}
