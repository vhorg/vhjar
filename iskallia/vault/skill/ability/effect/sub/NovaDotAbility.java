package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.init.ModParticles;
import iskallia.vault.skill.ability.config.sub.NovaDotConfig;
import iskallia.vault.skill.ability.effect.spi.AbstractNovaAbility;
import iskallia.vault.skill.ability.effect.spi.core.AbilityActionResult;
import iskallia.vault.util.damage.DamageOverTimeHelper;
import java.util.List;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.Potions;

public class NovaDotAbility extends AbstractNovaAbility<NovaDotConfig> {
   private static final int PARTICLE_COLOR = TextColor.parseColor("#5e8a37").getValue();

   protected AbilityActionResult doAction(NovaDotConfig config, ServerPlayer player, boolean active) {
      List<LivingEntity> targetEntities = this.getTargetEntities(config, player);
      float attackDamage = this.getAttackDamage(config, player);

      for (LivingEntity targetEntity : targetEntities) {
         DamageOverTimeHelper.invalidateAll(targetEntity);
         DamageOverTimeHelper.applyDamageOverTime(
            targetEntity, NovaDotAbility.PlayerDamageOverTimeSource.of(player), attackDamage, config.getDurationSeconds() * 20
         );
      }

      return AbilityActionResult.SUCCESS_COOLDOWN;
   }

   protected void doParticles(NovaDotConfig config, ServerPlayer player) {
      AreaEffectCloud areaEffectCloud = new AreaEffectCloud(player.level, player.getX(), player.getY(), player.getZ());
      areaEffectCloud.setOwner(player);
      areaEffectCloud.setRadius(config.getRadius());
      areaEffectCloud.setRadiusOnUse(-0.5F);
      areaEffectCloud.setWaitTime(0);
      areaEffectCloud.setDuration(4);
      areaEffectCloud.setPotion(Potions.EMPTY);
      areaEffectCloud.setRadiusPerTick(-areaEffectCloud.getRadius() / areaEffectCloud.getDuration());
      areaEffectCloud.setFixedColor(PARTICLE_COLOR);
      areaEffectCloud.setParticle((ParticleOptions)ModParticles.NOVA_DOT.get());
      player.level.addFreshEntity(areaEffectCloud);
   }

   protected void doSound(NovaDotConfig config, ServerPlayer player) {
      player.level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.SPLASH_POTION_BREAK, SoundSource.PLAYERS, 0.2F, 1.0F);
      player.playNotifySound(SoundEvents.SPLASH_POTION_BREAK, SoundSource.PLAYERS, 0.2F, 1.0F);
   }

   public static class PlayerDamageOverTimeSource extends EntityDamageSource {
      private PlayerDamageOverTimeSource(Entity damageSource) {
         super("player", damageSource);
      }

      public static NovaDotAbility.PlayerDamageOverTimeSource of(Player player) {
         return new NovaDotAbility.PlayerDamageOverTimeSource(player);
      }
   }
}
