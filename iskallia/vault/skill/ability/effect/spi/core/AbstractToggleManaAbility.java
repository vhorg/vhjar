package iskallia.vault.skill.ability.effect.spi.core;

import iskallia.vault.mana.Mana;
import iskallia.vault.skill.ability.config.spi.AbstractToggleManaConfig;
import iskallia.vault.util.calc.ManaCostHelper;
import net.minecraft.server.level.ServerPlayer;

public abstract class AbstractToggleManaAbility<C extends AbstractToggleManaConfig> extends AbstractToggleAbility<C> {
   protected boolean canToggle(C config, ServerPlayer player, boolean active) {
      if (active && !player.isCreative()) {
         float cost = ManaCostHelper.adjustManaCost(player, this.getAbilityGroupName(), config.getManaCostPerSecond());
         return Mana.get(player) >= cost;
      } else {
         return true;
      }
   }

   protected AbilityTickResult doActiveTick(C config, ServerPlayer player) {
      if (!player.isCreative() && !player.isSpectator()) {
         float cost = ManaCostHelper.adjustManaCost(player, this.getAbilityGroupName(), config.getManaCostPerSecond() / 20.0F);
         if (Mana.decrease(player, cost) <= 0.0F) {
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
