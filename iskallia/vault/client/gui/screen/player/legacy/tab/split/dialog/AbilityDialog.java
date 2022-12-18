package iskallia.vault.client.gui.screen.player.legacy.tab.split.dialog;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.component.ScrollableContainer;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.helper.FontHelper;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.gui.screen.player.AbilitiesElementContainerScreen;
import iskallia.vault.client.gui.screen.player.legacy.tab.split.spi.AbstractDialog;
import iskallia.vault.client.gui.screen.player.legacy.widget.AbilityNodeTextures;
import iskallia.vault.client.gui.screen.player.legacy.widget.AbilityWidget;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.init.ModTextureAtlases;
import iskallia.vault.network.message.AbilityLevelMessage;
import iskallia.vault.network.message.AbilitySelectSpecializationMessage;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityRegistry;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.component.AbilityDescriptionFactory;
import iskallia.vault.skill.ability.config.spi.AbstractAbilityConfig;
import iskallia.vault.skill.ability.effect.spi.core.AbstractAbility;
import iskallia.vault.skill.ability.group.AbilityGroup;
import java.awt.Rectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

public class AbilityDialog extends AbstractDialog<AbilitiesElementContainerScreen> {
   private final AbilityTree abilityTree;
   private MutableComponent descriptionContentComponent;
   private String selectedAbility = null;
   private AbilityWidget selectedAbilityWidget = null;

   public AbilityDialog(AbilityTree abilityTree, AbilitiesElementContainerScreen skillTreeScreen) {
      super(skillTreeScreen);
      this.abilityTree = abilityTree;
   }

   @Override
   public void update() {
      if (this.selectedAbility != null) {
         AbstractAbility<?> ability = AbilityRegistry.getAbility(this.selectedAbility);
         if (ability != null) {
            AbilityNode<?, ?> existingNode = this.abilityTree.getNodeOf(ability);
            boolean isSpecialization = !ability.getAbilityGroupName().equals(this.selectedAbility);
            this.selectedAbilityWidget = new AbilityWidget(
               this.selectedAbility,
               this.abilityTree,
               0,
               0,
               isSpecialization ? AbilityNodeTextures.SECONDARY_NODE : AbilityNodeTextures.PRIMARY_NODE,
               TextureAtlasRegion.of(ModTextureAtlases.ABILITIES, ModConfigs.ABILITIES_GUI.getIcon(this.selectedAbility))
            );
            AbilityNode<?, ?> targetAbilityNode = this.selectedAbilityWidget.makeAbilityNode();
            AbilityGroup<?, ?> targetAbilityGroup = targetAbilityNode.getGroup();
            OnPress pressAction;
            String buttonText;
            boolean activeState;
            if (targetAbilityNode.getSpecialization() != null) {
               buttonText = "Select Specialization";
               pressAction = button -> this.selectSpecialization();
               activeState = existingNode.getSpecialization() == null
                  && existingNode.isLearned()
                  && VaultBarOverlay.vaultLevel >= targetAbilityNode.getAbilityConfig().getLevelRequirement();
               this.regretButton = null;
            } else {
               if (!targetAbilityNode.isLearned()) {
                  buttonText = "Learn (" + targetAbilityGroup.learningCost() + ")";
               } else {
                  buttonText = existingNode.getLevel() >= targetAbilityGroup.getMaxLevel()
                     ? "Fully Learned"
                     : "Upgrade (" + targetAbilityGroup.levelUpCost(targetAbilityNode.getSpecialization(), targetAbilityNode.getLevel()) + ")";
               }

               pressAction = button -> this.upgradeAbility();
               AbstractAbilityConfig config = targetAbilityNode.getAbilityConfig();
               int cost = config == null
                  ? targetAbilityGroup.learningCost()
                  : targetAbilityGroup.levelUpCost(targetAbilityNode.getSpecialization(), targetAbilityNode.getLevel());
               int regretCost = existingNode.isLearned() ? existingNode.getAbilityConfig().getRegretCost() : 0;
               activeState = cost <= VaultBarOverlay.unspentSkillPoints
                  && existingNode.getLevel() < targetAbilityGroup.getMaxLevel()
                  && targetAbilityNode.getLevel() < targetAbilityGroup.getMaxLevel() + 1
                  && VaultBarOverlay.vaultLevel >= targetAbilityNode.getAbilityConfig().getLevelRequirement();
               String regretButtonText = !existingNode.isLearned() ? "Unlearn" : "Unlearn (" + regretCost + ")";
               boolean hasDependants = false;
               if (existingNode.getLevel() == 1) {
                  for (AbilityGroup<?, ?> dependent : ModConfigs.SKILL_GATES
                     .getGates()
                     .getAbilitiesDependingOn(existingNode.getAbility().getAbilityGroupName())) {
                     if (this.abilityTree.getNodeOf(dependent).isLearned()) {
                        hasDependants = true;
                        break;
                     }
                  }
               }

               this.regretButton = new Button(0, 0, 0, 0, new TextComponent(regretButtonText), button -> this.downgradeAbility(), Button.NO_TOOLTIP);
               this.regretButton.active = existingNode.isLearned() && regretCost <= VaultBarOverlay.unspentRegretPoints && !hasDependants;
            }

            this.descriptionComponent = new ScrollableContainer(this::renderDescriptions);
            this.descriptionContentComponent = AbilityDescriptionFactory.create(
               existingNode.getGroup(), targetAbilityNode.getSpecialization(), existingNode.getLevel(), VaultBarOverlay.vaultLevel
            );
            this.learnButton = new Button(0, 0, 0, 0, new TextComponent(buttonText), pressAction, Button.NO_TOOLTIP);
            this.learnButton.active = activeState;
         }
      }
   }

   public void setAbilityWidget(String abilityName) {
      this.selectedAbility = abilityName;
      this.update();
   }

   private void upgradeAbility() {
      AbilityNode<?, ?> abilityNode = this.abilityTree.getNodeByName(AbilityRegistry.getAbility(this.selectedAbility).getAbilityGroupName());
      if (abilityNode.getLevel() < abilityNode.getGroup().getMaxLevel()) {
         Minecraft.getInstance().player.playSound(abilityNode.isLearned() ? ModSounds.SKILL_TREE_UPGRADE_SFX : ModSounds.SKILL_TREE_LEARN_SFX, 1.0F, 1.0F);
         this.abilityTree.upgradeAbility(null, abilityNode);
         this.update();
         ModNetwork.CHANNEL.sendToServer(new AbilityLevelMessage(abilityNode.getGroup().getParentName(), true));
      }
   }

   private void downgradeAbility() {
      AbstractAbility<?> ability = AbilityRegistry.getAbility(this.selectedAbility);
      if (ability != null) {
         boolean isSpecialization = !ability.getAbilityGroupName().equals(this.selectedAbility);
         if (!isSpecialization) {
            AbilityNode<?, ?> abilityNode = this.abilityTree.getNodeOf(ability);
            if (abilityNode.isLearned()) {
               Minecraft.getInstance().player.playSound(ModSounds.SKILL_TREE_UPGRADE_SFX, 1.0F, 1.0F);
               if (abilityNode.getLevel() == 1) {
                  this.abilityTree.selectSpecialization(ability.getAbilityGroupName(), null);
               }

               this.abilityTree.downgradeAbility(null, abilityNode);
               this.update();
               ModNetwork.CHANNEL.sendToServer(new AbilityLevelMessage(abilityNode.getGroup().getParentName(), false));
            }
         }
      }
   }

   private void selectSpecialization() {
      AbilityNode<?, ?> targetNode = this.selectedAbilityWidget.makeAbilityNode();
      AbilityNode<?, ?> existingNode = this.abilityTree.getNodeByName(AbilityRegistry.getAbility(this.selectedAbility).getAbilityGroupName());
      String toSelect = targetNode.getSpecialization();
      String abilityName = existingNode.getGroup().getParentName();
      if (existingNode.getSpecialization() == null || !targetNode.getGroup().equals(existingNode.getGroup())) {
         if (VaultBarOverlay.vaultLevel >= targetNode.getAbilityConfig().getLevelRequirement()) {
            Minecraft.getInstance().player.playSound(ModSounds.SKILL_TREE_UPGRADE_SFX, 1.0F, 1.0F);
            this.abilityTree.selectSpecialization(abilityName, toSelect);
            this.skillTreeScreen.update();
            ModNetwork.CHANNEL.sendToServer(new AbilitySelectSpecializationMessage(abilityName, toSelect));
         }
      }
   }

   @Override
   public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      super.render(matrixStack, mouseX, mouseY, partialTicks);
      this.renderBackground(matrixStack, mouseX, mouseY, partialTicks);
      if (this.selectedAbility != null && this.selectedAbilityWidget != null) {
         matrixStack.pushPose();
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

      int width = this.selectedAbilityWidget.getWidth();
      UIHelper.renderContainerBorder(this, matrixStack, this.getHeadingBounds(), 14, 44, 2, 2, 2, 2, -7631989);
      int gap = 5;
      matrixStack.pushPose();
      matrixStack.translate(10.0, 0.0, 0.0);
      FontHelper.drawStringWithBorder(matrixStack, abilityName, (float)(width + gap), 13.0F, !learned ? -1 : -1849, !learned ? -16777216 : -12897536);
      FontHelper.drawStringWithBorder(matrixStack, subText, (float)(width + gap), 23.0F, !learned ? -1 : -1849, !learned ? -16777216 : -12897536);
      matrixStack.translate(width / 2.0F, 0.0, 0.0);
      matrixStack.translate(0.0, 23.0, 0.0);
      this.selectedAbilityWidget.render(matrixStack, mouseX, mouseY, partialTicks);
      matrixStack.popPose();
   }

   private void renderDescriptions(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      Rectangle bounds = this.descriptionComponent.getRenderableBounds();
      int renderedLineCount = UIHelper.renderWrappedText(matrixStack, this.descriptionContentComponent, bounds.width, 10);
      this.descriptionComponent.setInnerHeight(renderedLineCount * 10 + 20);
      RenderSystem.enableDepthTest();
   }

   private void renderFooter(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      int containerX = mouseX - this.bounds.x - 5;
      int containerY = mouseY - this.bounds.y - 5;
      this.learnButton.render(matrixStack, containerX, containerY, partialTicks);
      if (this.regretButton != null) {
         this.regretButton.render(matrixStack, containerX, containerY, partialTicks);
      }
   }
}
