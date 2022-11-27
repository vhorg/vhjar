package iskallia.vault.client.gui.screen.player.legacy.tab.split.dialog;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.component.ScrollableContainer;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.helper.FontHelper;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.gui.screen.player.TalentsElementContainerScreen;
import iskallia.vault.client.gui.screen.player.legacy.tab.split.spi.AbstractDialog;
import iskallia.vault.client.gui.screen.player.legacy.widget.TalentWidget;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.TalentLevelMessage;
import iskallia.vault.skill.talent.Talent;
import iskallia.vault.skill.talent.TalentGroup;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import java.awt.Rectangle;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;

public class TalentDialog extends AbstractDialog<TalentsElementContainerScreen> {
   private final TalentTree talentTree;
   private TalentGroup<?> talentGroup = null;
   private TalentWidget talentWidget = null;

   public TalentDialog(TalentTree talentTree, TalentsElementContainerScreen skillTreeScreen) {
      super(skillTreeScreen);
      this.talentTree = talentTree;
   }

   @Override
   public void update() {
      if (this.talentGroup != null) {
         SkillStyle abilityStyle = ModConfigs.TALENTS_GUI.getStyles().get(this.talentGroup.getParentName());
         this.talentWidget = new TalentWidget(this.talentGroup, this.talentTree, abilityStyle);
         this.talentWidget.setRenderPips(false);
         TalentNode<?> talentNode = this.talentTree.getNodeOf(this.talentGroup);
         Talent talent = talentNode.getTalent();
         int cost = talent == null ? this.talentGroup.learningCost() : this.talentGroup.cost(talentNode.getLevel() + 1);
         int regretCost = talent == null ? 0 : this.talentGroup.getTalent(talentNode.getLevel()).getRegretCost();
         String levelUpText = !talentNode.isLearned()
            ? "Learn (" + this.talentGroup.learningCost() + ")"
            : (talentNode.getLevel() >= this.talentGroup.getMaxLevel() ? "Learned" : "Upgrade (" + cost + ")");
         String regretText = !talentNode.isLearned() ? "Unlearn" : "Unlearn (" + regretCost + ")";
         this.learnButton = new Button(0, 0, 0, 0, new TextComponent(levelUpText), button -> this.upgradeAbility(), Button.NO_TOOLTIP);
         this.regretButton = new Button(0, 0, 0, 0, new TextComponent(regretText), button -> this.unlearnAbility(), Button.NO_TOOLTIP);
         this.descriptionComponent = new ScrollableContainer(this::renderDescriptions);
         boolean isLocked = ModConfigs.SKILL_GATES.getGates().isLocked(this.talentGroup, this.talentTree);
         boolean fulfillsLevelRequirement = talentNode.getLevel() >= this.talentGroup.getMaxLevel()
            || VaultBarOverlay.vaultLevel >= talentNode.getGroup().getTalent(talentNode.getLevel() + 1).getLevelRequirement();
         this.learnButton.active = cost <= VaultBarOverlay.unspentSkillPoints
            && fulfillsLevelRequirement
            && !isLocked
            && talentNode.getLevel() < this.talentGroup.getMaxLevel();
         boolean hasDependants = false;
         if (talentNode.getLevel() == 1) {
            for (TalentGroup<?> dependent : ModConfigs.SKILL_GATES.getGates().getTalentsDependingOn(this.talentGroup.getParentName())) {
               if (this.talentTree.getNodeOf(dependent).isLearned()) {
                  hasDependants = true;
                  break;
               }
            }
         }

         this.regretButton.active = regretCost <= VaultBarOverlay.unspentRegretPoints && talentNode.isLearned() && !isLocked && !hasDependants;
      }
   }

   public void setTalentGroup(TalentGroup<?> talentGroup) {
      this.talentGroup = talentGroup;
      this.update();
   }

   private void upgradeAbility() {
      TalentNode<?> talentNode = this.talentTree.getNodeOf(this.talentGroup);
      if (talentNode.getLevel() < this.talentGroup.getMaxLevel()) {
         if (VaultBarOverlay.vaultLevel >= talentNode.getGroup().getTalent(talentNode.getLevel() + 1).getLevelRequirement()) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player != null) {
               minecraft.player.playSound(talentNode.isLearned() ? ModSounds.SKILL_TREE_UPGRADE_SFX : ModSounds.SKILL_TREE_LEARN_SFX, 1.0F, 1.0F);
            }

            this.talentTree.upgradeTalent(null, talentNode);
            this.update();
            ModNetwork.CHANNEL.sendToServer(new TalentLevelMessage(this.talentGroup.getParentName(), true));
         }
      }
   }

   private void unlearnAbility() {
      TalentNode<?> talentNode = this.talentTree.getNodeOf(this.talentGroup);
      if (talentNode.isLearned()) {
         Minecraft minecraft = Minecraft.getInstance();
         if (minecraft.player != null) {
            minecraft.player.playSound(ModSounds.SKILL_TREE_UPGRADE_SFX, 1.0F, 1.0F);
         }

         this.talentTree.downgradeTalent(null, talentNode);
         this.update();
         ModNetwork.CHANNEL.sendToServer(new TalentLevelMessage(this.talentGroup.getParentName(), false));
      }
   }

   @Override
   public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      super.render(matrixStack, mouseX, mouseY, partialTicks);
      matrixStack.pushPose();
      this.renderBackground(matrixStack, mouseX, mouseY, partialTicks);
      if (this.talentGroup != null) {
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
      SkillStyle style = ModConfigs.TALENTS_GUI.getStyles().get(this.talentGroup.getParentName());
      TalentNode<?> talentNode = this.talentTree.getNodeByName(this.talentGroup.getParentName());
      Rectangle widgetBounds = this.talentWidget.getClickableBounds();
      UIHelper.renderContainerBorder(this, matrixStack, this.getHeadingBounds(), 14, 44, 2, 2, 2, 2, -7631989);
      String abilityName = talentNode.getGroup().getParentName();
      String subText = talentNode.getLevel() == 0 ? "Not Learned Yet" : "Level: " + talentNode.getLevel() + "/" + talentNode.getGroup().getMaxLevel();
      int gap = 5;
      matrixStack.pushPose();
      matrixStack.translate(10.0, 0.0, 0.0);
      FontHelper.drawStringWithBorder(
         matrixStack,
         abilityName,
         (float)(widgetBounds.width + gap),
         13.0F,
         talentNode.getLevel() == 0 ? -1 : -1849,
         talentNode.getLevel() == 0 ? -16777216 : -12897536
      );
      FontHelper.drawStringWithBorder(
         matrixStack,
         subText,
         (float)(widgetBounds.width + gap),
         23.0F,
         talentNode.getLevel() == 0 ? -1 : -1849,
         talentNode.getLevel() == 0 ? -16777216 : -12897536
      );
      matrixStack.translate(-style.x, -style.y, 0.0);
      matrixStack.translate(widgetBounds.getWidth() / 2.0, 0.0, 0.0);
      matrixStack.translate(0.0, 23.0, 0.0);
      this.talentWidget.render(matrixStack, mouseX, mouseY, partialTicks);
      matrixStack.popPose();
   }

   private void renderDescriptions(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      Rectangle renderableBounds = this.descriptionComponent.getRenderableBounds();
      TextComponent text = new TextComponent("");
      text.append(ModConfigs.SKILL_DESCRIPTIONS.getDescriptionFor(this.talentGroup.getParentName()));
      text.append("\n\n").append(this.getAdditionalDescription(this.talentGroup));
      int renderedLineCount = UIHelper.renderWrappedText(matrixStack, text, renderableBounds.width, 10);
      this.descriptionComponent.setInnerHeight(renderedLineCount * 10 + 20);
      RenderSystem.enableDepthTest();
   }

   private Component getAdditionalDescription(TalentGroup<?> talentGroup) {
      String arrow = String.valueOf('▶');
      Component costArrowTxt = new TextComponent(" " + arrow + " ").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(4737095)));
      Component lvlReqArrowTxt = new TextComponent(" " + arrow + " ").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(4737095)));
      MutableComponent txt = new TextComponent("Cost: ").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(4737095)));

      for (int lvl = 1; lvl <= talentGroup.getMaxLevel(); lvl++) {
         if (lvl > 1) {
            txt.append(costArrowTxt);
         }

         int cost = talentGroup.getTalent(lvl).getLearningCost();
         txt.append(new TextComponent(String.valueOf(cost)).withStyle(ChatFormatting.WHITE));
      }

      boolean displayRequirements = false;
      TextComponent lvlReq = new TextComponent("\n\nLevel requirement: ");

      for (int lvl = 1; lvl <= talentGroup.getMaxLevel(); lvl++) {
         if (lvl > 1) {
            lvlReq.append(lvlReqArrowTxt);
         }

         int levelRequirement = talentGroup.getTalent(lvl).getLevelRequirement();
         TextComponent lvlReqPart = new TextComponent(String.valueOf(levelRequirement));
         if (VaultBarOverlay.vaultLevel < levelRequirement) {
            lvlReqPart.withStyle(Style.EMPTY.withColor(TextColor.fromRgb(8257536)));
         } else {
            lvlReqPart.withStyle(ChatFormatting.WHITE);
         }

         lvlReq.append(lvlReqPart);
         if (levelRequirement > 0) {
            displayRequirements = true;
         }
      }

      if (displayRequirements) {
         txt.append(lvlReq);
      } else {
         txt.append(new TextComponent("\n\nNo Level requirements"));
      }

      return txt;
   }

   private void renderFooter(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      int containerX = mouseX - this.bounds.x - 5;
      int containerY = mouseY - this.bounds.y - 5;
      this.learnButton.render(matrixStack, containerX, containerY, partialTicks);
      this.regretButton.render(matrixStack, containerX, containerY, partialTicks);
   }
}
