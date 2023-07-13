package iskallia.vault.client.gui.screen.player.legacy.tab.split.dialog;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.component.ScrollableContainer;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.helper.FontHelper;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.gui.screen.player.legacy.LegacySkillTreeElementContainerScreen;
import iskallia.vault.client.gui.screen.player.legacy.tab.split.spi.AbstractDialog;
import iskallia.vault.client.gui.screen.player.legacy.widget.SkillWidget;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.base.GroupedSkill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.base.TieredSkill;
import iskallia.vault.skill.tree.SkillTree;
import java.awt.Rectangle;
import java.util.HashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;

public abstract class SkillDialog<T extends SkillTree, S extends LegacySkillTreeElementContainerScreen<T>> extends AbstractDialog<S> {
   protected final T skilTree;
   protected TieredSkill skillGroup = null;
   protected SkillWidget<T> skillWidget = null;

   protected SkillDialog(T skillTree, S skillTreeScreen) {
      super(skillTreeScreen);
      this.skilTree = skillTree;
   }

   @Override
   public void update() {
      if (this.skillGroup != null) {
         SkillStyle abilityStyle = this.getStyles().get(this.skillGroup.getId());
         this.skillWidget = new SkillWidget<>(this.skilTree, new TextComponent("the_vault.widgets.skill"), this.skillGroup, abilityStyle);
         this.skillWidget.setRenderPips(false);
         int cost = this.skillGroup.getLearnPointCost();
         String levelUpText = !this.skillGroup.isUnlocked()
            ? "Learn (" + this.skillGroup.getLearnPointCost() + ")"
            : (this.skillGroup.getUnmodifiedTier() >= this.skillGroup.getMaxLearnableTier() ? "Learned" : "Upgrade (" + cost + ")");
         this.learnButton = new Button(0, 0, 0, 0, new TextComponent(levelUpText), button -> this.upgradeSkill(), Button.NO_TOOLTIP);
         this.descriptionComponent = new ScrollableContainer(this::renderDescriptions);
         boolean isLocked = this.isSkillLocked();
         boolean fulfillsRequirements = VaultBarOverlay.vaultLevel >= this.skillGroup.getUnlockLevel();
         if (this.skillGroup.getParent() instanceof GroupedSkill grouped) {
            grouped.select(this.skillGroup.getId());
            fulfillsRequirements &= grouped.canLearn(SkillContext.ofClient());
         }

         this.learnButton.active = cost <= this.getUnspentSkillPoints()
            && fulfillsRequirements
            && !isLocked
            && this.skillGroup.getUnmodifiedTier() < this.skillGroup.getMaxLearnableTier();
         this.updateRegretButton();
      }
   }

   protected abstract int getUnspentSkillPoints();

   protected abstract void updateRegretButton();

   protected boolean isSkillLocked() {
      return ModConfigs.SKILL_GATES.getGates().isLocked(this.skillGroup.getId(), this.skilTree);
   }

   protected abstract HashMap<String, SkillStyle> getStyles();

   private void upgradeSkill() {
      if (this.skillGroup.getUnmodifiedTier() < this.skillGroup.getMaxLearnableTier()) {
         if (VaultBarOverlay.vaultLevel >= this.skillGroup.getUnlockLevel()) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player != null) {
               minecraft.player.playSound(this.skillGroup.isUnlocked() ? ModSounds.SKILL_TREE_UPGRADE_SFX : ModSounds.SKILL_TREE_LEARN_SFX, 1.0F, 1.0F);
            }

            this.skillGroup.learn(this.getSkillContext());
            this.update();
            this.sendUpgradeMessage();
         }
      }
   }

   protected abstract SkillContext getSkillContext();

   protected abstract void sendUpgradeMessage();

   public void setSkillGroup(TieredSkill skillGroup) {
      this.skillGroup = skillGroup;
      this.update();
   }

   private void renderDescriptions(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      Rectangle renderableBounds = this.descriptionComponent.getRenderableBounds();
      TextComponent text = new TextComponent("");
      text.append(ModConfigs.SKILL_DESCRIPTIONS.getDescriptionFor(this.skillGroup.getId()));
      text.append("\n\n").append(this.getAdditionalDescription(this.skillGroup));
      int renderedLineCount = UIHelper.renderWrappedText(matrixStack, text, renderableBounds.width, 10);
      this.descriptionComponent.setInnerHeight(renderedLineCount * 10 + 20);
      RenderSystem.enableDepthTest();
   }

   private Component getAdditionalDescription(TieredSkill skillGroup) {
      String arrow = String.valueOf('▶');
      Component costArrowTxt = new TextComponent(" " + arrow + " ").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(4737095)));
      Component lvlReqArrowTxt = new TextComponent(" " + arrow + " ").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(4737095)));
      MutableComponent txt = new TextComponent("Cost: ").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(4737095)));

      for (int lvl = 1; lvl <= skillGroup.getMaxLearnableTier(); lvl++) {
         if (lvl > 1) {
            txt.append(costArrowTxt);
         }

         int cost = skillGroup.getChild(lvl).getLearnPointCost();
         txt.append(new TextComponent(String.valueOf(cost)).withStyle(ChatFormatting.WHITE));
      }

      boolean displayRequirements = false;
      TextComponent lvlReq = new TextComponent("\n\nLevel requirement: ");

      for (int lvl = 1; lvl <= skillGroup.getMaxLearnableTier(); lvl++) {
         if (lvl > 1) {
            lvlReq.append(lvlReqArrowTxt);
         }

         int levelRequirement = skillGroup.getChild(lvl).getUnlockLevel();
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

   @Override
   public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      super.render(matrixStack, mouseX, mouseY, partialTicks);
      matrixStack.pushPose();
      this.renderBackground(matrixStack, mouseX, mouseY, partialTicks);
      if (this.skillGroup != null) {
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
      SkillStyle style = this.getStyles().get(this.skillGroup.getId());
      Rectangle widgetBounds = this.skillWidget.getClickableBounds();
      UIHelper.renderContainerBorder(this, matrixStack, this.getHeadingBounds(), 14, 44, 2, 2, 2, 2, -7631989);
      String abilityName = this.skillGroup.getName();
      String subText = !this.skillGroup.isUnlocked()
         ? "Not Learned Yet"
         : "Level: " + this.skillGroup.getUnmodifiedTier() + "/" + this.skillGroup.getMaxLearnableTier();
      int gap = 5;
      matrixStack.pushPose();
      matrixStack.translate(10.0, 0.0, 0.0);
      FontHelper.drawStringWithBorder(
         matrixStack,
         abilityName,
         (float)(widgetBounds.width + gap),
         13.0F,
         !this.skillGroup.isUnlocked() ? -1 : -1849,
         !this.skillGroup.isUnlocked() ? -16777216 : -12897536
      );
      FontHelper.drawStringWithBorder(
         matrixStack,
         subText,
         (float)(widgetBounds.width + gap),
         23.0F,
         !this.skillGroup.isUnlocked() ? -1 : -1849,
         !this.skillGroup.isUnlocked() ? -16777216 : -12897536
      );
      matrixStack.translate(-style.x, -style.y, 0.0);
      matrixStack.translate(widgetBounds.getWidth() / 2.0, 0.0, 0.0);
      matrixStack.translate(0.0, 23.0, 0.0);
      this.skillWidget.render(matrixStack, mouseX, mouseY, partialTicks);
      matrixStack.popPose();
   }

   protected void renderFooter(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      int containerX = mouseX - this.bounds.x - 5;
      int containerY = mouseY - this.bounds.y - 5;
      this.learnButton.render(matrixStack, containerX, containerY, partialTicks);
      this.renderRegretButton(matrixStack, partialTicks, containerX, containerY);
   }

   protected void renderRegretButton(PoseStack matrixStack, float partialTicks, int containerX, int containerY) {
   }
}
