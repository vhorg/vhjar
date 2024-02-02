package iskallia.vault.client.gui.screen.player.legacy.tab.split.dialog;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.component.ScrollableContainer;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.helper.FontHelper;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.gui.screen.player.ResearchesElementContainerScreen;
import iskallia.vault.client.gui.screen.player.legacy.tab.split.spi.AbstractDialog;
import iskallia.vault.client.gui.screen.player.legacy.widget.ResearchWidget;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.ResearchMessage;
import iskallia.vault.network.message.VaultResearchPenaltyMessage;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.research.type.Research;
import iskallia.vault.util.PlayerReference;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

public class ResearchDialog extends AbstractDialog<ResearchesElementContainerScreen> {
   private final ResearchTree researchTree;
   private String researchName = null;
   private ResearchWidget researchWidget = null;

   public ResearchDialog(ResearchTree researchTree, ResearchesElementContainerScreen skillTreeScreen) {
      super(skillTreeScreen);
      this.researchTree = researchTree;
      this.getResearchGamerule();
   }

   @Override
   public void update() {
      if (this.researchName != null) {
         SkillStyle researchStyle = ModConfigs.RESEARCHES_GUI.getStyles().get(this.researchName);
         this.researchWidget = new ResearchWidget(this.researchName, this.researchTree, researchStyle);
         this.researchWidget.setHoverable(false);
         Research research = ModConfigs.RESEARCHES.getByName(this.researchName);
         int researchCost = this.researchTree.getResearchCost(research);
         String teamIncrease = "";
         if (!this.researchTree.getResearchShares().isEmpty() && !ResearchTree.isPenalty) {
            teamIncrease = " (+" + Math.round(this.researchTree.getTeamResearchCostIncreaseMultiplier() * 100.0F) + "%)";
         }

         String buttonText = this.researchTree.isResearched(research) ? "Researched" : "Research (" + researchCost + ")" + teamIncrease;
         this.learnButton = new Button(
            10,
            this.bounds.height - 40,
            this.bounds.width - 30,
            20,
            new TextComponent(buttonText),
            button -> this.research(),
            (btn, poseStack, mouseX, mouseY) -> {
               if (btn.active && !this.researchTree.getResearchShares().isEmpty()) {
                  List<Component> shareList = new ArrayList<>();
                  shareList.add(new TextComponent("Sharing new researches with:"));
                  this.researchTree.getResearchShares().stream().map(PlayerReference::getName).forEach(name -> shareList.add(new TextComponent("- " + name)));
                  RenderSystem.disableDepthTest();
                  this.skillTreeScreen.renderTooltip(poseStack, shareList.stream().map(Component::getVisualOrderText).toList(), mouseX, mouseY);
                  RenderSystem.enableDepthTest();
               }
            }
         );
         this.descriptionComponent = new ScrollableContainer(this::renderDescriptions);
         boolean isLocked = ModConfigs.SKILL_GATES.getGates().isLocked(this.researchName, this.researchTree);
         this.learnButton.active = !this.researchTree.isResearched(research) && !isLocked && VaultBarOverlay.unspentKnowledgePoints >= researchCost;
      }
   }

   public void getResearchGamerule() {
      ModNetwork.CHANNEL.sendToServer(new VaultResearchPenaltyMessage.C2S());
   }

   public void setResearchName(String researchName) {
      this.researchName = researchName;
      this.update();
   }

   public void research() {
      Research research = ModConfigs.RESEARCHES.getByName(this.researchName);
      int cost = this.researchTree.getResearchCost(research);
      int unspentPoints = VaultBarOverlay.unspentKnowledgePoints;
      if (cost <= unspentPoints) {
         Minecraft minecraft = Minecraft.getInstance();
         if (minecraft.player != null) {
            minecraft.player.playSound(ModSounds.SKILL_TREE_LEARN_SFX, 1.0F, 1.0F);
         }

         this.researchTree.research(research);
         this.update();
         ModNetwork.CHANNEL.sendToServer(new ResearchMessage(this.researchName));
      }
   }

   @Override
   public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      super.render(matrixStack, mouseX, mouseY, partialTicks);
      matrixStack.pushPose();
      this.renderBackground(matrixStack, mouseX, mouseY, partialTicks);
      if (this.researchName != null) {
         matrixStack.translate(this.bounds.x + 5, this.bounds.y + 5, 0.0);
         this.renderHeading(matrixStack, mouseX, mouseY, partialTicks);
         this.descriptionComponent.setBounds(this.getDescriptionsBounds());
         this.descriptionComponent.render(matrixStack, mouseX, mouseY, partialTicks);
         this.renderFooter(matrixStack, mouseX, mouseY, partialTicks);
         matrixStack.popPose();
      }
   }

   private void renderHeading(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, ScreenTextures.UI_RESOURCE);
      SkillStyle style = ModConfigs.RESEARCHES_GUI.getStyles().get(this.researchName);
      Rectangle widgetBounds = this.researchWidget.getClickableBounds();
      UIHelper.renderContainerBorder(this, matrixStack, this.getHeadingBounds(), 14, 44, 2, 2, 2, 2, -7631989);
      boolean researched = this.researchTree.getResearchesDone().contains(this.researchName);
      String subText = !researched ? "Not Researched" : "Researched";
      int gap = 5;
      matrixStack.pushPose();
      matrixStack.translate(14.0, 0.0, 0.0);
      FontHelper.drawStringWithBorder(
         matrixStack, this.researchName, (float)(widgetBounds.width + gap), 13.0F, !researched ? -1 : -1849, !researched ? -16777216 : -12897536
      );
      FontHelper.drawStringWithBorder(
         matrixStack, subText, (float)(widgetBounds.width + gap), 23.0F, !researched ? -1 : -1849, !researched ? -16777216 : -12897536
      );
      matrixStack.popPose();
      matrixStack.pushPose();
      matrixStack.translate(10.0, 0.0, 0.0);
      matrixStack.translate(-style.x, -style.y, 0.0);
      matrixStack.translate(widgetBounds.getWidth() / 2.0, 0.0, 0.0);
      matrixStack.translate(-this.researchWidget.getRenderWidth() / 2.0, -this.researchWidget.getRenderHeight() / 2.0, 0.0);
      matrixStack.translate(-0.5, 20.5, 0.0);
      this.researchWidget.render(matrixStack, mouseX, mouseY, partialTicks);
      matrixStack.popPose();
   }

   private void renderDescriptions(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      Rectangle renderableBounds = this.descriptionComponent.getRenderableBounds();
      MutableComponent description = ModConfigs.SKILL_DESCRIPTIONS.getDescriptionFor(this.researchName);
      int renderedLineCount = UIHelper.renderWrappedText(matrixStack, description, renderableBounds.width, 10);
      this.descriptionComponent.setInnerHeight(renderedLineCount * 10 + 20);
      RenderSystem.enableDepthTest();
   }

   private void renderFooter(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      int containerX = mouseX - this.bounds.x - 5;
      int containerY = mouseY - this.bounds.y - 5;
      this.learnButton.render(matrixStack, containerX, containerY, partialTicks);
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, ScreenTextures.UI_RESOURCE);
      if (this.learnButton.active) {
         this.blit(matrixStack, this.learnButton.x + 3, this.learnButton.y + 3, 150, 0, 14, 14);
      }
   }
}
