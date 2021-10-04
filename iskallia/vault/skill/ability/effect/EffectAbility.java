package iskallia.vault.skill.ability.effect;

import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.config.EffectConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;

public abstract class EffectAbility extends AbilityEffect<EffectConfig> {
   public void onRemoved(EffectConfig config, PlayerEntity player) {
      player.func_195063_d(config.getEffect());
   }

   public void onBlur(EffectConfig config, PlayerEntity player) {
      player.func_195063_d(config.getEffect());
   }

   public void onTick(EffectConfig config, PlayerEntity player, boolean active) {
      if (!active) {
         player.func_195063_d(config.getEffect());
      } else {
         EffectInstance activeEffect = player.func_70660_b(config.getEffect());
         EffectInstance newEffect = new EffectInstance(
            config.getEffect(), Integer.MAX_VALUE, config.getAmplifier(), false, config.getType().showParticles, config.getType().showIcon
         );
         if (activeEffect == null) {
            player.func_195064_c(newEffect);
         }
      }
   }

   public boolean onAction(EffectConfig config, PlayerEntity player, boolean active) {
      if (active) {
         this.playEffects(config, player);
      }

      return true;
   }

   private void playEffects(EffectConfig config, PlayerEntity player) {
      if (config.getEffect() == Effects.field_76441_p) {
         player.field_70170_p
            .func_184148_a(
               player,
               player.func_226277_ct_(),
               player.func_226278_cu_(),
               player.func_226281_cx_(),
               ModSounds.INVISIBILITY_SFX,
               SoundCategory.MASTER,
               0.175F,
               1.0F
            );
         player.func_213823_a(ModSounds.INVISIBILITY_SFX, SoundCategory.MASTER, 0.7F, 1.0F);
      } else if (config.getEffect() == Effects.field_76439_r) {
         player.field_70170_p
            .func_184148_a(
               player,
               player.func_226277_ct_(),
               player.func_226278_cu_(),
               player.func_226281_cx_(),
               ModSounds.NIGHT_VISION_SFX,
               SoundCategory.MASTER,
               0.0375F,
               1.0F
            );
         player.func_213823_a(ModSounds.NIGHT_VISION_SFX, SoundCategory.MASTER, 0.15F, 1.0F);
      }
   }
}
