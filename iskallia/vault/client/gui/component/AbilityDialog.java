package iskallia.vault.client.gui.component;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.client.gui.helper.FontHelper;
import iskallia.vault.client.gui.helper.Rectangle;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.gui.screen.SkillTreeScreen;
import iskallia.vault.client.gui.widget.AbilityWidget;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.AbilityUpgradeMessage;
import iskallia.vault.skill.ability.AbilityGroup;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.type.PlayerAbility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;

public class AbilityDialog extends AbstractGui {
   private Rectangle bounds;
   private AbilityGroup<?> abilityGroup = null;
   private AbilityTree abilityTree;
   private AbilityWidget abilityWidget;
   private ScrollableContainer descriptionComponent;
   private Button abilityUpgradeButton;

   public AbilityDialog(AbilityTree abilityTree) {
      this.abilityTree = abilityTree;
      this.refreshWidgets();
   }

   public void refreshWidgets() {
      if (this.abilityGroup != null) {
         SkillStyle abilityStyle = ModConfigs.ABILITIES_GUI.getStyles().get(this.abilityGroup.getParentName());
         this.abilityWidget = new AbilityWidget(this.abilityGroup, this.abilityTree, abilityStyle);
         AbilityNode<?> abilityNode = this.abilityTree.getNodeOf(this.abilityGroup);
         String buttonText = !abilityNode.isLearned()
            ? "Learn (" + this.abilityGroup.learningCost() + ")"
            : (
               abilityNode.getLevel() >= this.abilityGroup.getMaxLevel()
                  ? "Fully Learned"
                  : "Upgrade (" + this.abilityGroup.cost(abilityNode.getLevel() + 1) + ")"
            );
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
         PlayerAbility ability = abilityNode.getAbility();
         int cost = ability == null ? this.abilityGroup.learningCost() : this.abilityGroup.cost(abilityNode.getLevel() + 1);
         this.abilityUpgradeButton.field_230693_o_ = cost <= VaultBarOverlay.unspentSkillPoints && abilityNode.getLevel() < this.abilityGroup.getMaxLevel();
      }
   }

   public void setAbilityGroup(AbilityGroup<?> abilityGroup) {
      this.abilityGroup = abilityGroup;
      this.refreshWidgets();
   }

   public AbilityDialog setBounds(Rectangle bounds) {
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
      AbilityNode<?> abilityNode = this.abilityTree.getNodeOf(this.abilityGroup);
      if (abilityNode.getLevel() < this.abilityGroup.getMaxLevel()) {
         Minecraft minecraft = Minecraft.func_71410_x();
         if (minecraft.field_71439_g != null) {
            minecraft.field_71439_g.func_184185_a(abilityNode.isLearned() ? ModSounds.SKILL_TREE_UPGRADE_SFX : ModSounds.SKILL_TREE_LEARN_SFX, 1.0F, 1.0F);
         }

         this.abilityTree.upgradeAbility(null, abilityNode);
         this.refreshWidgets();
         ModNetwork.CHANNEL.sendToServer(new AbilityUpgradeMessage(this.abilityGroup.getParentName()));
      }
   }

   public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      matrixStack.func_227860_a_();
      this.renderBackground(matrixStack, mouseX, mouseY, partialTicks);
      if (this.abilityGroup != null) {
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
      SkillStyle abilityStyle = ModConfigs.ABILITIES_GUI.getStyles().get(this.abilityGroup.getParentName());
      AbilityNode<?> abilityNode = this.abilityTree.getNodeByName(this.abilityGroup.getParentName());
      Rectangle abilityBounds = this.abilityWidget.getClickableBounds();
      UIHelper.renderContainerBorder(this, matrixStack, this.getHeadingBounds(), 14, 44, 2, 2, 2, 2, -7631989);
      String abilityName = abilityNode.getLevel() == 0 ? abilityNode.getGroup().getName(1) : abilityNode.getName();
      String subText = abilityNode.getLevel() == 0 ? "Not Learned Yet" : "Learned";
      int gap = 5;
      int contentWidth = abilityBounds.getWidth() + gap + Math.max(fontRenderer.func_78256_a(abilityName), fontRenderer.func_78256_a(subText));
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(10.0, 0.0, 0.0);
      FontHelper.drawStringWithBorder(
         matrixStack,
         abilityName,
         abilityBounds.getWidth() + gap,
         13.0F,
         abilityNode.getLevel() == 0 ? -1 : -1849,
         abilityNode.getLevel() == 0 ? -16777216 : -12897536
      );
      FontHelper.drawStringWithBorder(
         matrixStack,
         subText,
         abilityBounds.getWidth() + gap,
         23.0F,
         abilityNode.getLevel() == 0 ? -1 : -1849,
         abilityNode.getLevel() == 0 ? -16777216 : -12897536
      );
      matrixStack.func_227861_a_(-abilityStyle.x, -abilityStyle.y, 0.0);
      matrixStack.func_227861_a_(abilityBounds.getWidth() / 2.0F, 0.0, 0.0);
      matrixStack.func_227861_a_(0.0, 23.0, 0.0);
      this.abilityWidget.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
      matrixStack.func_227865_b_();
   }

   private void renderDescriptions(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      Rectangle renderableBounds = this.descriptionComponent.getRenderableBounds();
      IFormattableTextComponent description = ModConfigs.SKILL_DESCRIPTIONS.getDescriptionFor(this.abilityGroup.getParentName());
      int renderedLineCount = UIHelper.renderWrappedText(matrixStack, description, renderableBounds.getWidth(), 10);
      this.descriptionComponent.setInnerHeight(renderedLineCount * 10 + 20);
      RenderSystem.enableDepthTest();
   }

   private void renderFooter(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      int containerX = mouseX - this.bounds.x0;
      int containerY = mouseY - this.bounds.y0;
      this.abilityUpgradeButton.func_230430_a_(matrixStack, containerX, containerY, partialTicks);
      Minecraft.func_71410_x().func_110434_K().func_110577_a(SkillTreeScreen.UI_RESOURCE);
      AbilityNode<?> abilityNode = this.abilityTree.getNodeOf(this.abilityGroup);
      if (abilityNode.isLearned() && abilityNode.getLevel() < this.abilityGroup.getMaxLevel()) {
         this.func_238474_b_(matrixStack, 13, this.bounds.getHeight() - 40 - 2, 121, 0, 15, 23);
      }
   }
}
