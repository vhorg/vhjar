package iskallia.vault.client.gui.screen.player.legacy.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import iskallia.vault.client.atlas.ITextureAtlas;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.gui.screen.player.legacy.widget.connect.ConnectableWidget;
import iskallia.vault.client.gui.widget.ComponentWidget;
import iskallia.vault.client.util.TooltipUtil;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModTextureAtlases;
import iskallia.vault.skill.base.TieredSkill;
import iskallia.vault.skill.tree.SkillTree;
import iskallia.vault.util.ResourceBoundary;
import java.awt.Rectangle;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

public class SkillWidget<T extends SkillTree> extends AbstractWidget implements ConnectableWidget, ComponentWidget {
   protected static final int PIP_SIZE = 8;
   protected static final int GAP_SIZE = 2;
   protected static final int ICON_SIZE = 30;
   protected static final int MAX_PIPs_INLINE = 4;
   protected static final ResourceLocation SKILL_WIDGET_RESOURCE = new ResourceLocation("the_vault", "textures/gui/skill_widget.png");
   protected final T skillTree;
   protected boolean renderPips = true;
   TieredSkill skill;
   boolean locked;
   SkillStyle style;
   boolean selected;

   public SkillWidget(T skillTree, Component pMessage, TieredSkill skill, SkillStyle style) {
      super(style.x, style.y, 48, 0, pMessage);
      this.skillTree = skillTree;
      this.skill = skill;
      this.locked = ModConfigs.SKILL_GATES.getGates().isLocked(skill.getId(), skillTree) || VaultBarOverlay.vaultLevel < skill.getUnlockLevel();
      this.style = style;
      this.selected = false;
      this.height = pipRowCount(skill.getTier()) * 10 - 2;
   }

   public TieredSkill getSkill() {
      return this.skill;
   }

   public T getSkillTree() {
      return this.skillTree;
   }

   public int getClickableWidth() {
      int onlyIconWidth = 34;
      int pipLineWidth = Math.min(this.skill.getMaxTier(), 4) * 10;
      return this.hasPips() ? Math.max(pipLineWidth, onlyIconWidth) : onlyIconWidth;
   }

   public int getClickableHeight() {
      int height = 34;
      if (this.hasPips()) {
         int lines = TalentWidget.pipRowCount(this.skill.getMaxTier());
         height += 2;
         height += lines * 8 + (lines - 1) * 2;
      }

      return height;
   }

   public boolean hasPips() {
      return this.renderPips && this.skill.getMaxTier() > 1;
   }

   public static int pipRowCount(int level) {
      return (int)Math.ceil(level / 4.0F);
   }

   public void updateNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
   }

   @Override
   public Double getRenderPosition() {
      return new Double(this.x - this.getRenderWidth() / 2.0, this.y - this.getRenderHeight() / 2.0);
   }

   @Override
   public double getRenderWidth() {
      return 22.0;
   }

   @Override
   public double getRenderHeight() {
      return 22.0;
   }

   @Override
   public Rectangle getClickableBounds() {
      return new Rectangle(this.x - this.getClickableWidth() / 2, this.y - 15 - 2, this.getClickableWidth(), this.getClickableHeight());
   }

   public void setRenderPips(boolean renderPips) {
      this.renderPips = renderPips;
   }

   public boolean isMouseOver(double mouseX, double mouseY) {
      return this.getClickableBounds().contains(mouseX, mouseY);
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

   private void renderHover(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      if (this.isMouseOver(mouseX, mouseY)) {
         List<FormattedCharSequence> tTip = new ArrayList<>();
         tTip.add(FormattedCharSequence.forward(this.skill.getName(), Style.EMPTY));
         if (this.locked) {
            List<String> preconditions = ModConfigs.SKILL_GATES.getGates().getDependencySkills(this.skill.getId());
            if (!preconditions.isEmpty()) {
               tTip.add(FormattedCharSequence.forward("Requires:", Style.EMPTY.withColor(ChatFormatting.RED)));
               preconditions.forEach(
                  skillName -> this.getSkillTree()
                     .getForId(skillName)
                     .ifPresent(skill -> tTip.add(FormattedCharSequence.forward("- " + skill.getName(), Style.EMPTY.withColor(ChatFormatting.RED))))
               );
            }
         }

         List<String> conflicts = ModConfigs.SKILL_GATES.getGates().getLockedBySkills(this.skill.getId());
         if (!conflicts.isEmpty()) {
            tTip.add(FormattedCharSequence.forward("Cannot be unlocked alongside:", Style.EMPTY.withColor(ChatFormatting.RED)));
            conflicts.forEach(
               skillName -> this.getSkillTree()
                  .getForId(skillName)
                  .ifPresent(skill -> tTip.add(FormattedCharSequence.forward("- " + skill.getName(), Style.EMPTY.withColor(ChatFormatting.RED))))
            );
         }

         if (this.skill.getTier() < this.skill.getMaxTier()) {
            int levelRequirement = this.skill.getUnlockLevel();
            if (VaultBarOverlay.vaultLevel < levelRequirement) {
               tTip.add(FormattedCharSequence.forward("Requires level: " + levelRequirement, Style.EMPTY.withColor(ChatFormatting.RED)));
            }
         }

         TooltipUtil.renderTooltip(matrixStack, tTip, mouseX, mouseY, Integer.MAX_VALUE, Integer.MAX_VALUE);
         RenderSystem.enableBlend();
      }
   }

   public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      this.renderIcon(matrixStack, mouseX, mouseY, partialTicks);
      if (this.hasPips()) {
         this.renderPips(matrixStack, mouseX, mouseY, partialTicks);
      }
   }

   public void renderIcon(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      ResourceBoundary resourceBoundary = this.style.frameType.getResourceBoundary();
      matrixStack.pushPose();
      matrixStack.translate(-15.0, -15.0, 0.0);
      RenderSystem.setShaderTexture(0, resourceBoundary.getResource());
      int vOffset = this.locked ? 62 : (!this.selected && !this.isMouseOver(mouseX, mouseY) ? (this.skill.getTier() >= 1 ? 31 : 0) : -31);
      this.blit(
         matrixStack, this.x, this.y, resourceBoundary.getU(), resourceBoundary.getV() + vOffset, resourceBoundary.getWidth(), resourceBoundary.getHeight()
      );
      matrixStack.popPose();
      matrixStack.pushPose();
      matrixStack.translate(-8.0, -8.0, 0.0);
      ITextureAtlas atlas = this.getIconTextureAtlas();
      RenderSystem.setShaderTexture(0, atlas.getAtlasResourceLocation());
      GuiComponent.blit(matrixStack, this.x, this.y, 0, 16, 16, atlas.getSprite(this.style.icon));
      matrixStack.popPose();
   }

   private ITextureAtlas getIconTextureAtlas() {
      return ModTextureAtlases.SKILLS.get();
   }

   public void renderPips(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      RenderSystem.setShaderTexture(0, SKILL_WIDGET_RESOURCE);
      int rowCount = pipRowCount(this.skill.getMaxTier());
      int remainingPips = this.skill.getMaxTier();
      int remainingFilledPips = this.skill.getTier();

      for (int r = 0; r < rowCount; r++) {
         this.renderPipLine(matrixStack, this.x, this.y + 15 + 4 + r * 10, Math.min(4, remainingPips), Math.min(4, remainingFilledPips));
         remainingPips -= 4;
         remainingFilledPips -= 4;
      }
   }

   public void renderPipLine(PoseStack matrixStack, int x, int y, int count, int filledCount) {
      int lineWidth = count * 8 + (count - 1) * 2;
      int remainingFilled = filledCount;
      matrixStack.pushPose();
      matrixStack.translate(x, y, 0.0);
      matrixStack.translate(-lineWidth / 2.0F, -4.0, 0.0);

      for (int i = 0; i < count; i++) {
         if (remainingFilled > 0) {
            this.blit(matrixStack, 0, 0, 1, 133, 8, 8);
            remainingFilled--;
         } else {
            this.blit(matrixStack, 0, 0, 1, 124, 8, 8);
         }

         matrixStack.translate(10.0, 0.0, 0.0);
      }

      matrixStack.popPose();
   }
}
