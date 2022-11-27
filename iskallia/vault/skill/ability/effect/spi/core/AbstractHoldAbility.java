package iskallia.vault.skill.ability.effect.spi.core;

import iskallia.vault.skill.ability.KeyBehavior;
import iskallia.vault.skill.ability.config.spi.AbstractAbilityConfig;
import net.minecraft.server.level.ServerPlayer;

public abstract class AbstractHoldAbility<C extends AbstractAbilityConfig> extends AbstractAbility<C> {
   protected AbstractHoldAbility() {
      super(KeyBehavior.ACTIVATE_ON_HOLD);
   }

   @Override
   public AbilityActionResult onAction(C config, ServerPlayer player, boolean active) {
      AbilityActionResult result;
      if (active) {
         if (this.canBeginHold(config, player)) {
            result = this.doHoldBeginAction(config, player);
            if (result.isSuccess()) {
               this.doHoldBeginParticles(config, player);
               this.doHoldBeginSound(config, player);
            }
         } else {
            result = AbilityActionResult.FAIL;
         }
      } else {
         result = this.doHoldEndAction(config, player);
         if (result.isSuccess()) {
            this.doHoldEndParticles(config, player);
            this.doHoldEndSound(config, player);
         }
      }

      return result;
   }

   @Override
   public AbilityTickResult onTick(C config, ServerPlayer player, boolean active) {
      return active ? this.doActiveTick(config, player) : this.doInactiveTick(config, player);
   }

   protected boolean canBeginHold(C config, ServerPlayer player) {
      return true;
   }

   protected AbilityActionResult doHoldBeginAction(C config, ServerPlayer player) {
      return AbilityActionResult.SUCCESS_COOLDOWN_DEFERRED;
   }

   protected void doHoldBeginParticles(C config, ServerPlayer player) {
   }

   protected void doHoldBeginSound(C config, ServerPlayer player) {
   }

   protected AbilityTickResult doActiveTick(C config, ServerPlayer player) {
      return AbilityTickResult.PASS;
   }

   protected AbilityTickResult doInactiveTick(C config, ServerPlayer player) {
      return AbilityTickResult.PASS;
   }

   protected AbilityActionResult doHoldEndAction(C config, ServerPlayer player) {
      return AbilityActionResult.SUCCESS_COOLDOWN;
   }

   protected void doHoldEndParticles(C config, ServerPlayer player) {
   }

   protected void doHoldEndSound(C config, ServerPlayer player) {
   }
}
