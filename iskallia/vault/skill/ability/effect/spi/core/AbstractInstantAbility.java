package iskallia.vault.skill.ability.effect.spi.core;

import iskallia.vault.skill.ability.KeyBehavior;
import iskallia.vault.skill.ability.config.spi.AbstractAbilityConfig;
import net.minecraft.server.level.ServerPlayer;

public abstract class AbstractInstantAbility<C extends AbstractAbilityConfig> extends AbstractAbility<C> {
   protected AbstractInstantAbility() {
      super(KeyBehavior.INSTANT_ON_RELEASE);
   }

   @Override
   public AbilityActionResult onAction(C config, ServerPlayer player, boolean active) {
      if (this.canDoAction(config, player, active)) {
         AbilityActionResult abilityActionResult = this.doAction(config, player, active);
         if (abilityActionResult.isSuccess()) {
            this.doActionPost(config, player, active);
            this.doParticles(config, player);
            this.doSound(config, player);
            return abilityActionResult;
         }
      }

      return AbilityActionResult.FAIL;
   }

   protected boolean canDoAction(C config, ServerPlayer player, boolean active) {
      return true;
   }

   protected AbilityActionResult doAction(C config, ServerPlayer player, boolean active) {
      return AbilityActionResult.FAIL;
   }

   protected void doActionPost(C config, ServerPlayer player, boolean active) {
   }

   protected void doParticles(C config, ServerPlayer player) {
   }

   protected void doSound(C config, ServerPlayer player) {
   }
}
