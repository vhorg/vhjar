package iskallia.vault.skill.ability.effect.spi.core;

import iskallia.vault.mana.Mana;
import iskallia.vault.skill.ability.config.spi.AbstractToggleManaConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;

public abstract class AbstractToggleManaAbility<C extends AbstractToggleManaConfig> extends AbstractToggleAbility<C> {
   protected boolean canToggle(C config, ServerPlayer player, boolean active) {
      return !active || player.isCreative() || Mana.get(player) >= config.getManaCostPerSecond();
   }

   protected AbilityTickResult doActiveTick(C config, ServerPlayer player) {
      if (!player.isCreative() && !player.isSpectator()) {
         if (Mth.equal(Mana.decrease(player, config.getManaCostPerSecond() / 20.0F), 0.0F)) {
            this.doManaDepleted(config, player);
            return AbilityTickResult.COOLDOWN;
         } else {
            return AbilityTickResult.PASS;
         }
      } else {
         return AbilityTickResult.PASS;
      }
   }

   protected void doManaDepleted(C config, ServerPlayer player) {
   }
}
