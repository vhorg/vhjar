package iskallia.vault.client.gui.screen.player.legacy.tab.split.pan;

import iskallia.vault.client.gui.screen.player.ExpertisesElementContainerScreen;
import iskallia.vault.client.gui.screen.player.legacy.tab.split.dialog.ExpertiseDialog;
import iskallia.vault.client.gui.screen.player.legacy.widget.ExpertiseWidget;
import iskallia.vault.client.gui.screen.player.legacy.widget.TalentGroupWidget;
import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.base.TieredSkill;
import iskallia.vault.skill.tree.ExpertiseTree;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.network.chat.TextComponent;

public class ExpertisePanRegion extends SkillPanRegion<ExpertiseTree, ExpertisesElementContainerScreen, ExpertiseWidget> {
   public ExpertisePanRegion(ExpertiseDialog talentDialog, ExpertisesElementContainerScreen parentScreen) {
      super(parentScreen, new TextComponent("Talents Tab"), talentDialog);
   }

   protected void initSkillWidget(
      ExpertiseTree skillTree, String skillName, SkillStyle style, Map<String, ExpertiseWidget> widgets, List<TalentGroupWidget> groups
   ) {
      widgets.put(skillName, new ExpertiseWidget((TieredSkill)skillTree.getForId(skillName).orElse(null), skillTree, style));
   }

   @Override
   protected HashMap<String, SkillStyle> getStyles() {
      return ModConfigs.EXPERTISES_GUI.getStyles();
   }
}
