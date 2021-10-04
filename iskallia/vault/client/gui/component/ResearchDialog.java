package iskallia.vault.client.gui.component;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.client.gui.helper.FontHelper;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.gui.screen.SkillTreeScreen;
import iskallia.vault.client.gui.tab.ResearchesTab;
import iskallia.vault.client.gui.tab.SkillTab;
import iskallia.vault.client.gui.widget.ResearchWidget;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.ResearchMessage;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.research.type.Research;
import java.awt.Point;
import java.awt.Rectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;

public class ResearchDialog extends ComponentDialog {
   private final ResearchTree researchTree;
   private String researchName = null;
   private ResearchWidget researchWidget = null;

   public ResearchDialog(ResearchTree researchTree, SkillTreeScreen skillTreeScreen) {
      super(skillTreeScreen);
      this.researchTree = researchTree;
   }

   @Override
   public Point getIconUV() {
      return new Point(0, 60);
   }

   @Override
   public int getHeaderHeight() {
      return this.researchWidget.getClickableBounds().height;
   }

   @Override
   public SkillTab createTab() {
      return new ResearchesTab(this, this.getSkillTreeScreen());
   }

   @Override
   public void refreshWidgets() {
      if (this.researchName != null) {
         SkillStyle researchStyle = ModConfigs.RESEARCHES_GUI.getStyles().get(this.researchName);
         this.researchWidget = new ResearchWidget(this.researchName, this.researchTree, researchStyle);
         this.researchWidget.setHoverable(false);
         Research research = ModConfigs.RESEARCHES.getByName(this.researchName);
         int researchCost = this.researchTree.getResearchCost(research);
         String buttonText = this.researchTree.isResearched(this.researchName) ? "Researched" : "Research (" + researchCost + ")";
         this.selectButton = new Button(
            10,
            this.bounds.height - 40,
            this.bounds.width - 30,
            20,
            new StringTextComponent(buttonText),
            button -> this.research(),
            (button, matrixStack, x, y) -> {}
         );
         this.descriptionComponent = new ScrollableContainer(this::renderDescriptions);
         boolean isLocked = ModConfigs.SKILL_GATES.getGates().isLocked(this.researchName, this.researchTree);
         this.selectButton.field_230693_o_ = !this.researchTree.isResearched(this.researchName)
            && !isLocked
            && (research.usesKnowledge() ? VaultBarOverlay.unspentKnowledgePoints >= researchCost : VaultBarOverlay.unspentSkillPoints >= researchCost);
      }
   }

   public void setResearchName(String researchName) {
      this.researchName = researchName;
      this.refreshWidgets();
   }

   public void research() {
      Research research = ModConfigs.RESEARCHES.getByName(this.researchName);
      int cost = this.researchTree.getResearchCost(research);
      int unspentPoints = research.usesKnowledge() ? VaultBarOverlay.unspentKnowledgePoints : VaultBarOverlay.unspentSkillPoints;
      if (cost <= unspentPoints) {
         Minecraft minecraft = Minecraft.func_71410_x();
         if (minecraft.field_71439_g != null) {
            minecraft.field_71439_g.func_184185_a(ModSounds.SKILL_TREE_LEARN_SFX, 1.0F, 1.0F);
         }

         this.researchTree.research(this.researchName);
         this.refreshWidgets();
         ModNetwork.CHANNEL.sendToServer(new ResearchMessage(this.researchName));
      }
   }

   @Override
   public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      matrixStack.func_227860_a_();
      this.renderBackground(matrixStack, mouseX, mouseY, partialTicks);
      if (this.researchName != null) {
         matrixStack.func_227861_a_(this.bounds.x + 5, this.bounds.y + 5, 0.0);
         this.renderHeading(matrixStack, mouseX, mouseY, partialTicks);
         this.descriptionComponent.setBounds(this.getDescriptionsBounds());
         this.descriptionComponent.render(matrixStack, mouseX, mouseY, partialTicks);
         this.renderFooter(matrixStack, mouseX, mouseY, partialTicks);
         matrixStack.func_227865_b_();
      }
   }

   private void renderHeading(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      Minecraft.func_71410_x().func_110434_K().func_110577_a(SkillTreeScreen.UI_RESOURCE);
      FontRenderer fontRenderer = Minecraft.func_71410_x().field_71466_p;
      SkillStyle abilityStyle = ModConfigs.RESEARCHES_GUI.getStyles().get(this.researchName);
      Rectangle abilityBounds = this.researchWidget.getClickableBounds();
      UIHelper.renderContainerBorder(this, matrixStack, this.getHeadingBounds(), 14, 44, 2, 2, 2, 2, -7631989);
      boolean researched = this.researchTree.getResearchesDone().contains(this.researchName);
      String subText = !researched ? "Not Researched" : "Researched";
      int gap = 5;
      int contentWidth = abilityBounds.width + gap + Math.max(fontRenderer.func_78256_a(this.researchName), fontRenderer.func_78256_a(subText));
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(10.0, 0.0, 0.0);
      FontHelper.drawStringWithBorder(
         matrixStack, this.researchName, (float)(abilityBounds.width + gap), 13.0F, !researched ? -1 : -1849, !researched ? -16777216 : -12897536
      );
      FontHelper.drawStringWithBorder(
         matrixStack, subText, (float)(abilityBounds.width + gap), 23.0F, !researched ? -1 : -1849, !researched ? -16777216 : -12897536
      );
      matrixStack.func_227861_a_(-abilityStyle.x, -abilityStyle.y, 0.0);
      matrixStack.func_227861_a_(abilityBounds.getWidth() / 2.0, 0.0, 0.0);
      matrixStack.func_227861_a_(-this.researchWidget.getRenderWidth() / 2.0, -this.researchWidget.getRenderHeight() / 2.0, 0.0);
      matrixStack.func_227861_a_(-3.0, 20.0, 0.0);
      this.researchWidget.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
      matrixStack.func_227865_b_();
   }

   private void renderDescriptions(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      Rectangle renderableBounds = this.descriptionComponent.getRenderableBounds();
      IFormattableTextComponent description = ModConfigs.SKILL_DESCRIPTIONS.getDescriptionFor(this.researchName);
      int renderedLineCount = UIHelper.renderWrappedText(matrixStack, description, renderableBounds.width, 10);
      this.descriptionComponent.setInnerHeight(renderedLineCount * 10 + 20);
      RenderSystem.enableDepthTest();
   }

   private void renderFooter(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      int containerX = mouseX - this.bounds.x;
      int containerY = mouseY - this.bounds.y;
      this.selectButton.func_230430_a_(matrixStack, containerX, containerY, partialTicks);
      Minecraft.func_71410_x().func_110434_K().func_110577_a(SkillTreeScreen.UI_RESOURCE);
      Research research = ModConfigs.RESEARCHES.getByName(this.researchName);
      boolean researched = this.researchTree.getResearchesDone().contains(this.researchName);
      if (!researched) {
         this.func_238474_b_(matrixStack, 13, this.bounds.height - 40 - 2, 121 + (research.usesKnowledge() ? 15 : 30), 0, 15, 23);
      }
   }
}
