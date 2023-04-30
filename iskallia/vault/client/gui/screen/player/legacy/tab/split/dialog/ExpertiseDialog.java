package iskallia.vault.client.gui.screen.player.legacy.tab.split.dialog;

import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.gui.screen.player.ExpertisesElementContainerScreen;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.ExpertiseLevelMessage;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.tree.ExpertiseTree;
import java.util.HashMap;

public class ExpertiseDialog extends SkillDialog<ExpertiseTree, ExpertisesElementContainerScreen> {
   public ExpertiseDialog(ExpertiseTree talentTree, ExpertisesElementContainerScreen expertisesElementContainerScreen) {
      super(talentTree, expertisesElementContainerScreen);
   }

   @Override
   protected int getUnspentSkillPoints() {
      return VaultBarOverlay.unspentExpertisePoints;
   }

   @Override
   protected void updateRegretButton() {
      this.regretButton = null;
   }

   @Override
   protected HashMap<String, SkillStyle> getStyles() {
      return ModConfigs.EXPERTISES_GUI.getStyles();
   }

   @Override
   protected SkillContext getSkillContext() {
      return SkillContext.ofExpertiseClient();
   }

   @Override
   protected void sendUpgradeMessage() {
      ModNetwork.CHANNEL.sendToServer(new ExpertiseLevelMessage(this.skillGroup.getId(), true));
   }
}
