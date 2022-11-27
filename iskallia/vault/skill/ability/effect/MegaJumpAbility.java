package iskallia.vault.skill.ability.effect;

import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.config.MegaJumpConfig;
import iskallia.vault.skill.ability.effect.spi.AbstractMegaJumpAbility;
import iskallia.vault.skill.ability.effect.spi.core.AbilityActionResult;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;

public class MegaJumpAbility<C extends MegaJumpConfig> extends AbstractMegaJumpAbility<C> {
   protected AbilityActionResult doAction(C config, ServerPlayer player, boolean active) {
      double magnitude = config.getHeight() * 0.15;
      double addY = -Math.min(0.0, player.getDeltaMovement().y());
      player.push(0.0, addY + magnitude, 0.0);
      player.startFallFlying();
      player.hurtMarked = true;
      return AbilityActionResult.SUCCESS_COOLDOWN;
   }

   protected void doParticles(C config, ServerPlayer player) {
      ((ServerLevel)player.level).sendParticles(ParticleTypes.POOF, player.getX(), player.getY(), player.getZ(), 50, 1.0, 0.5, 1.0, 0.0);
   }

   protected void doSound(C config, ServerPlayer player) {
      player.playNotifySound(ModSounds.MEGA_JUMP_SFX, SoundSource.PLAYERS, 0.3F, 1.0F);
      player.level.playSound(player, player.getX(), player.getY(), player.getZ(), ModSounds.MEGA_JUMP_SFX, SoundSource.PLAYERS, 0.3F, 1.0F);
   }
}
