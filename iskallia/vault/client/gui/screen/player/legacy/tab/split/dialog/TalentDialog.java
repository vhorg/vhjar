package iskallia.vault.client.gui.screen.player.legacy.tab.split.dialog;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.gui.screen.player.TalentsElementContainerScreen;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.TalentLevelMessage;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.tree.TalentTree;
import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextComponent;

public class TalentDialog extends SkillDialog<TalentTree, TalentsElementContainerScreen> {
   public TalentDialog(TalentTree talentTree, TalentsElementContainerScreen skillTreeScreen) {
      super(talentTree, skillTreeScreen);
   }

   @Override
   protected int getUnspentSkillPoints() {
      return VaultBarOverlay.unspentSkillPoints;
   }

   @Override
   protected void updateRegretButton() {
      int regretCost = this.skillGroup.getRegretPointCost();
      boolean hasDependants = false;
      if (this.skillGroup.getTier() == 1) {
         for (String dependent : ModConfigs.SKILL_GATES.getGates().getSkillsDependingOn(this.skillGroup.getId(), this.skilTree)) {
            if (this.skilTree.getForId(dependent).map(Skill::isUnlocked).orElse(false)) {
               hasDependants = true;
               break;
            }
         }
      }

      String regretText = !this.skillGroup.isUnlocked() ? "Unlearn" : "Unlearn (" + regretCost + ")";
      this.regretButton = new Button(0, 0, 0, 0, new TextComponent(regretText), button -> this.unlearnTalent(), Button.NO_TOOLTIP);
      this.regretButton.active = regretCost <= VaultBarOverlay.unspentRegretPoints && this.skillGroup.isUnlocked() && !this.isSkillLocked() && !hasDependants;
   }

   @Override
   protected HashMap<String, SkillStyle> getStyles() {
      return ModConfigs.TALENTS_GUI.getStyles();
   }

   @Override
   protected SkillContext getSkillContext() {
      return SkillContext.ofClient();
   }

   private void unlearnTalent() {
      if (this.skillGroup.isUnlocked()) {
         Minecraft minecraft = Minecraft.getInstance();
         if (minecraft.player != null) {
            minecraft.player.playSound(ModSounds.SKILL_TREE_UPGRADE_SFX, 1.0F, 1.0F);
         }

         this.skillGroup.regret(SkillContext.ofClient());
         this.update();
         ModNetwork.CHANNEL.sendToServer(new TalentLevelMessage(this.skillGroup.getId(), false));
      }
   }

   @Override
   protected void renderRegretButton(PoseStack matrixStack, float partialTicks, int containerX, int containerY) {
      this.regretButton.render(matrixStack, containerX, containerY, partialTicks);
   }

   @Override
   protected void sendUpgradeMessage() {
      ModNetwork.CHANNEL.sendToServer(new TalentLevelMessage(this.skillGroup.getId(), true));
   }
}
