package iskallia.vault.skill.ability.effect;

import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.config.RampageConfig;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.SoundCategory;

public class RampageAbility<C extends RampageConfig> extends AbilityEffect<C> {
   @Override
   public String getAbilityGroupName() {
      return "Rampage";
   }

   public boolean onAction(C config, ServerPlayerEntity player, boolean active) {
      if (player.func_70644_a(ModEffects.RAMPAGE)) {
         return false;
      } else {
         EffectInstance newEffect = new EffectInstance(
            config.getEffect(), config.getDurationTicks(), config.getAmplifier(), false, config.getType().showParticles, config.getType().showIcon
         );
         player.field_70170_p
            .func_184148_a(
               null, player.func_226277_ct_(), player.func_226278_cu_(), player.func_226281_cx_(), ModSounds.RAMPAGE_SFX, SoundCategory.PLAYERS, 0.2F, 1.0F
            );
         player.func_213823_a(ModSounds.RAMPAGE_SFX, SoundCategory.PLAYERS, 0.2F, 1.0F);
         player.func_195064_c(newEffect);
         return false;
      }
   }
}
