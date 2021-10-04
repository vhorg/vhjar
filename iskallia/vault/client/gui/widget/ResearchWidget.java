package iskallia.vault.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.client.gui.widget.connect.ConnectableWidget;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.research.type.Research;
import iskallia.vault.util.ResourceBoundary;
import java.awt.Rectangle;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.gui.GuiUtils;

public class ResearchWidget extends Widget implements ConnectableWidget, ComponentWidget {
   private static final int ICON_SIZE = 30;
   private static final ResourceLocation SKILL_WIDGET_RESOURCE = new ResourceLocation("the_vault", "textures/gui/skill-widget.png");
   public static final ResourceLocation RESEARCHES_RESOURCE = new ResourceLocation("the_vault", "textures/gui/researches.png");
   private final String researchName;
   private final ResearchTree researchTree;
   private final boolean locked;
   private final SkillStyle style;
   private boolean selected = false;
   private boolean hoverable = true;

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

   @Override
   public Rectangle getClickableBounds() {
      return new Rectangle(this.field_230690_l_, this.field_230691_m_, 30, 30);
   }

   @Override
   public Double getRenderPosition() {
      return new Double(this.field_230690_l_ + 2.5, this.field_230691_m_ + 2.5);
   }

   @Override
   public double getRenderWidth() {
      return 25.0;
   }

   @Override
   public double getRenderHeight() {
      return 25.0;
   }

   public void setHoverable(boolean hoverable) {
      this.hoverable = hoverable;
   }

   public boolean func_231047_b_(double mouseX, double mouseY) {
      return !this.hoverable ? false : this.getClickableBounds().contains(mouseX, mouseY);
   }

   public boolean func_231044_a_(double mouseX, double mouseY, int button) {
      if (this.selected) {
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

   public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, List<Runnable> postContainerRender) {
      this.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
      Matrix4f current = matrixStack.func_227866_c_().func_227870_a_().func_226601_d_();
      postContainerRender.add(() -> {
         RenderSystem.pushMatrix();
         RenderSystem.multMatrix(current);
         this.renderHover(matrixStack, mouseX, mouseY, partialTicks);
         RenderSystem.popMatrix();
      });
   }

   public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      this.renderIcon(matrixStack, mouseX, mouseY, partialTicks);
   }

   private void renderHover(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      if (this.func_231047_b_(mouseX, mouseY)) {
         List<ITextComponent> tTip = new ArrayList<>();
         tTip.add(new StringTextComponent(this.researchName));
         if (this.locked) {
            List<Research> preconditions = ModConfigs.SKILL_GATES.getGates().getDependencyResearches(this.researchName);
            if (!preconditions.isEmpty()) {
               tTip.add(new StringTextComponent("Requires:").func_240699_a_(TextFormatting.RED));
               preconditions.forEach(research -> tTip.add(new StringTextComponent("- " + research.getName()).func_240699_a_(TextFormatting.RED)));
            }
         }

         List<Research> conflicts = ModConfigs.SKILL_GATES.getGates().getLockedByResearches(this.researchName);
         if (!conflicts.isEmpty()) {
            tTip.add(new StringTextComponent("Cannot be unlocked alongside:").func_240699_a_(TextFormatting.RED));
            conflicts.forEach(research -> tTip.add(new StringTextComponent("- " + research.getName()).func_240699_a_(TextFormatting.RED)));
         }

         GuiUtils.drawHoveringText(
            matrixStack,
            tTip,
            this.field_230690_l_ + 15,
            this.field_230691_m_ + 15,
            Integer.MAX_VALUE,
            Integer.MAX_VALUE,
            -1,
            Minecraft.func_71410_x().field_71466_p
         );
         RenderSystem.enableBlend();
      }
   }

   private void renderIcon(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      ResourceBoundary resourceBoundary = this.style.frameType.getResourceBoundary();
      matrixStack.func_227860_a_();
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
      Minecraft.func_71410_x().field_71446_o.func_110577_a(RESEARCHES_RESOURCE);
      this.func_238474_b_(matrixStack, this.field_230690_l_ + 15, this.field_230691_m_ + 15, this.style.u, this.style.v, 16, 16);
      matrixStack.func_227865_b_();
   }
}
