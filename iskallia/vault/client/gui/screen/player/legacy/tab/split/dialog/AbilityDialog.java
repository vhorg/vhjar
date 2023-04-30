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
import iskallia.vault.skill.ability.component.AbilityDescriptionFactory;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.base.SpecializedSkill;
import iskallia.vault.skill.base.TieredSkill;
import iskallia.vault.skill.tree.AbilityTree;
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
         TieredSkill ability = (TieredSkill)this.abilityTree.getForId(this.selectedAbility).orElse(null);
         if (ability != null) {
            SpecializedSkill existing = (SpecializedSkill)ability.getParent();
            boolean isSpecialization = existing.getIndex() != 0;
            this.selectedAbilityWidget = new AbilityWidget(
               this.selectedAbility,
               this.abilityTree,
               0,
               0,
               isSpecialization ? AbilityNodeTextures.SECONDARY_NODE : AbilityNodeTextures.PRIMARY_NODE,
               TextureAtlasRegion.of(ModTextureAtlases.ABILITIES, ModConfigs.ABILITIES_GUI.getIcon(this.selectedAbility))
            );
            SpecializedSkill current = this.selectedAbilityWidget.getAbilityGroup();
            SpecializedSkill target = this.selectedAbilityWidget.makeAbilityNode();
            OnPress pressAction;
            String buttonText;
            boolean activeState;
            if (target.getIndex() != 0) {
               buttonText = "Select Specialization";
               pressAction = button -> this.selectSpecialization();
               activeState = existing.getIndex() == 0 && existing.isUnlocked() && VaultBarOverlay.vaultLevel >= target.getUnlockLevel();
               this.regretButton = null;
            } else {
               if (!target.isUnlocked()) {
                  buttonText = "Learn (" + current.getLearnPointCost() + ")";
               } else {
                  buttonText = ((TieredSkill)existing.getSpecialization()).getTier() >= ((TieredSkill)target.getSpecialization()).getMaxTier()
                     ? "Fully Learned"
                     : "Upgrade (" + current.getSpecialization().getLearnPointCost() + ")";
               }

               pressAction = button -> this.upgradeAbility();
               int cost = target.getLearnPointCost();
               int regretCost = existing.isUnlocked() ? existing.getRegretPointCost() : 0;
               activeState = cost <= VaultBarOverlay.unspentSkillPoints
                  && ((TieredSkill)existing.getSpecialization()).getTier() < ((TieredSkill)target.getSpecialization()).getMaxTier()
                  && ((TieredSkill)target.getSpecialization()).getTier() < ((TieredSkill)target.getSpecialization()).getMaxTier() + 1
                  && VaultBarOverlay.vaultLevel >= current.getUnlockLevel();
               String regretButtonText = !existing.isUnlocked() ? "Unlearn" : "Unlearn (" + regretCost + ")";
               boolean hasDependants = false;
               if (((TieredSkill)existing.getSpecialization()).getTier() == 1) {
                  for (String dependent : ModConfigs.SKILL_GATES.getGates().getAbilitiesDependingOn(existing.getId())) {
                     if (this.abilityTree.getForId(dependent).map(Skill::isUnlocked).orElse(false)) {
                        hasDependants = true;
                        break;
                     }
                  }
               }

               this.regretButton = new Button(0, 0, 0, 0, new TextComponent(regretButtonText), button -> this.downgradeAbility(), Button.NO_TOOLTIP);
               this.regretButton.active = existing.isUnlocked() && regretCost <= VaultBarOverlay.unspentRegretPoints && !hasDependants;
            }

            this.descriptionComponent = new ScrollableContainer(this::renderDescriptions);
            this.descriptionContentComponent = AbilityDescriptionFactory.create(
               (TieredSkill)target.getSpecialization(),
               ((TieredSkill)current.getSpecialization()).getTier(),
               ((TieredSkill)current.getSpecialization()).getMaxTier(),
               VaultBarOverlay.vaultLevel
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
      TieredSkill ability = (TieredSkill)this.abilityTree.getForId(this.selectedAbility).orElse(null);
      if (ability.getTier() < ability.getMaxTier()) {
         Minecraft.getInstance().player.playSound(ability.isUnlocked() ? ModSounds.SKILL_TREE_UPGRADE_SFX : ModSounds.SKILL_TREE_LEARN_SFX, 1.0F, 1.0F);
         ((SpecializedSkill)ability.getParent()).learn(SkillContext.ofClient());
         this.update();
         ModNetwork.CHANNEL.sendToServer(new AbilityLevelMessage(ability.getParent().getId(), true));
      }
   }

   private void downgradeAbility() {
      TieredSkill ability = (TieredSkill)this.abilityTree.getForId(this.selectedAbility).orElse(null);
      if (ability != null) {
         SpecializedSkill group = (SpecializedSkill)ability.getParent();
         if (group.isUnlocked()) {
            Minecraft.getInstance().player.playSound(ModSounds.SKILL_TREE_UPGRADE_SFX, 1.0F, 1.0F);
            group.regret(SkillContext.ofClient());
            this.update();
            ModNetwork.CHANNEL.sendToServer(new AbilityLevelMessage(group.getId(), false));
         }
      }
   }

   private void selectSpecialization() {
      SpecializedSkill targetNode = this.selectedAbilityWidget.makeAbilityNode();
      SpecializedSkill existingNode = (SpecializedSkill)this.abilityTree.getForId(this.selectedAbility).map(Skill::getParent).orElse(null);
      String toSelect = targetNode.getSpecialization().getId();
      String abilityName = existingNode.getId();
      if (existingNode.getIndex() == 0 || !targetNode.getId().equals(existingNode.getId())) {
         if (VaultBarOverlay.vaultLevel >= targetNode.getUnlockLevel()) {
            Minecraft.getInstance().player.playSound(ModSounds.SKILL_TREE_UPGRADE_SFX, 1.0F, 1.0F);
            this.abilityTree.specialize(toSelect, SkillContext.ofClient());
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
      SpecializedSkill targetAbilityNode = this.selectedAbilityWidget.makeAbilityNode();
      SpecializedSkill currentAbilityNode = (SpecializedSkill)this.abilityTree.getForId(targetAbilityNode.getId()).orElse(null);
      boolean learned;
      String abilityName;
      String subText;
      if (targetAbilityNode.getIndex() != 0) {
         learned = targetAbilityNode.getIndex() == currentAbilityNode.getIndex();
         abilityName = targetAbilityNode.getSpecialization().getName();
         subText = learned ? "Selected" : "Not selected";
      } else {
         learned = targetAbilityNode.isUnlocked();
         abilityName = targetAbilityNode.getSpecialization().getName();
         if (!learned) {
            subText = "Not learned yet";
         } else {
            subText = "Level: "
               + ((TieredSkill)currentAbilityNode.getSpecialization()).getTier()
               + "/"
               + ((TieredSkill)currentAbilityNode.getSpecialization()).getMaxTier();
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
