package iskallia.vault.client.gui.tab;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.client.gui.screen.SkillTreeScreen;
import iskallia.vault.client.gui.widget.ResearchWidget;
import iskallia.vault.container.SkillTreeContainer;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.research.ResearchTree;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.text.StringTextComponent;

public class ResearchesTab extends SkillTab {
   private List<ResearchWidget> researchWidgets = new LinkedList<>();
   private ResearchWidget selectedWidget;

   public ResearchesTab(SkillTreeScreen parentScreen) {
      super(parentScreen, new StringTextComponent("Researches Tab"));
   }

   @Override
   public void refresh() {
      this.researchWidgets.clear();
      ResearchTree researchTree = ((SkillTreeContainer)this.parentScreen.func_212873_a_()).getResearchTree();
      ModConfigs.RESEARCHES_GUI.getStyles().forEach((researchName, style) -> this.researchWidgets.add(new ResearchWidget(researchName, researchTree, style)));
   }

   @Override
   public boolean func_231044_a_(double mouseX, double mouseY, int button) {
      boolean mouseClicked = super.func_231044_a_(mouseX, mouseY, button);
      Vector2f midpoint = this.parentScreen.getContainerBounds().midpoint();
      int containerMouseX = (int)((mouseX - midpoint.field_189982_i) / this.viewportScale - this.viewportTranslation.field_189982_i);
      int containerMouseY = (int)((mouseY - midpoint.field_189983_j) / this.viewportScale - this.viewportTranslation.field_189983_j);

      for (ResearchWidget researchWidget : this.researchWidgets) {
         if (researchWidget.func_231047_b_(containerMouseX, containerMouseY) && researchWidget.func_231044_a_(containerMouseX, containerMouseY, button)) {
            if (this.selectedWidget != null) {
               this.selectedWidget.deselect();
            }

            this.selectedWidget = researchWidget;
            this.selectedWidget.select();
            this.parentScreen.getResearchDialog().setResearchName(this.selectedWidget.getResearchName());
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

      for (ResearchWidget researchWidget : this.researchWidgets) {
         researchWidget.func_230430_a_(matrixStack, containerMouseX, containerMouseY, partialTicks);
      }

      matrixStack.func_227865_b_();
   }
}
