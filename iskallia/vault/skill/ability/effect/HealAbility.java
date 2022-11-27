package iskallia.vault.skill.ability.effect;

import iskallia.vault.init.ModParticles;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.config.HealConfig;
import iskallia.vault.skill.ability.effect.spi.AbstractHealAbility;
import iskallia.vault.skill.ability.effect.spi.core.AbilityActionResult;
import java.awt.Color;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.item.alchemy.Potions;

public class HealAbility extends AbstractHealAbility<HealConfig> {
   @Override
   public String getAbilityGroupName() {
      return "Heal";
   }

   protected AbilityActionResult doAction(HealConfig config, ServerPlayer player, boolean active) {
      player.heal(config.getFlatLifeHealed());
      return AbilityActionResult.SUCCESS_COOLDOWN;
   }

   protected void doParticles(HealConfig config, ServerPlayer player) {
      ((ServerLevel)player.level)
         .sendParticles((SimpleParticleType)ModParticles.HEAL.get(), player.getX(), player.getY(), player.getZ(), 25, 1.0, 0.5, 1.0, 0.0);
      AreaEffectCloud areaEffectCloud = new AreaEffectCloud(player.level, player.getX(), player.getY(), player.getZ());
      areaEffectCloud.setOwner(player);
      areaEffectCloud.setRadius(1.5F);
      areaEffectCloud.setRadiusOnUse(-0.5F);
      areaEffectCloud.setWaitTime(0);
      areaEffectCloud.setDuration(4);
      areaEffectCloud.setPotion(Potions.EMPTY);
      areaEffectCloud.setRadiusPerTick(0.0F);
      areaEffectCloud.setFixedColor(Color.RED.getRGB());
      player.level.addFreshEntity(areaEffectCloud);
   }

   protected void doSound(HealConfig config, ServerPlayer player) {
      player.level.playSound(player, player.getX(), player.getY(), player.getZ(), ModSounds.HEAL, SoundSource.PLAYERS, 0.5F, 1.0F);
      player.playNotifySound(ModSounds.HEAL, SoundSource.PLAYERS, 0.5F, 1.0F);
   }
}
