package iskallia.vault.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.client.gui.helper.LightmapHelper;
import iskallia.vault.client.gui.helper.ScreenDrawHelper;
import iskallia.vault.client.gui.helper.SkillFrame;
import iskallia.vault.client.gui.screen.SkillTreeScreen;
import iskallia.vault.config.entry.ResearchGroupStyle;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.research.group.ResearchGroup;
import iskallia.vault.util.ResourceBoundary;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.IRenderTypeBuffer.Impl;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.StringTextComponent;

public class ResearchGroupWidget extends Widget {
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
      super(style.getX(), style.getY(), getGroupWidth(style), getGroupHeight(style), new StringTextComponent("the_vault.widgets.research_group"));
      this.groupStyle = style;
      this.researchTree = researchTree;
      this.selectedResearchSupplier = selectedResearchSupplier;
   }

   private static int getGroupWidth(ResearchGroupStyle style) {
      FontRenderer fr = Minecraft.func_71410_x().field_71466_p;
      ResearchGroup group = ModConfigs.RESEARCH_GROUPS.getResearchGroupById(style.getGroup());
      int minWidth = 5;
      int iconWidth = style.getIcon() == null ? 0 : 35;
      int titleWidth = group == null ? 0 : fr.func_78256_a(group.getTitle()) + 5;
      int costWidth = fr.func_78256_a("XXXXXXXX") + 15 + 5;
      return Math.max(minWidth + iconWidth + titleWidth + costWidth, style.getBoxWidth());
   }

   private static int getGroupHeight(ResearchGroupStyle style) {
      int minHeight = 24;
      return Math.max(minHeight, style.getBoxHeight());
   }

   public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      Minecraft.func_71410_x().func_110434_K().func_110577_a(SkillTreeScreen.UI_RESOURCE);
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(this.groupStyle.getX(), this.groupStyle.getY(), 0.0);
      this.renderContainerBox(matrixStack, mouseX, mouseY, partialTicks);
      this.renderHeaderBox(matrixStack, mouseX, mouseY, partialTicks);
      this.renderHeaderIcon(matrixStack, mouseX, mouseY, partialTicks);
      this.renderHeaderInformation(matrixStack, mouseX, mouseY, partialTicks);
      matrixStack.func_227865_b_();
   }

   private void renderHeaderInformation(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      FontRenderer fr = Minecraft.func_71410_x().field_71466_p;
      int titleOffset = 5;
      if (this.groupStyle.getIcon() != null) {
         titleOffset += 35;
      }

      int costRightOffset = this.field_230688_j_ - 5;
      ResearchGroup group = ModConfigs.RESEARCH_GROUPS.getResearchGroupById(this.groupStyle.getGroup());
      if (group != null) {
         String title = group.getTitle();
         title = title == null ? "" : title;
         IReorderingProcessor bidiTitle = LanguageMap.func_74808_a().func_241870_a(new StringTextComponent(title));
         Impl renderBuf = IRenderTypeBuffer.func_228455_a_(Tessellator.func_178181_a().func_178180_c());
         fr.func_238416_a_(
            bidiTitle,
            titleOffset,
            6.0F,
            this.groupStyle.getHeaderTextColor(),
            true,
            matrixStack.func_227866_c_().func_227870_a_(),
            renderBuf,
            false,
            0,
            LightmapHelper.getPackedFullbrightCoords()
         );
         renderBuf.func_228461_a_();
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
         StringTextComponent costDisplay = new StringTextComponent(displayStr);
         if (displayAdditional) {
            String selectedStr = selectedAdditionalDisplay >= 0 ? "+" + selectedAdditionalDisplay : String.valueOf(selectedAdditionalDisplay);
            costDisplay.func_240702_b_(String.format(" (%s)", selectedStr));
         }

         IReorderingProcessor bidiCost = LanguageMap.func_74808_a().func_241870_a(costDisplay);
         costRightOffset -= fr.func_243245_a(bidiCost);
         Impl renderBuf = IRenderTypeBuffer.func_228455_a_(Tessellator.func_178181_a().func_178180_c());
         fr.func_238416_a_(
            bidiCost,
            costRightOffset,
            6.0F,
            this.groupStyle.getHeaderTextColor(),
            true,
            matrixStack.func_227866_c_().func_227870_a_(),
            renderBuf,
            false,
            0,
            LightmapHelper.getPackedFullbrightCoords()
         );
         renderBuf.func_228461_a_();
         RenderSystem.enableDepthTest();
         RenderSystem.enableBlend();
      }
   }

   private void renderHeaderIcon(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      if (this.groupStyle.getIcon() != null) {
         ResourceBoundary iconFrame = SkillFrame.RECTANGULAR.getResourceBoundary();
         Minecraft.func_71410_x().func_110434_K().func_110577_a(iconFrame.getResource());
         float iconFrameX = 5.0F;
         float iconFrameY = -5.0F;
         ScreenDrawHelper.draw(
            7,
            DefaultVertexFormats.field_227851_o_,
            buf -> ScreenDrawHelper.rect(buf, matrixStack, iconFrameX, iconFrameY, 0.0F, 30.0F, 30.0F)
               .texVanilla(iconFrame.getU(), iconFrame.getV(), iconFrame.getW(), iconFrame.getH())
               .draw()
         );
         ResearchGroupStyle.Icon icon = this.groupStyle.getIcon();
         Minecraft.func_71410_x().func_110434_K().func_110577_a(ResearchWidget.RESEARCHES_RESOURCE);
         float iconX = 12.0F;
         float iconY = 2.0F;
         ScreenDrawHelper.draw(
            7,
            DefaultVertexFormats.field_227851_o_,
            buf -> ScreenDrawHelper.rect(buf, matrixStack, iconX, iconY, 0.0F, 16.0F, 16.0F).texVanilla(icon.getU(), icon.getV(), 16.0F, 16.0F).draw()
         );
      }
   }

   private void renderHeaderBox(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      int headerColor = this.groupStyle.getHeaderColor();
      ScreenDrawHelper.draw(
         7,
         DefaultVertexFormats.field_227851_o_,
         buf -> {
            ScreenDrawHelper.rect(buf, matrixStack, 3.0F, 20.0F).texVanilla(166.0F, 0.0F, 3.0F, 20.0F).color(headerColor).draw();
            ScreenDrawHelper.rect(buf, matrixStack, this.field_230688_j_ - 6, 20.0F)
               .at(3.0F, 0.0F)
               .texVanilla(169.0F, 0.0F, 1.0F, 20.0F)
               .color(headerColor)
               .draw();
            ScreenDrawHelper.rect(buf, matrixStack, 3.0F, 20.0F)
               .at(this.field_230688_j_ - 3, 0.0F)
               .texVanilla(170.0F, 0.0F, 3.0F, 20.0F)
               .color(headerColor)
               .draw();
         }
      );
   }

   private void renderContainerBox(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      ScreenDrawHelper.draw(
         7,
         DefaultVertexFormats.field_227851_o_,
         buf -> {
            ScreenDrawHelper.rect(buf, matrixStack, 3.0F, 3.0F).texVanilla(166.0F, 20.0F, 3.0F, 3.0F).draw();
            ScreenDrawHelper.rect(buf, matrixStack, this.field_230688_j_ - 6, 3.0F).at(3.0F, 0.0F).texVanilla(169.0F, 20.0F, 1.0F, 3.0F).draw();
            ScreenDrawHelper.rect(buf, matrixStack, 3.0F, 3.0F).at(this.field_230688_j_ - 3, 0.0F).texVanilla(170.0F, 20.0F, 3.0F, 3.0F).draw();
            ScreenDrawHelper.rect(buf, matrixStack, 3.0F, this.field_230689_k_ - 6).at(0.0F, 3.0F).texVanilla(166.0F, 23.0F, 3.0F, 1.0F).draw();
            ScreenDrawHelper.rect(buf, matrixStack, 3.0F, this.field_230689_k_ - 6)
               .at(this.field_230688_j_ - 3, 3.0F)
               .texVanilla(170.0F, 23.0F, 3.0F, 1.0F)
               .draw();
            ScreenDrawHelper.rect(buf, matrixStack, 3.0F, 3.0F).at(0.0F, this.field_230689_k_ - 3).texVanilla(166.0F, 24.0F, 3.0F, 3.0F).draw();
            ScreenDrawHelper.rect(buf, matrixStack, this.field_230688_j_ - 6, 3.0F)
               .at(3.0F, this.field_230689_k_ - 3)
               .texVanilla(169.0F, 24.0F, 1.0F, 3.0F)
               .draw();
            ScreenDrawHelper.rect(buf, matrixStack, 3.0F, 3.0F)
               .at(this.field_230688_j_ - 3, this.field_230689_k_ - 3)
               .texVanilla(170.0F, 24.0F, 3.0F, 3.0F)
               .draw();
            ScreenDrawHelper.rect(buf, matrixStack, this.field_230688_j_ - 6, this.field_230689_k_ - 6)
               .at(3.0F, 3.0F)
               .texVanilla(169.0F, 23.0F, 1.0F, 1.0F)
               .draw();
         }
      );
   }
}
