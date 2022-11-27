package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.init.ModParticles;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.config.sub.NovaSpeedConfig;
import iskallia.vault.skill.ability.effect.spi.AbstractNovaAbility;
import iskallia.vault.skill.ability.effect.spi.core.AbilityActionResult;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;

public class NovaSpeedAbility extends AbstractNovaAbility<NovaSpeedConfig> {
   protected AbilityActionResult doAction(NovaSpeedConfig config, ServerPlayer player, boolean active) {
      for (LivingEntity nearbyEntity : player.level
         .getNearbyEntities(
            LivingEntity.class,
            TargetingConditions.forCombat().selector(entity -> !(entity instanceof Player)).range(config.getRadius()),
            player,
            player.getBoundingBox().inflate(config.getRadius())
         )) {
         nearbyEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, config.getDurationTicks(), config.getAmplifier()));
      }

      return AbilityActionResult.SUCCESS_COOLDOWN;
   }

   protected void doParticles(NovaSpeedConfig config, ServerPlayer player) {
      super.doParticles(config, player);
      int particleCount = (int)Mth.clamp(Math.pow(config.getRadius(), 2.0) * (float) Math.PI * 100.0, 50.0, 400.0);
      ((ServerLevel)player.level)
         .sendParticles(
            (SimpleParticleType)ModParticles.NOVA_SPEED.get(),
            player.getX(),
            player.getY(),
            player.getZ(),
            particleCount,
            config.getRadius() * 0.5,
            0.5,
            config.getRadius() * 0.5,
            0.0
         );
   }

   protected void doSound(NovaSpeedConfig config, ServerPlayer player) {
      player.level.playSound(player, player.getX(), player.getY(), player.getZ(), ModSounds.NOVA_SPEED, SoundSource.PLAYERS, 0.2F, 1.0F);
      player.playNotifySound(ModSounds.NOVA_SPEED, SoundSource.PLAYERS, 0.2F, 1.0F);
   }
}
