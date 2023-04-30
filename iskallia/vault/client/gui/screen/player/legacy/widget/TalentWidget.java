package iskallia.vault.client.gui.screen.player.legacy.widget;

import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.skill.base.TieredSkill;
import iskallia.vault.skill.tree.TalentTree;
import net.minecraft.network.chat.TextComponent;

public class TalentWidget extends SkillWidget<TalentTree> {
   public TalentWidget(TieredSkill skill, TalentTree talentTree, SkillStyle style) {
      super(talentTree, new TextComponent("the_vault.widgets.talent"), skill, style);
   }
}
