package iskallia.vault.client.gui.screen.player.legacy.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import iskallia.vault.client.atlas.ITextureAtlas;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.helper.LightmapHelper;
import iskallia.vault.client.gui.helper.ScreenDrawHelper;
import iskallia.vault.client.gui.helper.SkillFrame;
import iskallia.vault.config.entry.ResearchGroupStyle;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModTextureAtlases;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.research.group.ResearchGroup;
import iskallia.vault.util.ResourceBoundary;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;

public class ResearchGroupWidget extends AbstractWidget {
   private static final int CONTENT_SPACER = 5;
   private static final int CONTAINER_CORNER_WH = 3;
   private static final int CONTAINER_HEADER_WIDTH = 3;
   private static final int CONTAINER_HEADER_HEIGHT = 20;
   private static final int CONTAINER_HEADER_ICON_FRAME_WH = 30;
   private static final int CONTAINER_HEADER_ICON_WH = 16;
   private final ResearchGroupStyle groupStyle;
   private final ResearchTree researchTree;
   private final Supplier<ResearchWidget> selectedResearchSupplier;

   public ResearchGroupWidget(ResearchGroupStyle style, ResearchTree researchTree, Supplier<ResearchWidget> selectedResearchSupplier) {
      super(style.getX(), style.getY(), getGroupWidth(style), getGroupHeight(style), new TextComponent("the_vault.widgets.research_group"));
      this.groupStyle = style;
      this.researchTree = researchTree;
      this.selectedResearchSupplier = selectedResearchSupplier;
   }

   private static int getGroupWidth(ResearchGroupStyle style) {
      Font fr = Minecraft.getInstance().font;
      ResearchGroup group = ModConfigs.RESEARCH_GROUPS.getResearchGroupById(style.getGroup());
      int minWidth = 5;
      int iconWidth = style.getIcon() == null ? 0 : 35;
      int titleWidth = group == null ? 0 : fr.width(group.getTitle()) + 5;
      int costWidth = fr.width("XXXXXXXX") + 15 + 5;
      return Math.max(minWidth + iconWidth + titleWidth + costWidth, style.getBoxWidth());
   }

   private static int getGroupHeight(ResearchGroupStyle style) {
      int minHeight = 24;
      return Math.max(minHeight, style.getBoxHeight());
   }

   public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      RenderSystem.setShaderTexture(0, ScreenTextures.UI_RESOURCE);
      matrixStack.pushPose();
      matrixStack.translate(this.groupStyle.getX(), this.groupStyle.getY(), 0.0);
      this.renderContainerBox(matrixStack, mouseX, mouseY, partialTicks);
      this.renderHeaderBox(matrixStack, mouseX, mouseY, partialTicks);
      this.renderHeaderIcon(matrixStack, mouseX, mouseY, partialTicks);
      this.renderHeaderInformation(matrixStack, mouseX, mouseY, partialTicks);
      matrixStack.popPose();
   }

   private void renderHeaderInformation(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      Font fr = Minecraft.getInstance().font;
      int titleOffset = 5;
      if (this.groupStyle.getIcon() != null) {
         titleOffset += 35;
      }

      int costRightOffset = this.width - 5;
      ResearchGroup group = ModConfigs.RESEARCH_GROUPS.getResearchGroupById(this.groupStyle.getGroup());
      if (group != null) {
         String title = group.getTitle();
         title = title == null ? "" : title;
         FormattedCharSequence bidiTitle = Language.getInstance().getVisualOrder(new TextComponent(title));
         BufferSource renderBuf = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
         fr.drawInBatch(
            bidiTitle,
            titleOffset,
            6.0F,
            this.groupStyle.getHeaderTextColor(),
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

      float currentAdditional = 0.0F;

      for (String research : this.researchTree.getResearchesDone()) {
         ResearchGroup resGroup = ModConfigs.RESEARCH_GROUPS.getResearchGroup(research);
         if (resGroup != null) {
            currentAdditional += resGroup.getGroupIncreasedResearchCost(this.groupStyle.getGroup());
         }
      }

      int currentAdditionalDisplay = Math.round(currentAdditional);
      boolean displayAdditional = false;
      float selectedAdditional = 0.0F;
      ResearchWidget selectedWidget = this.selectedResearchSupplier.get();
      if (selectedWidget != null) {
         String selectedResearch = selectedWidget.getResearchName();
         if (selectedResearch != null && !this.researchTree.isResearched(selectedResearch)) {
            displayAdditional = currentAdditionalDisplay != 0;
            ResearchGroup selectedGroup = ModConfigs.RESEARCH_GROUPS.getResearchGroup(selectedResearch);
            if (selectedGroup != null) {
               selectedAdditional += selectedGroup.getGroupIncreasedResearchCost(this.groupStyle.getGroup());
               displayAdditional |= selectedAdditional != 0.0F;
            }
         }
      }

      int selectedAdditionalDisplay = Math.round(currentAdditional + selectedAdditional) - currentAdditionalDisplay;
      if (currentAdditionalDisplay != 0 || displayAdditional) {
         String displayStr = currentAdditionalDisplay >= 0 ? "+" + currentAdditionalDisplay : String.valueOf(currentAdditionalDisplay);
         TextComponent costDisplay = new TextComponent(displayStr);
         if (displayAdditional) {
            String selectedStr = selectedAdditionalDisplay >= 0 ? "+" + selectedAdditionalDisplay : String.valueOf(selectedAdditionalDisplay);
            costDisplay.append(String.format(" (%s)", selectedStr));
         }

         FormattedCharSequence bidiCost = Language.getInstance().getVisualOrder(costDisplay);
         costRightOffset -= fr.width(bidiCost);
         BufferSource renderBuf = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
         fr.drawInBatch(
            bidiCost,
            costRightOffset,
            6.0F,
            this.groupStyle.getHeaderTextColor(),
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
   }

   private void renderHeaderIcon(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      if (this.groupStyle.getIcon() != null) {
         ResourceBoundary iconFrame = SkillFrame.RECTANGULAR.getResourceBoundary();
         RenderSystem.setShaderTexture(0, iconFrame.getResource());
         float iconFrameX = 5.0F;
         float iconFrameY = -5.0F;
         ScreenDrawHelper.draw(
            Mode.QUADS,
            DefaultVertexFormat.POSITION_COLOR_TEX,
            buf -> ScreenDrawHelper.rect(buf, matrixStack, iconFrameX, iconFrameY, 0.0F, 30.0F, 30.0F)
               .texVanilla(iconFrame.getU(), iconFrame.getV(), iconFrame.getWidth(), iconFrame.getHeight())
               .draw()
         );
         ITextureAtlas atlas = ModTextureAtlases.RESEARCH_GROUPS.get();
         TextureAtlasSprite sprite = atlas.getSprite(this.groupStyle.getIcon());
         RenderSystem.setShaderTexture(0, atlas.getAtlasResourceLocation());
         float iconX = 12.0F;
         float iconY = 2.0F;
         ScreenDrawHelper.draw(
            Mode.QUADS,
            DefaultVertexFormat.POSITION_COLOR_TEX,
            buf -> ScreenDrawHelper.rect(buf, matrixStack, iconX, iconY, 0.0F, 16.0F, 16.0F).tex(sprite).draw()
         );
      }
   }

   private void renderHeaderBox(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      int headerColor = this.groupStyle.getHeaderColor();
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
