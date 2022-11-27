package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.init.ModEffects;
import iskallia.vault.skill.ability.config.sub.RampageChainConfig;
import iskallia.vault.skill.ability.effect.spi.AbstractRampageAbility;
import iskallia.vault.skill.ability.effect.spi.core.AbilityActionResult;
import iskallia.vault.skill.ability.effect.spi.core.ToggleAbilityEffect;
import iskallia.vault.util.calc.PlayerStat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class RampageChainAbility extends AbstractRampageAbility<RampageChainConfig> {
   public RampageChainAbility() {
      CommonEvents.PLAYER_STAT.of(PlayerStat.ON_HIT_CHAIN).filter(data -> data.getEntity().hasEffect(ModEffects.RAMPAGE_CHAIN)).register(this, data -> {
         int amplifier = data.getEntity().getEffect(ModEffects.RAMPAGE_CHAIN).getAmplifier();
         data.setValue(data.getValue() + amplifier);
      });
   }

   protected AbilityActionResult doToggle(RampageChainConfig config, ServerPlayer player, boolean active) {
      if (active) {
         ModEffects.RAMPAGE_CHAIN.addTo(player, config.getAdditionalChainCount());
         return AbilityActionResult.SUCCESS_COOLDOWN_DEFERRED;
      } else {
         player.removeEffect(ModEffects.RAMPAGE_CHAIN);
         return AbilityActionResult.SUCCESS_COOLDOWN;
      }
   }

   protected void doManaDepleted(RampageChainConfig config, ServerPlayer player) {
      player.removeEffect(ModEffects.RAMPAGE_CHAIN);
   }

   public static class RampageChainEffect extends ToggleAbilityEffect {
      public RampageChainEffect(int color, ResourceLocation resourceLocation) {
         super("Rampage", color, resourceLocation);
      }
   }
}
