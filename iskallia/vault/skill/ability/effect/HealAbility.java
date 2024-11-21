package iskallia.vault.skill.ability.effect;

import iskallia.vault.init.ModParticles;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.effect.spi.AbstractHealAbility;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.base.SkillContext;
import java.awt.Color;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.item.alchemy.Potions;

public class HealAbility extends AbstractHealAbility {
   public HealAbility(int unlockLevel, int learnPointCost, int regretPointCost, int cooldownTicks, float manaCost) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost);
   }

   public HealAbility() {
   }

   @Override
   protected Ability.ActionResult doAction(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         float healed = this.getFlatLifeHealed();
         player.heal(healed);
         return Ability.ActionResult.successCooldownImmediate();
      }).orElse(Ability.ActionResult.fail());
   }

   @Override
   protected void doParticles(SkillContext context) {
      context.getSource()
         .as(ServerPlayer.class)
         .ifPresent(
            player -> {
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
         );
   }

   @Override
   protected void doSound(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(player -> {
         player.level.playSound(player, player.getX(), player.getY(), player.getZ(), ModSounds.HEAL, SoundSource.PLAYERS, 0.5F, 1.0F);
         player.playNotifySound(ModSounds.HEAL, SoundSource.PLAYERS, 0.5F, 1.0F);
      });
   }
}
