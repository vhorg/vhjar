package iskallia.vault.skill.ability.effect.spi.core;

import iskallia.vault.mana.Mana;
import iskallia.vault.skill.ability.config.spi.AbstractAbilityConfig;
import iskallia.vault.skill.ability.config.spi.IInstantManaConfig;
import net.minecraft.server.level.ServerPlayer;

public abstract class AbstractInstantManaAbility<C extends AbstractAbilityConfig & IInstantManaConfig> extends AbstractInstantAbility<C> {
   @Override
   protected boolean canDoAction(C config, ServerPlayer player, boolean active) {
      return player.isCreative() || Mana.get(player) >= config.getManaCost();
   }

   @Override
   protected void doActionPost(C config, ServerPlayer player, boolean active) {
      super.doActionPost(config, player, active);
      if (!player.isCreative()) {
         Mana.decrease(player, config.getManaCost());
      }
   }
}
