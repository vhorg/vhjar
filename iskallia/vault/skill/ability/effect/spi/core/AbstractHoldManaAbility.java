package iskallia.vault.skill.ability.effect.spi.core;

import iskallia.vault.mana.Mana;
import iskallia.vault.skill.ability.config.spi.AbstractHoldManaConfig;
import iskallia.vault.util.calc.ManaCostHelper;
import net.minecraft.server.level.ServerPlayer;

public abstract class AbstractHoldManaAbility<C extends AbstractHoldManaConfig> extends AbstractHoldAbility<C> {
   protected boolean canBeginHold(C config, ServerPlayer player) {
      if (player.isCreative()) {
         return true;
      } else {
         float cost = ManaCostHelper.adjustManaCost(player, this.getAbilityGroupName(), config.getManaCostPerSecond() / 20.0F);
         return Mana.get(player) >= cost;
      }
   }

   protected AbilityTickResult doActiveTick(C config, ServerPlayer player) {
      if (!player.isCreative() && !player.isSpectator()) {
         float cost = ManaCostHelper.adjustManaCost(player, this.getAbilityGroupName(), config.getManaCostPerSecond() / 20.0F);
         return Mana.decrease(player, cost) <= 0.0F ? AbilityTickResult.COOLDOWN : AbilityTickResult.PASS;
      } else {
         return AbilityTickResult.PASS;
      }
   }
}
