package iskallia.vault.client.gui.screen.player.legacy.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import iskallia.vault.client.atlas.ITextureAtlas;
import iskallia.vault.client.gui.screen.player.legacy.widget.connect.ConnectableWidget;
import iskallia.vault.client.gui.widget.ComponentWidget;
import iskallia.vault.client.util.TooltipUtil;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModTextureAtlases;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.research.type.Research;
import iskallia.vault.util.ResourceBoundary;
import java.awt.Rectangle;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;

public class ResearchWidget extends AbstractWidget implements ConnectableWidget, ComponentWidget {
   private static final int ICON_SIZE = 30;
   private final String researchName;
   private final ResearchTree researchTree;
   private final boolean locked;
   private final SkillStyle style;
   private boolean selected = false;
   private boolean hoverable = true;

   public ResearchWidget(String researchName, ResearchTree researchTree, SkillStyle style) {
      super(style.x, style.y, 30, 30, new TextComponent("the_vault.widgets.research"));
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
      return new Rectangle(this.x, this.y, 30, 30);
   }

   @Override
   public Double getRenderPosition() {
      return new Double(this.x + 2.5, this.y + 2.5);
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

   public boolean isMouseOver(double mouseX, double mouseY) {
      return !this.hoverable ? false : this.getClickableBounds().contains(mouseX, mouseY);
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (this.selected) {
         return false;
      } else {
         this.playDownSound(Minecraft.getInstance().getSoundManager());
         return true;
      }
   }

   public void select() {
      this.selected = true;
   }

   public void deselect() {
      this.selected = false;
   }

   public void renderWidget(
      PoseStack matrixStack,
      Rectangle containerBounds,
      int mouseX,
      int mouseY,
      int containerMouseX,
      int containerMouseY,
      float partialTicks,
      List<Runnable> postContainerRender
   ) {
      this.render(matrixStack, containerMouseX, containerMouseY, partialTicks);
      if (containerBounds.contains(mouseX, mouseY)) {
         Matrix4f current = matrixStack.last().pose().copy();
         postContainerRender.add(() -> {
            matrixStack.pushPose();
            matrixStack.mulPoseMatrix(current);
            this.renderHover(matrixStack, containerMouseX, containerMouseY, partialTicks);
            matrixStack.popPose();
         });
      }
   }

   public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      this.renderIcon(matrixStack, mouseX, mouseY, partialTicks);
   }

   private void renderHover(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      if (this.isMouseOver(mouseX, mouseY)) {
         List<FormattedCharSequence> tTip = new ArrayList<>();
         tTip.add(FormattedCharSequence.forward(this.researchName, Style.EMPTY));
         if (this.locked) {
            List<Research> preconditions = ModConfigs.SKILL_GATES.getGates().getDependencyResearches(this.researchName);
            if (!preconditions.isEmpty()) {
               String tTipRequirement = ModConfigs.SKILL_GATES.getGates().hasEitherSkillGate(this.getResearchName()) ? " any of" : "";
               tTip.add(FormattedCharSequence.forward("Requires" + tTipRequirement + ":", Style.EMPTY.withColor(ChatFormatting.RED)));
               preconditions.forEach(research -> tTip.add(FormattedCharSequence.forward("- " + research.getName(), Style.EMPTY.withColor(ChatFormatting.RED))));
            }
         }

         List<Research> conflicts = ModConfigs.SKILL_GATES.getGates().getLockedByResearches(this.researchName);
         if (!conflicts.isEmpty()) {
            tTip.add(FormattedCharSequence.forward("Cannot be unlocked alongside:", Style.EMPTY.withColor(ChatFormatting.RED)));
            conflicts.forEach(research -> tTip.add(FormattedCharSequence.forward("- " + research.getName(), Style.EMPTY.withColor(ChatFormatting.RED))));
         }

         TooltipUtil.renderTooltip(matrixStack, tTip, mouseX, mouseY, Integer.MAX_VALUE, Integer.MAX_VALUE);
         RenderSystem.enableBlend();
      }
   }

   private void renderIcon(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      ResourceBoundary resourceBoundary = this.style.frameType.getResourceBoundary();
      matrixStack.pushPose();
      RenderSystem.setShaderTexture(0, resourceBoundary.getResource());
      int vOffset = this.locked
         ? 62
         : (!this.selected && !this.isMouseOver(mouseX, mouseY) ? (this.researchTree.getResearchesDone().contains(this.researchName) ? 31 : 0) : -31);
      this.blit(
         matrixStack, this.x, this.y, resourceBoundary.getU(), resourceBoundary.getV() + vOffset, resourceBoundary.getWidth(), resourceBoundary.getHeight()
      );
      matrixStack.popPose();
      matrixStack.pushPose();
      matrixStack.translate(-8.0, -8.0, 0.0);
      ITextureAtlas atlas = ModTextureAtlases.RESEARCHES.get();
      RenderSystem.setShaderTexture(0, atlas.getAtlasResourceLocation());
      GuiComponent.blit(matrixStack, this.x + 15, this.y + 15, 0, 16, 16, atlas.getSprite(this.style.icon));
      matrixStack.popPose();
   }

   public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
   }
}
