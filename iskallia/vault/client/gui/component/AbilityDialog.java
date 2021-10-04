package iskallia.vault.client.gui.component;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.client.gui.helper.FontHelper;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.gui.screen.SkillTreeScreen;
import iskallia.vault.client.gui.tab.AbilitiesTab;
import iskallia.vault.client.gui.tab.SkillTab;
import iskallia.vault.client.gui.widget.AbilityWidget;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.AbilitySelectSpecializationMessage;
import iskallia.vault.network.message.AbilityUpgradeMessage;
import iskallia.vault.skill.ability.AbilityGroup;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityRegistry;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.config.AbilityConfig;
import java.awt.Point;
import java.awt.Rectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.Button.IPressable;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

public class AbilityDialog extends ComponentDialog {
   private final AbilityTree abilityTree;
   private String selectedAbility = null;
   private AbilityWidget selectedAbilityWidget = null;

   public AbilityDialog(AbilityTree abilityTree, SkillTreeScreen skillTreeScreen) {
      super(skillTreeScreen);
      this.abilityTree = abilityTree;
   }

   @Override
   public Point getIconUV() {
      return new Point(32, 60);
   }

   @Override
   public int getHeaderHeight() {
      return this.selectedAbilityWidget.getClickableBounds().height;
   }

   @Override
   public SkillTab createTab() {
      return new AbilitiesTab(this, this.getSkillTreeScreen());
   }

   @Override
   public void refreshWidgets() {
      if (this.selectedAbility != null) {
         SkillStyle abilityStyle = ModConfigs.ABILITIES_GUI.getStyles().get(this.selectedAbility);
         this.selectedAbilityWidget = new AbilityWidget(this.selectedAbility, this.abilityTree, abilityStyle);
         this.selectedAbilityWidget.setHoverable(false);
         this.selectedAbilityWidget.setRenderPips(false);
         AbilityNode<?, ?> existingNode = this.abilityTree.getNodeOf(AbilityRegistry.getAbility(this.selectedAbility));
         AbilityNode<?, ?> targetAbilityNode = this.selectedAbilityWidget.makeAbilityNode();
         AbilityGroup<?, ?> targetAbilityGroup = targetAbilityNode.getGroup();
         IPressable pressAction;
         String buttonText;
         boolean activeState;
         if (targetAbilityNode.getSpecialization() != null) {
            buttonText = "Select Specialization";
            pressAction = button -> this.selectSpecialization();
            activeState = existingNode.getSpecialization() == null
               && existingNode.isLearned()
               && VaultBarOverlay.vaultLevel >= targetAbilityNode.getAbilityConfig().getLevelRequirement();
         } else {
            if (!targetAbilityNode.isLearned()) {
               buttonText = "Learn (" + targetAbilityGroup.learningCost() + ")";
            } else {
               buttonText = existingNode.getLevel() >= targetAbilityGroup.getMaxLevel()
                  ? "Fully Learned"
                  : "Upgrade (" + targetAbilityGroup.levelUpCost(targetAbilityNode.getSpecialization(), targetAbilityNode.getLevel()) + ")";
            }

            pressAction = button -> this.upgradeAbility();
            AbilityConfig ability = targetAbilityNode.getAbilityConfig();
            int cost = ability == null
               ? targetAbilityGroup.learningCost()
               : targetAbilityGroup.levelUpCost(targetAbilityNode.getSpecialization(), targetAbilityNode.getLevel());
            activeState = cost <= VaultBarOverlay.unspentSkillPoints
               && existingNode.getLevel() < targetAbilityGroup.getMaxLevel()
               && targetAbilityNode.getLevel() < targetAbilityGroup.getMaxLevel() + 1
               && VaultBarOverlay.vaultLevel >= targetAbilityNode.getAbilityConfig().getLevelRequirement();
         }

         this.descriptionComponent = new ScrollableContainer(this::renderDescriptions);
         this.selectButton = new Button(
            10, this.bounds.height - 40, this.bounds.width - 30, 20, new StringTextComponent(buttonText), pressAction, (button, matrixStack, x, y) -> {}
         );
         this.selectButton.field_230693_o_ = activeState;
      }
   }

   public void setAbilityWidget(String abilityName) {
      this.selectedAbility = abilityName;
      this.refreshWidgets();
   }

   private void upgradeAbility() {
      AbilityNode<?, ?> abilityNode = this.abilityTree.getNodeByName(AbilityRegistry.getAbility(this.selectedAbility).getAbilityGroupName());
      if (abilityNode.getLevel() < abilityNode.getGroup().getMaxLevel()) {
         Minecraft.func_71410_x()
            .field_71439_g
            .func_184185_a(abilityNode.isLearned() ? ModSounds.SKILL_TREE_UPGRADE_SFX : ModSounds.SKILL_TREE_LEARN_SFX, 1.0F, 1.0F);
         this.abilityTree.upgradeAbility(null, abilityNode);
         this.refreshWidgets();
         ModNetwork.CHANNEL.sendToServer(new AbilityUpgradeMessage(abilityNode.getGroup().getParentName()));
      }
   }

   private void selectSpecialization() {
      AbilityNode<?, ?> targetNode = this.selectedAbilityWidget.makeAbilityNode();
      AbilityNode<?, ?> existingNode = this.abilityTree.getNodeByName(AbilityRegistry.getAbility(this.selectedAbility).getAbilityGroupName());
      String toSelect = targetNode.getSpecialization();
      String abilityName = existingNode.getGroup().getParentName();
      if (existingNode.getSpecialization() == null || !targetNode.getGroup().equals(existingNode.getGroup())) {
         if (VaultBarOverlay.vaultLevel >= targetNode.getAbilityConfig().getLevelRequirement()) {
            Minecraft.func_71410_x().field_71439_g.func_184185_a(ModSounds.SKILL_TREE_UPGRADE_SFX, 1.0F, 1.0F);
            this.abilityTree.selectSpecialization(abilityName, toSelect);
            this.getSkillTreeScreen().refreshWidgets();
            ModNetwork.CHANNEL.sendToServer(new AbilitySelectSpecializationMessage(abilityName, toSelect));
         }
      }
   }

   @Override
   public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      this.renderBackground(matrixStack, mouseX, mouseY, partialTicks);
      if (this.selectedAbility != null && this.selectedAbilityWidget != null) {
         matrixStack.func_227860_a_();
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
      SkillStyle abilityStyle = ModConfigs.ABILITIES_GUI.getStyles().get(this.selectedAbility);
      AbilityNode<?, ?> targetAbilityNode = this.selectedAbilityWidget.makeAbilityNode();
      AbilityNode<?, ?> currentAbilityNode = this.abilityTree.getNodeOf(targetAbilityNode.getGroup());
      boolean learned;
      String abilityName;
      String subText;
      if (targetAbilityNode.getSpecialization() != null) {
         learned = targetAbilityNode.getSpecialization().equals(currentAbilityNode.getSpecialization());
         abilityName = targetAbilityNode.getGroup().getParentName() + ": " + targetAbilityNode.getSpecializationName();
         subText = learned ? "Selected" : "Not selected";
      } else {
         learned = targetAbilityNode.isLearned();
         abilityName = targetAbilityNode.getGroup().getParentName();
         if (!learned) {
            subText = "Not learned yet";
         } else {
            subText = "Level: " + currentAbilityNode.getLevel() + "/" + targetAbilityNode.getGroup().getMaxLevel();
         }
      }

      Rectangle abilityBounds = this.selectedAbilityWidget.getClickableBounds();
      UIHelper.renderContainerBorder(this, matrixStack, this.getHeadingBounds(), 14, 44, 2, 2, 2, 2, -7631989);
      int gap = 5;
      int contentWidth = abilityBounds.width + gap + Math.max(fontRenderer.func_78256_a(abilityName), fontRenderer.func_78256_a(subText));
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(10.0, 0.0, 0.0);
      FontHelper.drawStringWithBorder(
         matrixStack, abilityName, (float)(abilityBounds.width + gap), 13.0F, !learned ? -1 : -1849, !learned ? -16777216 : -12897536
      );
      FontHelper.drawStringWithBorder(matrixStack, subText, (float)(abilityBounds.width + gap), 23.0F, !learned ? -1 : -1849, !learned ? -16777216 : -12897536);
      matrixStack.func_227861_a_(-abilityStyle.x, -abilityStyle.y, 0.0);
      matrixStack.func_227861_a_(abilityBounds.getWidth() / 2.0, 0.0, 0.0);
      matrixStack.func_227861_a_(0.0, 23.0, 0.0);
      this.selectedAbilityWidget.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
      matrixStack.func_227865_b_();
   }

   private void renderDescriptions(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      AbilityNode<?, ?> targetAbilityNode = this.selectedAbilityWidget.makeAbilityNode();
      Rectangle renderableBounds = this.descriptionComponent.getRenderableBounds();
      StringTextComponent text = new StringTextComponent("");
      text.func_230529_a_(ModConfigs.SKILL_DESCRIPTIONS.getDescriptionFor(this.selectedAbilityWidget.getAbilityName()));
      if (targetAbilityNode.getSpecialization() == null) {
         text.func_240702_b_("\n\n").func_230529_a_(this.getAdditionalDescription(targetAbilityNode));
      }

      int renderedLineCount = UIHelper.renderWrappedText(matrixStack, text, renderableBounds.width, 10);
      this.descriptionComponent.setInnerHeight(renderedLineCount * 10 + 20);
      RenderSystem.enableDepthTest();
   }

   private ITextComponent getAdditionalDescription(AbilityNode<?, ?> abilityNode) {
      String arrow = String.valueOf('▶');
      ITextComponent arrowTxt = new StringTextComponent(" " + arrow + " ").func_240703_c_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(4737095)));
      IFormattableTextComponent txt = new StringTextComponent("Cost: ").func_240703_c_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(4737095)));

      for (int lvl = 1; lvl <= abilityNode.getGroup().getMaxLevel(); lvl++) {
         if (lvl > 1) {
            txt.func_230529_a_(arrowTxt);
         }

         int cost = abilityNode.getGroup().levelUpCost(null, lvl);
         txt.func_230529_a_(new StringTextComponent(String.valueOf(cost)).func_240699_a_(TextFormatting.WHITE));
      }

      boolean displayRequirements = false;
      IFormattableTextComponent lvlReq = new StringTextComponent("\n\nLevel requirement: ")
         .func_240703_c_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(4737095)));

      for (int lvl = 0; lvl < abilityNode.getGroup().getMaxLevel(); lvl++) {
         if (lvl > 0) {
            lvlReq.func_230529_a_(arrowTxt);
         }

         int levelRequirement = abilityNode.getGroup().getAbilityConfig(null, lvl).getLevelRequirement();
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
      AbilityNode<?, ?> targetAbilityNode = this.selectedAbilityWidget.makeAbilityNode();
      AbilityNode<?, ?> existingNode = this.abilityTree.getNodeOf(targetAbilityNode.getGroup());
      if (targetAbilityNode.getSpecialization() == null
         && targetAbilityNode.isLearned()
         && existingNode.getLevel() < targetAbilityNode.getGroup().getMaxLevel()
         && targetAbilityNode.getLevel() < targetAbilityNode.getGroup().getMaxLevel() + 1) {
         this.func_238474_b_(matrixStack, 13, this.bounds.height - 40 - 2, 121, 0, 15, 23);
      }
   }
}
