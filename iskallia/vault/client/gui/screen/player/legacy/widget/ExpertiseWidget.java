package iskallia.vault.client.gui.screen.player.legacy.widget;

import iskallia.vault.config.entry.SkillStyle;
import iskallia.vault.skill.base.TieredSkill;
import iskallia.vault.skill.tree.ExpertiseTree;
import net.minecraft.network.chat.TextComponent;

public class ExpertiseWidget extends SkillWidget<ExpertiseTree> {
   public ExpertiseWidget(TieredSkill skill, ExpertiseTree expertiseTree, SkillStyle style) {
      super(expertiseTree, new TextComponent("the_vault.widgets.expertise"), skill, style);
   }
}
