package iskallia.vault.client.gui.tab;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.client.gui.screen.SkillTreeScreen;
import iskallia.vault.client.gui.widget.TalentWidget;
import iskallia.vault.container.SkillTreeContainer;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.talent.TalentTree;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.text.StringTextComponent;

public class TalentsTab extends SkillTab {
   private List<TalentWidget> talentWidgets = new LinkedList<>();
   private TalentWidget selectedWidget;

   public TalentsTab(SkillTreeScreen parentScreen) {
      super(parentScreen, new StringTextComponent("Talents Tab"));
   }

   @Override
   public void refresh() {
      this.talentWidgets.clear();
      TalentTree talentTree = ((SkillTreeContainer)this.parentScreen.func_212873_a_()).getTalentTree();
      ModConfigs.TALENTS_GUI
         .getStyles()
         .forEach((abilityName, style) -> this.talentWidgets.add(new TalentWidget(ModConfigs.TALENTS.getByName(abilityName), talentTree, style)));
   }

   @Override
   public boolean func_231044_a_(double mouseX, double mouseY, int button) {
      boolean mouseClicked = super.func_231044_a_(mouseX, mouseY, button);
      Vector2f midpoint = this.parentScreen.getContainerBounds().midpoint();
      int containerMouseX = (int)((mouseX - midpoint.field_189982_i) / this.viewportScale - this.viewportTranslation.field_189982_i);
      int containerMouseY = (int)((mouseY - midpoint.field_189983_j) / this.viewportScale - this.viewportTranslation.field_189983_j);

      for (TalentWidget abilityWidget : this.talentWidgets) {
         if (abilityWidget.func_231047_b_(containerMouseX, containerMouseY) && abilityWidget.func_231044_a_(containerMouseX, containerMouseY, button)) {
            if (this.selectedWidget != null) {
               this.selectedWidget.deselect();
            }

            this.selectedWidget = abilityWidget;
            this.selectedWidget.select();
            this.parentScreen.getTalentDialog().setTalentGroup(this.selectedWidget.getTalentGroup());
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

      for (TalentWidget abilityWidget : this.talentWidgets) {
         abilityWidget.func_230430_a_(matrixStack, containerMouseX, containerMouseY, partialTicks);
      }

      matrixStack.func_227865_b_();
   }
}
