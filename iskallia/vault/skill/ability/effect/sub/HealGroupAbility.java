package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.init.ModParticles;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.config.sub.HealGroupConfig;
import iskallia.vault.skill.ability.effect.spi.AbstractHealAbility;
import iskallia.vault.skill.ability.effect.spi.core.AbilityActionResult;
import java.awt.Color;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.Potions;

public class HealGroupAbility extends AbstractHealAbility<HealGroupConfig> {
   protected AbilityActionResult doAction(HealGroupConfig config, ServerPlayer player, boolean active) {
      for (LivingEntity nearbyEntity : player.level
         .getNearbyEntities(
            LivingEntity.class,
            TargetingConditions.forNonCombat().selector(entity -> entity instanceof Player).range(config.getRadius()),
            player,
            player.getBoundingBox().inflate(config.getRadius())
         )) {
         nearbyEntity.heal(config.getFlatLifeHealed());
      }

      player.heal(config.getFlatLifeHealed());
      return AbilityActionResult.SUCCESS_COOLDOWN;
   }

   protected void doParticles(HealGroupConfig config, ServerPlayer player) {
      ((ServerLevel)player.level)
         .sendParticles(
            (SimpleParticleType)ModParticles.HEAL.get(),
            player.getX(),
            player.getY(),
            player.getZ(),
            25,
            config.getRadius() * 0.5,
            0.5,
            config.getRadius() * 0.5,
            0.0
         );
      AreaEffectCloud areaEffectCloud = new AreaEffectCloud(player.level, player.getX(), player.getY(), player.getZ());
      areaEffectCloud.setOwner(player);
      areaEffectCloud.setRadius(config.getRadius());
      areaEffectCloud.setRadiusOnUse(-0.5F);
      areaEffectCloud.setWaitTime(0);
      areaEffectCloud.setDuration(4);
      areaEffectCloud.setPotion(Potions.EMPTY);
      areaEffectCloud.setRadiusPerTick(-areaEffectCloud.getRadius() / areaEffectCloud.getDuration());
      areaEffectCloud.setFixedColor(Color.RED.getRGB());
      player.level.addFreshEntity(areaEffectCloud);
   }

   protected void doSound(HealGroupConfig config, ServerPlayer player) {
      player.level.playSound(player, player.getX(), player.getY(), player.getZ(), ModSounds.HEAL, SoundSource.PLAYERS, 0.5F, 1.0F);
      player.playNotifySound(ModSounds.HEAL, SoundSource.PLAYERS, 0.5F, 1.0F);
   }
}
