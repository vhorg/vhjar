package iskallia.vault.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.client.gui.helper.Rectangle;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.talent.TalentGroup;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.util.ResourceBoundary;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

public class TalentWidget extends Widget {
   private static final int PIP_SIZE = 8;
   private static final int GAP_SIZE = 2;
   private static final int ICON_SIZE = 30;
   private static final int MAX_PIPs_INLINE = 4;
   private static final ResourceLocation SKILL_WIDGET_RESOURCE = new ResourceLocation("the_vault", "textures/gui/skill-widget.png");
   private static final ResourceLocation TALENTS_RESOURCE = new ResourceLocation("the_vault", "textures/gui/talents.png");
   TalentGroup<?> talentGroup;
   TalentTree talentTree;
   boolean locked;
   SkillStyle style;
   boolean selected;

   public TalentWidget(TalentGroup<?> talentGroup, TalentTree talentTree, SkillStyle style) {
      super(style.x, style.y, 48, pipRowCount(talentTree.getNodeOf(talentGroup).getLevel()) * 10 - 2, new StringTextComponent("the_vault.widgets.talent"));
      this.style = style;
      this.talentGroup = talentGroup;
      this.talentTree = talentTree;
      this.locked = ModConfigs.SKILL_GATES.getGates().isLocked(talentGroup, talentTree);
      this.selected = false;
   }

   public TalentGroup<?> getTalentGroup() {
      return this.talentGroup;
   }

   public TalentTree getTalentTree() {
      return this.talentTree;
   }

   public int getClickableWidth() {
      int onlyIconWidth = 34;
      int pipLineWidth = Math.min(this.talentGroup.getMaxLevel(), 4) * 10;
      return this.hasPips() ? Math.max(pipLineWidth, onlyIconWidth) : onlyIconWidth;
   }

   public int getClickableHeight() {
      int height = 34;
      if (this.hasPips()) {
         int lines = pipRowCount(this.talentGroup.getMaxLevel());
         height += 2;
         height += lines * 8 + (lines - 1) * 2;
      }

      return height;
   }

   public Rectangle getClickableBounds() {
      Rectangle bounds = new Rectangle();
      bounds.x0 = this.field_230690_l_ - this.getClickableWidth() / 2;
      bounds.y0 = this.field_230691_m_ - 15 - 2;
      bounds.x1 = bounds.x0 + this.getClickableWidth();
      bounds.y1 = bounds.y0 + this.getClickableHeight();
      return bounds;
   }

   public boolean hasPips() {
      return !this.locked && this.talentGroup.getMaxLevel() > 1;
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
      if (this.hasPips()) {
         this.renderPips(matrixStack, mouseX, mouseY, partialTicks);
      }
   }

   public void renderIcon(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      ResourceBoundary resourceBoundary = this.style.frameType.getResourceBoundary();
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(-15.0, -15.0, 0.0);
      Minecraft.func_71410_x().field_71446_o.func_110577_a(resourceBoundary.getResource());
      int vOffset = this.locked
         ? 62
         : (!this.selected && !this.func_231047_b_(mouseX, mouseY) ? (this.talentTree.getNodeOf(this.talentGroup).getLevel() >= 1 ? 31 : 0) : -31);
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
      Minecraft.func_71410_x().field_71446_o.func_110577_a(this.locked ? SKILL_WIDGET_RESOURCE : TALENTS_RESOURCE);
      if (this.locked) {
         this.func_238474_b_(matrixStack, this.field_230690_l_ + 3, this.field_230691_m_ + 1, 10, 124, 10, 14);
      } else {
         this.func_238474_b_(matrixStack, this.field_230690_l_, this.field_230691_m_, this.style.u, this.style.v, 16, 16);
      }

      matrixStack.func_227865_b_();
   }

   public void renderPips(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      Minecraft.func_71410_x().field_71446_o.func_110577_a(SKILL_WIDGET_RESOURCE);
      int rowCount = pipRowCount(this.talentGroup.getMaxLevel());
      int remainingPips = this.talentGroup.getMaxLevel();
      int remainingFilledPips = this.talentTree.getNodeOf(this.talentGroup).getLevel();

      for (int r = 0; r < rowCount; r++) {
         this.renderPipLine(
            matrixStack, this.field_230690_l_, this.field_230691_m_ + 15 + 4 + r * 10, Math.min(4, remainingPips), Math.min(4, remainingFilledPips)
         );
         remainingPips -= 4;
         remainingFilledPips -= 4;
      }
   }

   public void renderPipLine(MatrixStack matrixStack, int x, int y, int count, int filledCount) {
      int lineWidth = count * 8 + (count - 1) * 2;
      int remainingFilled = filledCount;
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(x, y, 0.0);
      matrixStack.func_227861_a_(-lineWidth / 2.0F, -4.0, 0.0);

      for (int i = 0; i < count; i++) {
         if (remainingFilled > 0) {
            this.func_238474_b_(matrixStack, 0, 0, 1, 133, 8, 8);
            remainingFilled--;
         } else {
            this.func_238474_b_(matrixStack, 0, 0, 1, 124, 8, 8);
         }

         matrixStack.func_227861_a_(10.0, 0.0, 0.0);
      }

      matrixStack.func_227865_b_();
   }

   public static int pipRowCount(int level) {
      return (int)Math.ceil(level / 4.0F);
   }
}
