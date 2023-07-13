package iskallia.vault.skill.ability.effect;

import iskallia.vault.event.ActiveFlags;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.NovaParticleMessage;
import iskallia.vault.skill.ability.effect.spi.AbstractNovaAbility;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.base.SkillContext;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

public class NovaAbility extends AbstractNovaAbility {
   public NovaAbility(
      int unlockLevel,
      int learnPointCost,
      int regretPointCost,
      int cooldownTicks,
      float manaCost,
      float radius,
      float percentAbilityPowerDealt,
      float knockbackStrengthMultiplier
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost, radius, percentAbilityPowerDealt, knockbackStrengthMultiplier);
   }

   public NovaAbility() {
   }

   @Override
   protected Ability.ActionResult doAction(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> {
         Vec3 pos = context.getSource().getPos().orElse(player.position());
         List<LivingEntity> targetEntities = this.getTargetEntities(player.level, player, pos);
         float attackDamage = this.getAbilityPower(player);
         DamageSource damageSource = DamageSource.playerAttack(player);

         for (LivingEntity entity : targetEntities) {
            float damageModifier = this.getDamageModifier(this.getRadius(player), player.distanceTo(entity));
            ActiveFlags.IS_AP_ATTACKING.runIfNotSet(() -> ActiveFlags.IS_AOE_ATTACKING.runIfNotSet(() -> {
               if (entity.hurt(damageSource, attackDamage * damageModifier) && !Mth.equal(this.getKnockbackStrengthMultiplier(), 0.0F)) {
                  double dx = pos.x - entity.getX();
                  double dz = pos.z - entity.getZ();
                  if (dx * dx + dz * dz < 1.0E-4) {
                     dx = (Math.random() - Math.random()) * 0.01;
                     dz = (Math.random() - Math.random()) * 0.01;
                  }

                  entity.knockback(0.4F * this.getKnockbackStrengthMultiplier(), dx, dz);
               }
            }));
         }

         return Ability.ActionResult.successCooldownImmediate();
      }).orElse(Ability.ActionResult.fail());
   }

   protected float getDamageModifier(float radius, float dist) {
      if (dist >= 0.0F && dist < radius / 5.0F * 1.0F) {
         return 1.0F;
      } else if (dist >= radius / 5.0F * 1.0F && dist < radius / 5.0F * 2.0F) {
         return 0.8F;
      } else if (dist >= radius / 5.0F * 2.0F && dist < radius / 5.0F * 3.0F) {
         return 0.6F;
      } else if (dist >= radius / 5.0F * 3.0F && dist < radius / 5.0F * 4.0F) {
         return 0.4F;
      } else {
         return dist >= radius / 5.0F * 4.0F ? 0.2F : 0.2F;
      }
   }

   @Override
   protected void doParticles(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(player -> {
         Vec3 pos = context.getSource().getPos().orElse(player.position());
         float radius = this.getRadius(player);
         ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new NovaParticleMessage(new Vec3(pos.x, pos.y + 0.15F, pos.z), radius));
      });
   }

   @Override
   protected void doSound(SkillContext context) {
      context.getSource().as(ServerPlayer.class).ifPresent(player -> {
         Vec3 pos = context.getSource().getPos().orElse(player.position());
         player.level.playSound(player, pos.x, pos.y, pos.z, SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 0.2F, 1.0F);
         player.playNotifySound(SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 0.2F, 1.0F);
      });
   }
}
