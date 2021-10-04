package iskallia.vault.client.gui.component;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.client.gui.helper.FontHelper;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.gui.screen.SkillTreeScreen;
import iskallia.vault.client.gui.tab.SkillTab;
import iskallia.vault.client.gui.tab.TalentsTab;
import iskallia.vault.client.gui.widget.TalentWidget;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.TalentUpgradeMessage;
import iskallia.vault.skill.talent.TalentGroup;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.skill.talent.type.PlayerTalent;
import java.awt.Point;
import java.awt.Rectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

public class TalentDialog extends ComponentDialog {
   private final TalentTree talentTree;
   private TalentGroup<?> talentGroup = null;
   private TalentWidget talentWidget = null;

   public TalentDialog(TalentTree talentTree, SkillTreeScreen skillTreeScreen) {
      super(skillTreeScreen);
      this.talentTree = talentTree;
   }

   @Override
   public Point getIconUV() {
      return new Point(16, 60);
   }

   @Override
   public int getHeaderHeight() {
      return this.talentWidget.getClickableBounds().height;
   }

   @Override
   public SkillTab createTab() {
      return new TalentsTab(this, this.getSkillTreeScreen());
   }

   @Override
   public void refreshWidgets() {
      if (this.talentGroup != null) {
         SkillStyle abilityStyle = ModConfigs.TALENTS_GUI.getStyles().get(this.talentGroup.getParentName());
         this.talentWidget = new TalentWidget(this.talentGroup, this.talentTree, abilityStyle);
         this.talentWidget.setRenderPips(false);
         TalentNode<?> talentNode = this.talentTree.getNodeOf(this.talentGroup);
         String buttonText = !talentNode.isLearned()
            ? "Learn (" + this.talentGroup.learningCost() + ")"
            : (talentNode.getLevel() >= this.talentGroup.getMaxLevel() ? "Fully Learned" : "Upgrade (" + this.talentGroup.cost(talentNode.getLevel() + 1) + ")");
         this.selectButton = new Button(
            10,
            this.bounds.height - 40,
            this.bounds.width - 30,
            20,
            new StringTextComponent(buttonText),
            button -> this.upgradeAbility(),
            (button, matrixStack, x, y) -> {}
         );
         this.descriptionComponent = new ScrollableContainer(this::renderDescriptions);
         boolean isLocked = ModConfigs.SKILL_GATES.getGates().isLocked(this.talentGroup, this.talentTree);
         boolean fulfillsLevelRequirement = talentNode.getLevel() >= this.talentGroup.getMaxLevel()
            || VaultBarOverlay.vaultLevel >= talentNode.getGroup().getTalent(talentNode.getLevel() + 1).getLevelRequirement();
         PlayerTalent ability = talentNode.getTalent();
         int cost = ability == null ? this.talentGroup.learningCost() : this.talentGroup.cost(talentNode.getLevel() + 1);
         this.selectButton.field_230693_o_ = cost <= VaultBarOverlay.unspentSkillPoints
            && fulfillsLevelRequirement
            && !isLocked
            && talentNode.getLevel() < this.talentGroup.getMaxLevel();
      }
   }

   public void setTalentGroup(TalentGroup<?> talentGroup) {
      this.talentGroup = talentGroup;
      this.refreshWidgets();
   }

   public void upgradeAbility() {
      TalentNode<?> talentNode = this.talentTree.getNodeOf(this.talentGroup);
      if (talentNode.getLevel() < this.talentGroup.getMaxLevel()) {
         if (VaultBarOverlay.vaultLevel >= talentNode.getGroup().getTalent(talentNode.getLevel() + 1).getLevelRequirement()) {
            Minecraft minecraft = Minecraft.func_71410_x();
            if (minecraft.field_71439_g != null) {
               minecraft.field_71439_g.func_184185_a(talentNode.isLearned() ? ModSounds.SKILL_TREE_UPGRADE_SFX : ModSounds.SKILL_TREE_LEARN_SFX, 1.0F, 1.0F);
            }

            this.talentTree.upgradeTalent(null, talentNode);
            this.refreshWidgets();
            ModNetwork.CHANNEL.sendToServer(new TalentUpgradeMessage(this.talentGroup.getParentName()));
         }
      }
   }

   @Override
   public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      matrixStack.func_227860_a_();
      this.renderBackground(matrixStack, mouseX, mouseY, partialTicks);
      if (this.talentGroup != null) {
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
      SkillStyle abilityStyle = ModConfigs.TALENTS_GUI.getStyles().get(this.talentGroup.getParentName());
      TalentNode<?> talentNode = this.talentTree.getNodeByName(this.talentGroup.getParentName());
      Rectangle abilityBounds = this.talentWidget.getClickableBounds();
      UIHelper.renderContainerBorder(this, matrixStack, this.getHeadingBounds(), 14, 44, 2, 2, 2, 2, -7631989);
      String abilityName = talentNode.getGroup().getParentName();
      String subText = talentNode.getLevel() == 0 ? "Not Learned Yet" : "Level: " + talentNode.getLevel() + "/" + talentNode.getGroup().getMaxLevel();
      int gap = 5;
      int contentWidth = abilityBounds.width + gap + Math.max(fontRenderer.func_78256_a(abilityName), fontRenderer.func_78256_a(subText));
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(10.0, 0.0, 0.0);
      FontHelper.drawStringWithBorder(
         matrixStack,
         abilityName,
         (float)(abilityBounds.width + gap),
         13.0F,
         talentNode.getLevel() == 0 ? -1 : -1849,
         talentNode.getLevel() == 0 ? -16777216 : -12897536
      );
      FontHelper.drawStringWithBorder(
         matrixStack,
         subText,
         (float)(abilityBounds.width + gap),
         23.0F,
         talentNode.getLevel() == 0 ? -1 : -1849,
         talentNode.getLevel() == 0 ? -16777216 : -12897536
      );
      matrixStack.func_227861_a_(-abilityStyle.x, -abilityStyle.y, 0.0);
      matrixStack.func_227861_a_(abilityBounds.getWidth() / 2.0, 0.0, 0.0);
      matrixStack.func_227861_a_(0.0, 23.0, 0.0);
      this.talentWidget.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
      matrixStack.func_227865_b_();
   }

   private void renderDescriptions(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      Rectangle renderableBounds = this.descriptionComponent.getRenderableBounds();
      StringTextComponent text = new StringTextComponent("");
      text.func_230529_a_(ModConfigs.SKILL_DESCRIPTIONS.getDescriptionFor(this.talentGroup.getParentName()));
      text.func_240702_b_("\n\n").func_230529_a_(this.getAdditionalDescription(this.talentGroup));
      int renderedLineCount = UIHelper.renderWrappedText(matrixStack, text, renderableBounds.width, 10);
      this.descriptionComponent.setInnerHeight(renderedLineCount * 10 + 20);
      RenderSystem.enableDepthTest();
   }

   private ITextComponent getAdditionalDescription(TalentGroup<?> talentGroup) {
      String arrow = String.valueOf('▶');
      ITextComponent costArrowTxt = new StringTextComponent(" " + arrow + " ")
         .func_240703_c_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(4737095)));
      ITextComponent lvlReqArrowTxt = new StringTextComponent(" " + arrow + " ")
         .func_240703_c_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(4737095)));
      IFormattableTextComponent txt = new StringTextComponent("Cost: ").func_240703_c_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(4737095)));

      for (int lvl = 1; lvl <= talentGroup.getMaxLevel(); lvl++) {
         if (lvl > 1) {
            txt.func_230529_a_(costArrowTxt);
         }

         int cost = talentGroup.getTalent(lvl).getCost();
         txt.func_230529_a_(new StringTextComponent(String.valueOf(cost)).func_240699_a_(TextFormatting.WHITE));
      }

      boolean displayRequirements = false;
      StringTextComponent lvlReq = new StringTextComponent("\n\nLevel requirement: ");

      for (int lvl = 1; lvl <= talentGroup.getMaxLevel(); lvl++) {
         if (lvl > 1) {
            lvlReq.func_230529_a_(lvlReqArrowTxt);
         }

         int levelRequirement = talentGroup.getTalent(lvl).getLevelRequirement();
         StringTextComponent lvlReqPart = new StringTextComponent(String.valueOf(levelRequirement));
         if (VaultBarOverlay.vaultLevel < levelRequirement) {
            lvlReqPart.func_240703_c_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(8257536)));
         } else {
            lvlReqPart.func_240699_a_(TextFormatting.WHITE);
         }

         lvlReq.func_230529_a_(lvlReqPart);
         if (levelRequirement > 0) {
            displayRequirements = true;
         }
      }

      if (displayRequirements) {
         txt.func_230529_a_(lvlReq);
      } else {
         txt.func_230529_a_(new StringTextComponent("\n\nNo Level requirements"));
      }

      return txt;
   }

   private void renderFooter(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      int containerX = mouseX - this.bounds.x;
      int containerY = mouseY - this.bounds.y;
      this.selectButton.func_230430_a_(matrixStack, containerX, containerY, partialTicks);
      Minecraft.func_71410_x().func_110434_K().func_110577_a(SkillTreeScreen.UI_RESOURCE);
      TalentNode<?> talentNode = this.talentTree.getNodeOf(this.talentGroup);
      if (talentNode.isLearned() && talentNode.getLevel() < this.talentGroup.getMaxLevel()) {
         this.func_238474_b_(matrixStack, 13, this.bounds.height - 40 - 2, 121, 0, 15, 23);
      }
   }
}
