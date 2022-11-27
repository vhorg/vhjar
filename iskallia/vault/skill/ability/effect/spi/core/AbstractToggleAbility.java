package iskallia.vault.skill.ability.effect.spi.core;

import iskallia.vault.skill.ability.KeyBehavior;
import iskallia.vault.skill.ability.config.spi.AbstractAbilityConfig;
import net.minecraft.server.level.ServerPlayer;

public abstract class AbstractToggleAbility<C extends AbstractAbilityConfig> extends AbstractAbility<C> {
   protected AbstractToggleAbility() {
      super(KeyBehavior.TOGGLE_ON_RELEASE);
   }

   @Override
   public AbilityActionResult onAction(C config, ServerPlayer player, boolean active) {
      AbilityActionResult result;
      if (this.canToggle(config, player, active)) {
         result = this.doToggle(config, player, active);
         if (result.isSuccess()) {
            this.doToggleParticles(config, player, active);
            this.doToggleSound(config, player, active);
         }
      } else {
         result = AbilityActionResult.FAIL;
      }

      return result;
   }

   @Override
   public AbilityTickResult onTick(C config, ServerPlayer player, boolean active) {
      return active ? this.doActiveTick(config, player) : this.doInactiveTick(config, player);
   }

   protected AbilityTickResult doActiveTick(C config, ServerPlayer player) {
      return AbilityTickResult.PASS;
   }

   protected AbilityTickResult doInactiveTick(C config, ServerPlayer player) {
      return AbilityTickResult.PASS;
   }

   protected boolean canToggle(C config, ServerPlayer player, boolean active) {
      return true;
   }

   protected AbilityActionResult doToggle(C config, ServerPlayer player, boolean active) {
      return active ? AbilityActionResult.SUCCESS_COOLDOWN_DEFERRED : AbilityActionResult.SUCCESS_COOLDOWN;
   }

   protected void doToggleParticles(C config, ServerPlayer player, boolean active) {
   }

   protected void doToggleSound(C config, ServerPlayer player, boolean active) {
   }
}
