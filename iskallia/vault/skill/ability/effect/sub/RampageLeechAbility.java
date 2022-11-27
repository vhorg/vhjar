package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.init.ModEffects;
import iskallia.vault.skill.ability.config.sub.RampageLeechConfig;
import iskallia.vault.skill.ability.effect.spi.AbstractRampageAbility;
import iskallia.vault.skill.ability.effect.spi.core.AbilityActionResult;
import iskallia.vault.skill.ability.effect.spi.core.ToggleAbilityEffect;
import iskallia.vault.util.calc.PlayerStat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;

public class RampageLeechAbility extends AbstractRampageAbility<RampageLeechConfig> {
   public RampageLeechAbility() {
      CommonEvents.PLAYER_STAT.of(PlayerStat.LEECH).filter(data -> data.getEntity().hasEffect(ModEffects.RAMPAGE_LEECH)).register(this, data -> {
         int amplifier = data.getEntity().getEffect(ModEffects.RAMPAGE_LEECH).getAmplifier();
         float leechPercent = amplifier / 100.0F;
         data.setValue(data.getValue() + leechPercent);
      });
   }

   protected AbilityActionResult doToggle(RampageLeechConfig config, ServerPlayer player, boolean active) {
      if (active) {
         int amplifier = (int)Mth.clamp(config.getLeechPercent() * 100.0F, 0.0F, 100.0F);
         ModEffects.RAMPAGE_LEECH.addTo(player, amplifier);
         return AbilityActionResult.SUCCESS_COOLDOWN_DEFERRED;
      } else {
         player.removeEffect(ModEffects.RAMPAGE_LEECH);
         return AbilityActionResult.SUCCESS_COOLDOWN;
      }
   }

   protected void doManaDepleted(RampageLeechConfig config, ServerPlayer player) {
      player.removeEffect(ModEffects.RAMPAGE_LEECH);
   }

   public static class RampageLeechEffect extends ToggleAbilityEffect {
      public RampageLeechEffect(int color, ResourceLocation resourceLocation) {
         super("Rampage", color, resourceLocation);
      }
   }
}
