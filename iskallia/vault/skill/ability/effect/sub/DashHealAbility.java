package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.skill.ability.config.sub.DashHealConfig;
import iskallia.vault.skill.ability.effect.DashAbility;
import net.minecraft.entity.player.PlayerEntity;

public class DashHealAbility extends DashAbility<DashHealConfig> {
   public boolean onAction(DashHealConfig config, PlayerEntity player, boolean active) {
      if (super.onAction(config, player, active)) {
         player.func_70691_i(config.getHealPerDash());
         return true;
      } else {
         return false;
      }
   }
}
