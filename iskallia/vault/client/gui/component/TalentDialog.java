package iskallia.vault.client.gui.component;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.client.gui.helper.FontHelper;
import iskallia.vault.client.gui.helper.Rectangle;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.gui.screen.SkillTreeScreen;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;

public class TalentDialog extends AbstractGui {
   private Rectangle bounds;
   private TalentGroup<?> talentGroup = null;
   private TalentTree talentTree;
   private TalentWidget abilityWidget;
   private ScrollableContainer descriptionComponent;
   private Button abilityUpgradeButton;

   public TalentDialog(TalentTree talentTree) {
      this.talentTree = talentTree;
      this.refreshWidgets();
   }

   public void refreshWidgets() {
      if (this.talentGroup != null) {
         SkillStyle abilityStyle = ModConfigs.TALENTS_GUI.getStyles().get(this.talentGroup.getParentName());
         this.abilityWidget = new TalentWidget(this.talentGroup, this.talentTree, abilityStyle);
         TalentNode<?> talentNode = this.talentTree.getNodeOf(this.talentGroup);
         String buttonText = !talentNode.isLearned()
            ? "Learn (" + this.talentGroup.learningCost() + ")"
            : (talentNode.getLevel() >= this.talentGroup.getMaxLevel() ? "Fully Learned" : "Upgrade (" + this.talentGroup.cost(talentNode.getLevel() + 1) + ")");
         this.abilityUpgradeButton = new Button(
            10,
            this.bounds.getHeight() - 40,
            this.bounds.getWidth() - 30,
            20,
            new StringTextComponent(buttonText),
            button -> this.upgradeAbility(),
            (button, matrixStack, x, y) -> {}
         );
         this.descriptionComponent = new ScrollableContainer(this::renderDescriptions);
         PlayerTalent ability = talentNode.getTalent();
         int cost = ability == null ? this.talentGroup.learningCost() : this.talentGroup.cost(talentNode.getLevel() + 1);
         this.abilityUpgradeButton.field_230693_o_ = cost <= VaultBarOverlay.unspentSkillPoints && talentNode.getLevel() < this.talentGroup.getMaxLevel();
      }
   }

   public void setTalentGroup(TalentGroup<?> talentGroup) {
      this.talentGroup = talentGroup;
      this.refreshWidgets();
   }

   public TalentDialog setBounds(Rectangle bounds) {
      this.bounds = bounds;
      return this;
   }

   public Rectangle getHeadingBounds() {
      Rectangle abilityBounds = this.abilityWidget.getClickableBounds();
      Rectangle headingBounds = new Rectangle();
      headingBounds.x0 = 5;
      headingBounds.y0 = 5;
      headingBounds.x1 = headingBounds.x0 + this.bounds.getWidth() - 20;
      headingBounds.y1 = headingBounds.y0 + abilityBounds.getHeight() + 5;
      return headingBounds;
   }

   public Rectangle getDescriptionsBounds() {
      Rectangle headingBounds = this.getHeadingBounds();
      Rectangle descriptionsBounds = new Rectangle();
      descriptionsBounds.x0 = headingBounds.x0;
      descriptionsBounds.y0 = headingBounds.y1 + 10;
      descriptionsBounds.x1 = headingBounds.x1;
      descriptionsBounds.y1 = this.bounds.getHeight() - 50;
      return descriptionsBounds;
   }

   public void mouseMoved(int screenX, int screenY) {
      if (this.bounds != null) {
         int containerX = screenX - this.bounds.x0;
         int containerY = screenY - this.bounds.y0;
         if (this.abilityUpgradeButton != null) {
            this.abilityUpgradeButton.func_212927_b(containerX, containerY);
         }
      }
   }

   public void mouseClicked(int screenX, int screenY, int button) {
      int containerX = screenX - this.bounds.x0;
      int containerY = screenY - this.bounds.y0;
      if (this.abilityUpgradeButton != null) {
         this.abilityUpgradeButton.func_231044_a_(containerX, containerY, button);
      }
   }

   public void mouseScrolled(double mouseX, double mouseY, double delta) {
      if (this.bounds.contains((int)mouseX, (int)mouseY)) {
         this.descriptionComponent.mouseScrolled(mouseX, mouseY, delta);
      }
   }

   public void upgradeAbility() {
      TalentNode<?> talentNode = this.talentTree.getNodeOf(this.talentGroup);
      if (talentNode.getLevel() < this.talentGroup.getMaxLevel()) {
         Minecraft minecraft = Minecraft.func_71410_x();
         if (minecraft.field_71439_g != null) {
            minecraft.field_71439_g.func_184185_a(talentNode.isLearned() ? ModSounds.SKILL_TREE_UPGRADE_SFX : ModSounds.SKILL_TREE_LEARN_SFX, 1.0F, 1.0F);
         }

         this.talentTree.upgradeTalent(null, talentNode);
         this.refreshWidgets();
         ModNetwork.CHANNEL.sendToServer(new TalentUpgradeMessage(this.talentGroup.getParentName()));
      }
   }

   public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      matrixStack.func_227860_a_();
      this.renderBackground(matrixStack, mouseX, mouseY, partialTicks);
      if (this.talentGroup != null) {
         matrixStack.func_227861_a_(this.bounds.x0 + 5, this.bounds.y0 + 5, 0.0);
         this.renderHeading(matrixStack, mouseX, mouseY, partialTicks);
         this.descriptionComponent.setBounds(this.getDescriptionsBounds());
         this.descriptionComponent.render(matrixStack, mouseX, mouseY, partialTicks);
         this.renderFooter(matrixStack, mouseX, mouseY, partialTicks);
         matrixStack.func_227860_a_();
      }
   }

   private void renderBackground(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      Minecraft.func_71410_x().func_110434_K().func_110577_a(SkillTreeScreen.UI_RESOURCE);
      func_238467_a_(matrixStack, this.bounds.x0 + 5, this.bounds.y0 + 5, this.bounds.x1 - 5, this.bounds.y1 - 5, -3750202);
      this.func_238474_b_(matrixStack, this.bounds.x0, this.bounds.y0, 0, 44, 5, 5);
      this.func_238474_b_(matrixStack, this.bounds.x1 - 5, this.bounds.y0, 8, 44, 5, 5);
      this.func_238474_b_(matrixStack, this.bounds.x0, this.bounds.y1 - 5, 0, 52, 5, 5);
      this.func_238474_b_(matrixStack, this.bounds.x1 - 5, this.bounds.y1 - 5, 8, 52, 5, 5);
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(this.bounds.x0 + 5, this.bounds.y0, 0.0);
      matrixStack.func_227862_a_(this.bounds.getWidth() - 10, 1.0F, 1.0F);
      this.func_238474_b_(matrixStack, 0, 0, 6, 44, 1, 5);
      matrixStack.func_227861_a_(0.0, this.bounds.getHeight() - 5, 0.0);
      this.func_238474_b_(matrixStack, 0, 0, 6, 52, 1, 5);
      matrixStack.func_227865_b_();
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(this.bounds.x0, this.bounds.y0 + 5, 0.0);
      matrixStack.func_227862_a_(1.0F, this.bounds.getHeight() - 10, 1.0F);
      this.func_238474_b_(matrixStack, 0, 0, 0, 50, 5, 1);
      matrixStack.func_227861_a_(this.bounds.getWidth() - 5, 0.0, 0.0);
      this.func_238474_b_(matrixStack, 0, 0, 8, 50, 5, 1);
      matrixStack.func_227865_b_();
   }

   private void renderHeading(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      Minecraft.func_71410_x().func_110434_K().func_110577_a(SkillTreeScreen.UI_RESOURCE);
      FontRenderer fontRenderer = Minecraft.func_71410_x().field_71466_p;
      SkillStyle abilityStyle = ModConfigs.TALENTS_GUI.getStyles().get(this.talentGroup.getParentName());
      TalentNode<?> talentNode = this.talentTree.getNodeByName(this.talentGroup.getParentName());
      Rectangle abilityBounds = this.abilityWidget.getClickableBounds();
      UIHelper.renderContainerBorder(this, matrixStack, this.getHeadingBounds(), 14, 44, 2, 2, 2, 2, -7631989);
      String abilityName = talentNode.getLevel() == 0 ? talentNode.getGroup().getName(1) : talentNode.getName();
      String subText = talentNode.getLevel() == 0 ? "Not Learned Yet" : "Learned";
      int gap = 5;
      int contentWidth = abilityBounds.getWidth() + gap + Math.max(fontRenderer.func_78256_a(abilityName), fontRenderer.func_78256_a(subText));
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(10.0, 0.0, 0.0);
      FontHelper.drawStringWithBorder(
         matrixStack,
         abilityName,
         abilityBounds.getWidth() + gap,
         13.0F,
         talentNode.getLevel() == 0 ? -1 : -1849,
         talentNode.getLevel() == 0 ? -16777216 : -12897536
      );
      FontHelper.drawStringWithBorder(
         matrixStack,
         subText,
         abilityBounds.getWidth() + gap,
         23.0F,
         talentNode.getLevel() == 0 ? -1 : -1849,
         talentNode.getLevel() == 0 ? -16777216 : -12897536
      );
      matrixStack.func_227861_a_(-abilityStyle.x, -abilityStyle.y, 0.0);
      matrixStack.func_227861_a_(abilityBounds.getWidth() / 2.0F, 0.0, 0.0);
      matrixStack.func_227861_a_(0.0, 23.0, 0.0);
      this.abilityWidget.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
      matrixStack.func_227865_b_();
   }

   private void renderDescriptions(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      Rectangle renderableBounds = this.descriptionComponent.getRenderableBounds();
      IFormattableTextComponent description = ModConfigs.SKILL_DESCRIPTIONS.getDescriptionFor(this.talentGroup.getParentName());
      int renderedLineCount = UIHelper.renderWrappedText(matrixStack, description, renderableBounds.getWidth(), 10);
      this.descriptionComponent.setInnerHeight(renderedLineCount * 10 + 20);
      RenderSystem.enableDepthTest();
   }

   private void renderFooter(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      int containerX = mouseX - this.bounds.x0;
      int containerY = mouseY - this.bounds.y0;
      this.abilityUpgradeButton.func_230430_a_(matrixStack, containerX, containerY, partialTicks);
      Minecraft.func_71410_x().func_110434_K().func_110577_a(SkillTreeScreen.UI_RESOURCE);
      TalentNode<?> talentNode = this.talentTree.getNodeOf(this.talentGroup);
      if (talentNode.isLearned() && talentNode.getLevel() < this.talentGroup.getMaxLevel()) {
         this.func_238474_b_(matrixStack, 13, this.bounds.getHeight() - 40 - 2, 121, 0, 15, 23);
      }
   }
}
