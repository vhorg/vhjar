package iskallia.vault.client.gui.screen.player.legacy.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Matrix4f;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.helper.LightmapHelper;
import iskallia.vault.client.gui.helper.ScreenDrawHelper;
import iskallia.vault.client.util.TooltipUtil;
import iskallia.vault.skill.base.GroupedSkill;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;

public class TalentGroupWidget extends AbstractWidget {
   private static final int CONTAINER_CORNER_WH = 3;
   private static final int CONTAINER_HEADER_WIDTH = 3;
   private static final int CONTAINER_HEADER_HEIGHT = 20;
   private final TalentGroupStyle style;
   private final GroupedSkill groupedSkill;

   public TalentGroupWidget(GroupedSkill groupedSkill, TalentGroupStyle style) {
      super(style.getX(), style.getY(), style.getBoxWidth(), style.getBoxHeight(), new TextComponent("the_vault.widgets.talent_group"));
      this.style = style;
      this.groupedSkill = groupedSkill;
   }

   public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      RenderSystem.setShaderTexture(0, ScreenTextures.UI_RESOURCE);
      matrixStack.pushPose();
      matrixStack.translate(this.style.getX(), this.style.getY(), 0.0);
      this.renderContainerBox(matrixStack, mouseX, mouseY, partialTicks);
      this.renderHeaderBox(matrixStack, mouseX, mouseY, partialTicks);
      matrixStack.pushPose();
      matrixStack.translate(0.0, this.style.getBoxHeight() - 20, 0.0);
      this.renderFooterBox(matrixStack, mouseX, mouseY, partialTicks);
      matrixStack.popPose();
      this.renderHeaderInformation(matrixStack, mouseX, mouseY, partialTicks);
      matrixStack.popPose();
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

   public boolean isMouseOverHeader(double pMouseX, double pMouseY) {
      return this.active && this.visible && pMouseX >= this.x && pMouseY >= this.y && pMouseX < this.x + this.width && pMouseY < this.y + 20;
   }

   private void renderHover(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      if (this.isMouseOverHeader(mouseX, mouseY)) {
         List<FormattedCharSequence> tTip = new ArrayList<>();
         tTip.add(Language.getInstance().getVisualOrder(new TextComponent("Grouped talents have a max amount of skill points")));
         tTip.add(Language.getInstance().getVisualOrder(new TextComponent("that can be spent per group")));
         TooltipUtil.renderTooltip(matrixStack, tTip, mouseX, mouseY, Integer.MAX_VALUE, Integer.MAX_VALUE);
         RenderSystem.enableBlend();
      }
   }

   private void renderHeaderInformation(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      Font fr = Minecraft.getInstance().font;
      String title = this.style.getGroup();
      FormattedCharSequence bidiTitle = Language.getInstance().getVisualOrder(new TextComponent(title));
      BufferSource renderBuf = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
      fr.drawInBatch(
         bidiTitle,
         this.getWidth() / 2.0F - fr.width(bidiTitle) / 2.0F,
         6.0F,
         this.style.getHeaderTextColor(),
         true,
         matrixStack.last().pose(),
         renderBuf,
         false,
         0,
         LightmapHelper.getPackedFullbrightCoords()
      );
      bidiTitle = Language.getInstance()
         .getVisualOrder(new TextComponent(this.groupedSkill.getSpentLearnPoints() + " / " + this.groupedSkill.getMaxSpentLearnPoints()));
      fr.drawInBatch(
         bidiTitle,
         this.getWidth() / 2.0F - fr.width(bidiTitle) / 2.0F,
         this.height - 5 - 9,
         this.style.getHeaderTextColor(),
         true,
         matrixStack.last().pose(),
         renderBuf,
         false,
         0,
         LightmapHelper.getPackedFullbrightCoords()
      );
      renderBuf.endBatch();
      RenderSystem.enableDepthTest();
      RenderSystem.enableBlend();
   }

   private void renderHeaderBox(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      int headerColor = this.style.getHeaderColor();
      ScreenDrawHelper.draw(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX, buf -> {
         ScreenDrawHelper.rect(buf, matrixStack, 3.0F, 20.0F).texVanilla(166.0F, 0.0F, 3.0F, 20.0F).color(headerColor).draw();
         ScreenDrawHelper.rect(buf, matrixStack, this.width - 6, 20.0F).at(3.0F, 0.0F).texVanilla(169.0F, 0.0F, 1.0F, 20.0F).color(headerColor).draw();
         ScreenDrawHelper.rect(buf, matrixStack, 3.0F, 20.0F).at(this.width - 3, 0.0F).texVanilla(170.0F, 0.0F, 3.0F, 20.0F).color(headerColor).draw();
      });
   }

   private void renderFooterBox(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      int headerColor = this.style.getHeaderColor();
      ScreenDrawHelper.draw(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX, buf -> {
         ScreenDrawHelper.rect(buf, matrixStack, 3.0F, 20.0F).texVanilla(166.0F, 0.0F, 3.0F, 20.0F).color(headerColor).draw();
         ScreenDrawHelper.rect(buf, matrixStack, this.width - 6, 20.0F).at(3.0F, 0.0F).texVanilla(169.0F, 0.0F, 1.0F, 20.0F).color(headerColor).draw();
         ScreenDrawHelper.rect(buf, matrixStack, 3.0F, 20.0F).at(this.width - 3, 0.0F).texVanilla(170.0F, 0.0F, 3.0F, 20.0F).color(headerColor).draw();
      });
   }

   private void renderContainerBox(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      ScreenDrawHelper.draw(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX, buf -> {
         ScreenDrawHelper.rect(buf, matrixStack, 3.0F, 3.0F).texVanilla(166.0F, 20.0F, 3.0F, 3.0F).draw();
         ScreenDrawHelper.rect(buf, matrixStack, this.width - 6, 3.0F).at(3.0F, 0.0F).texVanilla(169.0F, 20.0F, 1.0F, 3.0F).draw();
         ScreenDrawHelper.rect(buf, matrixStack, 3.0F, 3.0F).at(this.width - 3, 0.0F).texVanilla(170.0F, 20.0F, 3.0F, 3.0F).draw();
         ScreenDrawHelper.rect(buf, matrixStack, 3.0F, this.height - 6).at(0.0F, 3.0F).texVanilla(166.0F, 23.0F, 3.0F, 1.0F).draw();
         ScreenDrawHelper.rect(buf, matrixStack, 3.0F, this.height - 6).at(this.width - 3, 3.0F).texVanilla(170.0F, 23.0F, 3.0F, 1.0F).draw();
         ScreenDrawHelper.rect(buf, matrixStack, 3.0F, 3.0F).at(0.0F, this.height - 3).texVanilla(166.0F, 24.0F, 3.0F, 3.0F).draw();
         ScreenDrawHelper.rect(buf, matrixStack, this.width - 6, 3.0F).at(3.0F, this.height - 3).texVanilla(169.0F, 24.0F, 1.0F, 3.0F).draw();
         ScreenDrawHelper.rect(buf, matrixStack, 3.0F, 3.0F).at(this.width - 3, this.height - 3).texVanilla(170.0F, 24.0F, 3.0F, 3.0F).draw();
         ScreenDrawHelper.rect(buf, matrixStack, this.width - 6, this.height - 6).at(3.0F, 3.0F).texVanilla(169.0F, 23.0F, 1.0F, 1.0F).draw();
      });
   }

   public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
   }
}
