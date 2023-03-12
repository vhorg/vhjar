package iskallia.vault.skill.ability.effect.spi.core;

import iskallia.vault.mana.Mana;
import iskallia.vault.skill.ability.config.spi.AbstractAbilityConfig;
import iskallia.vault.skill.ability.config.spi.IInstantManaConfig;
import iskallia.vault.util.calc.ManaCostHelper;
import net.minecraft.server.level.ServerPlayer;

public abstract class AbstractInstantManaAbility<C extends AbstractAbilityConfig & IInstantManaConfig> extends AbstractInstantAbility<C> {
   @Override
   protected boolean canDoAction(C config, ServerPlayer player, boolean active) {
      if (player.isCreative()) {
         return true;
      } else {
         float cost = ManaCostHelper.adjustManaCost(player, this.getAbilityGroupName(), config.getManaCost());
         return Mana.get(player) >= cost;
      }
   }

   @Override
   protected void doActionPost(C config, ServerPlayer player, boolean active) {
      super.doActionPost(config, player, active);
      if (!player.isCreative()) {
         float cost = ManaCostHelper.adjustManaCost(player, this.getAbilityGroupName(), config.getManaCost());
         Mana.decrease(player, cost);
      }
   }
}
