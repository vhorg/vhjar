package iskallia.vault.skill.ability.effect.spi.core;

import iskallia.vault.mana.Mana;
import iskallia.vault.skill.ability.config.spi.AbstractHoldManaConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;

public abstract class AbstractHoldManaAbility<C extends AbstractHoldManaConfig> extends AbstractHoldAbility<C> {
   protected boolean canBeginHold(C config, ServerPlayer player) {
      return player.isCreative() || Mana.get(player) >= config.getManaCostPerSecond() / 20.0F;
   }

   protected AbilityTickResult doActiveTick(C config, ServerPlayer player) {
      if (!player.isCreative() && !player.isSpectator()) {
         return Mth.equal(Mana.decrease(player, config.getManaCostPerSecond() / 20.0F), 0.0F) ? AbilityTickResult.COOLDOWN : AbilityTickResult.PASS;
      } else {
         return AbilityTickResult.PASS;
      }
   }
}
