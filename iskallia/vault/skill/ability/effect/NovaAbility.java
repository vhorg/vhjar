package iskallia.vault.skill.ability.effect;

import iskallia.vault.event.ActiveFlags;
import iskallia.vault.skill.ability.config.NovaConfig;
import iskallia.vault.skill.ability.effect.spi.AbstractNovaAbility;
import iskallia.vault.skill.ability.effect.spi.core.AbilityActionResult;
import java.awt.Color;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.alchemy.Potions;

public class NovaAbility<C extends NovaConfig> extends AbstractNovaAbility<C> {
   protected AbilityActionResult doAction(C config, ServerPlayer player, boolean active) {
      List<LivingEntity> targetEntities = this.getTargetEntities(config, player);
      float attackDamage = this.getAttackDamage(config, player);
      DamageSource damageSource = DamageSource.playerAttack(player);

      for (LivingEntity entity : targetEntities) {
         ActiveFlags.IS_AOE_ATTACKING.runIfNotSet(() -> {
            if (entity.hurt(damageSource, attackDamage) && !Mth.equal(config.getKnockbackStrengthMultiplier(), 0.0F)) {
               double dx = player.getX() - entity.getX();
               double dz = player.getZ() - entity.getZ();
               if (dx * dx + dz * dz < 1.0E-4) {
                  dx = (Math.random() - Math.random()) * 0.01;
                  dz = (Math.random() - Math.random()) * 0.01;
               }

               entity.knockback(0.4F * config.getKnockbackStrengthMultiplier(), dx, dz);
            }
         });
      }

      return AbilityActionResult.SUCCESS_COOLDOWN;
   }

   @Override
   protected void doParticles(C config, ServerPlayer player) {
      super.doParticles(config, player);
      float radius = config.getRadius(player);
      int particleCount = (int)Mth.clamp(Math.pow(radius, 2.0) * (float) Math.PI * 100.0, 50.0, 400.0);
      ((ServerLevel)player.level)
         .sendParticles(ParticleTypes.EXPLOSION, player.getX(), player.getY(), player.getZ(), particleCount, radius * 0.5, 0.5, radius * 0.5, 0.0);
      ((ServerLevel)player.level)
         .sendParticles(ParticleTypes.SMOKE, player.getX(), player.getY(), player.getZ(), particleCount / 2, radius * 0.5, 0.5, radius * 0.5, 0.0);
      AreaEffectCloud areaEffectCloud = new AreaEffectCloud(player.level, player.getX(), player.getY(), player.getZ());
      areaEffectCloud.setOwner(player);
      areaEffectCloud.setRadius(radius);
      areaEffectCloud.setRadiusOnUse(-0.5F);
      areaEffectCloud.setWaitTime(0);
      areaEffectCloud.setDuration(4);
      areaEffectCloud.setPotion(Potions.EMPTY);
      areaEffectCloud.setRadiusPerTick(-areaEffectCloud.getRadius() / areaEffectCloud.getDuration());
      areaEffectCloud.setFixedColor(Color.RED.getRGB());
      areaEffectCloud.setParticle(ParticleTypes.FLAME);
      player.level.addFreshEntity(areaEffectCloud);
   }

   protected void doSound(C config, ServerPlayer player) {
      player.level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 0.2F, 1.0F);
      player.playNotifySound(SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 0.2F, 1.0F);
   }
}
