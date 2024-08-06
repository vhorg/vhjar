package iskallia.vault.skill.talent.type;

import iskallia.vault.init.ModAttributes;
import iskallia.vault.skill.base.SkillContext;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;

public class DisciplineTalent extends GearAttributeTalent {
   @Override
   public boolean canApply(SkillContext context) {
      return context.getSource().as(Player.class).map(player -> {
         AttributeInstance mana = player.getAttribute(ModAttributes.MANA_MAX);
         return mana != null && mana.getBaseValue() == mana.getValue();
      }).orElse(false) && super.canApply(context);
   }
}
