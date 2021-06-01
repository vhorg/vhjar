package iskallia.vault.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.client.gui.helper.Rectangle;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.util.ResourceBoundary;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

public class ResearchWidget extends Widget {
   private static final int ICON_SIZE = 30;
   private static final ResourceLocation SKILL_WIDGET_RESOURCE = new ResourceLocation("the_vault", "textures/gui/skill-widget.png");
   private static final ResourceLocation RESEARCHES_RESOURCE = new ResourceLocation("the_vault", "textures/gui/researches.png");
   String researchName;
   ResearchTree researchTree;
   boolean locked;
   SkillStyle style;
   boolean selected;

   public ResearchWidget(String researchName, ResearchTree researchTree, SkillStyle style) {
      super(style.x, style.y, 30, 30, new StringTextComponent("the_vault.widgets.research"));
      this.style = style;
      this.locked = ModConfigs.SKILL_GATES.getGates().isLocked(researchName, researchTree);
      this.researchName = researchName;
      this.researchTree = researchTree;
   }

   public ResearchTree getResearchTree() {
      return this.researchTree;
   }

   public String getResearchName() {
      return this.researchName;
   }

   public Rectangle getClickableBounds() {
      Rectangle bounds = new Rectangle();
      bounds.x0 = this.field_230690_l_ - 15;
      bounds.y0 = this.field_230691_m_ - 15;
      bounds.x1 = this.field_230690_l_ + 15;
      bounds.y1 = this.field_230691_m_ + 15;
      return bounds;
   }

   public boolean func_231047_b_(double mouseX, double mouseY) {
      Rectangle clickableBounds = this.getClickableBounds();
      return clickableBounds.x0 <= mouseX && mouseX <= clickableBounds.x1 && clickableBounds.y0 <= mouseY && mouseY <= clickableBounds.y1;
   }

   public boolean func_231044_a_(double mouseX, double mouseY, int button) {
      if (this.locked) {
         return false;
      } else if (this.selected) {
         return false;
      } else {
         this.func_230988_a_(Minecraft.func_71410_x().func_147118_V());
         return true;
      }
   }

   public void select() {
      this.selected = true;
   }

   public void deselect() {
      this.selected = false;
   }

   public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      this.renderIcon(matrixStack, mouseX, mouseY, partialTicks);
   }

   public void renderIcon(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      ResourceBoundary resourceBoundary = this.style.frameType.getResourceBoundary();
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(-15.0, -15.0, 0.0);
      Minecraft.func_71410_x().field_71446_o.func_110577_a(resourceBoundary.getResource());
      int vOffset = this.locked
         ? 62
         : (!this.selected && !this.func_231047_b_(mouseX, mouseY) ? (this.researchTree.getResearchesDone().contains(this.researchName) ? 31 : 0) : -31);
      this.func_238474_b_(
         matrixStack,
         this.field_230690_l_,
         this.field_230691_m_,
         resourceBoundary.getU(),
         resourceBoundary.getV() + vOffset,
         resourceBoundary.getW(),
         resourceBoundary.getH()
      );
      matrixStack.func_227865_b_();
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(-8.0, -8.0, 0.0);
      Minecraft.func_71410_x().field_71446_o.func_110577_a(this.locked ? SKILL_WIDGET_RESOURCE : RESEARCHES_RESOURCE);
      if (this.locked) {
         this.func_238474_b_(matrixStack, this.field_230690_l_ + 3, this.field_230691_m_ + 1, 10, 124, 10, 14);
      } else {
         this.func_238474_b_(matrixStack, this.field_230690_l_, this.field_230691_m_, this.style.u, this.style.v, 16, 16);
      }

      matrixStack.func_227865_b_();
   }
}
