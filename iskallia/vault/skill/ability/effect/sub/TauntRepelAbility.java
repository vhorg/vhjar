package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.entity.VaultBoss;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.ability.config.sub.TauntRepelConfig;
import iskallia.vault.skill.ability.effect.spi.AbstractTauntAbility;
import iskallia.vault.skill.ability.effect.spi.core.AbilityActionResult;
import java.awt.Color;
import java.util.EnumSet;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.phys.Vec3;

public class TauntRepelAbility extends AbstractTauntAbility<TauntRepelConfig> {
   protected static final Predicate<LivingEntity> MONSTER_PREDICATE = entity -> entity.getType().getCategory() == MobCategory.MONSTER
      && !(entity instanceof VaultBoss);

   protected AbilityActionResult doAction(TauntRepelConfig config, ServerPlayer player, boolean active) {
      player.removeEffect(ModEffects.TAUNT_REPEL_PLAYER);
      player.addEffect(new MobEffectInstance(ModEffects.TAUNT_REPEL_PLAYER, config.getDurationTicks(), 0, false, false, true));

      for (Mob mob : player.level
         .getNearbyEntities(
            Mob.class,
            TargetingConditions.forCombat().range(config.getRadius()).selector(MONSTER_PREDICATE),
            player,
            player.getBoundingBox().inflate(config.getRadius())
         )) {
         if (player.hasLineOfSight(mob)) {
            mob.setTarget(null);
            mob.removeEffect(ModEffects.TAUNT_REPEL_MOB);
            mob.addEffect(new MobEffectInstance(ModEffects.TAUNT_REPEL_MOB, config.getDurationTicks(), 0, false, true, false));
            Vec3 force = mob.position().subtract(player.position()).normalize().scale(config.getRepelForce());
            mob.push(force.x, force.y, force.z);
            mob.goalSelector.getRunningGoals().forEach(WrappedGoal::stop);
            mob.getNavigation().stop();
         }
      }

      return AbilityActionResult.SUCCESS_COOLDOWN;
   }

   protected void doParticles(TauntRepelConfig config, ServerPlayer player) {
      int particleCount = (int)Mth.clamp(Math.pow(config.getRadius(), 2.0) * (float) Math.PI * 100.0, 50.0, 400.0);
      ((ServerLevel)player.level)
         .sendParticles(
            ParticleTypes.SMOKE, player.getX(), player.getY(), player.getZ(), particleCount / 2, config.getRadius() * 0.5, 0.5, config.getRadius() * 0.5, 0.0
         );
      ((ServerLevel)player.level)
         .sendParticles(
            ParticleTypes.SOUL, player.getX(), player.getY(), player.getZ(), particleCount / 2, config.getRadius() * 0.5, 0.5, config.getRadius() * 0.5, 0.0
         );
      AreaEffectCloud areaEffectCloud = new AreaEffectCloud(player.level, player.getX(), player.getY(), player.getZ());
      areaEffectCloud.setOwner(player);
      areaEffectCloud.setRadius(config.getRadius());
      areaEffectCloud.setRadiusOnUse(-0.5F);
      areaEffectCloud.setWaitTime(0);
      areaEffectCloud.setDuration(4);
      areaEffectCloud.setPotion(Potions.EMPTY);
      areaEffectCloud.setRadiusPerTick(-areaEffectCloud.getRadius() / areaEffectCloud.getDuration());
      areaEffectCloud.setFixedColor(Color.BLACK.getRGB());
      areaEffectCloud.setParticle(ParticleTypes.SMOKE);
      player.level.addFreshEntity(areaEffectCloud);
   }

   protected void doSound(TauntRepelConfig config, ServerPlayer player) {
      player.level.playSound(player, player.getX(), player.getY(), player.getZ(), ModSounds.TAUNT_REPEL, SoundSource.PLAYERS, 1.0F, 1.0F);
      player.playNotifySound(ModSounds.TAUNT_REPEL, SoundSource.PLAYERS, 1.0F, 1.0F);
   }

   public static class FearGoal extends Goal {
      private static final int RANGE = 1024;
      private static final int RADIUS = 10;
      private static final int VERTICAL_DISTANCE = 4;
      protected final PathfinderMob mob;
      protected final double speedModifier;
      protected double posX;
      protected double posY;
      protected double posZ;

      public FearGoal(PathfinderMob mob, double speedModifier) {
         this.mob = mob;
         this.speedModifier = speedModifier;
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         if (this.mob.getDeltaMovement().lengthSqr() > 2.0) {
            return false;
         } else {
            Player nearestPlayer = this.mob
               .level
               .getNearestPlayer(TargetingConditions.forNonCombat().range(1024.0).selector(entity -> entity.hasEffect(ModEffects.TAUNT_REPEL_PLAYER)), this.mob);
            Vec3 randomPos = null;

            for (int i = 0; i < 10; i++) {
               if (nearestPlayer == null) {
                  randomPos = DefaultRandomPos.getPos(this.mob, 10, 4);
               } else {
                  randomPos = DefaultRandomPos.getPosAway(this.mob, 10, 4, nearestPlayer.position());
               }

               if (randomPos != null) {
                  break;
               }
            }

            if (randomPos == null) {
               return false;
            } else {
               this.posX = randomPos.x;
               this.posY = randomPos.y;
               this.posZ = randomPos.z;
               return true;
            }
         }
      }

      public void start() {
         this.mob.getNavigation().moveTo(this.posX, this.posY, this.posZ, this.speedModifier);
      }

      public boolean canContinueToUse() {
         if (this.mob.getNavigation().isDone() && this.canUse()) {
            this.start();
         }

         return true;
      }

      public boolean isInterruptable() {
         return false;
      }
   }

   public static class TauntRepelMobEffect extends MobEffect {
      private static final double SPEED_MODIFIER = 1.25;

      public TauntRepelMobEffect(int color, ResourceLocation resourceLocation) {
         super(MobEffectCategory.NEUTRAL, color);
         this.setRegistryName(resourceLocation);
      }

      public boolean isDurationEffectTick(int duration, int amplifier) {
         return true;
      }

      public void applyEffectTick(@Nonnull LivingEntity livingEntity, int amplifier) {
         if (livingEntity instanceof Mob mob) {
            mob.goalSelector
               .getRunningGoals()
               .filter(wrappedGoalx -> !(wrappedGoalx.getGoal() instanceof TauntRepelAbility.FearGoal))
               .forEach(WrappedGoal::stop);
            mob.setTarget(null);

            for (WrappedGoal wrappedGoal : mob.goalSelector.getAvailableGoals()) {
               if (wrappedGoal.getGoal() instanceof TauntRepelAbility.FearGoal fearGoal && !wrappedGoal.isRunning() && fearGoal.canUse()) {
                  wrappedGoal.start();
                  break;
               }
            }
         }
      }

      @ParametersAreNonnullByDefault
      public void addAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int amplifier) {
         super.addAttributeModifiers(livingEntity, attributeMap, amplifier);
         if (livingEntity instanceof PathfinderMob mob) {
            mob.goalSelector.addGoal(0, new TauntRepelAbility.FearGoal(mob, 1.25));
         }
      }

      @ParametersAreNonnullByDefault
      public void removeAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int amplifier) {
         super.removeAttributeModifiers(livingEntity, attributeMap, amplifier);
         if (livingEntity instanceof PathfinderMob mob) {
            mob.goalSelector
               .getAvailableGoals()
               .stream()
               .filter(wrappedGoal -> wrappedGoal.getGoal() instanceof TauntRepelAbility.FearGoal)
               .forEach(wrappedGoal -> {
                  wrappedGoal.stop();
                  mob.goalSelector.removeGoal(wrappedGoal.getGoal());
               });
         }
      }
   }

   public static class TauntRepelPlayerEffect extends MobEffect {
      public TauntRepelPlayerEffect(int color, ResourceLocation resourceLocation) {
         super(MobEffectCategory.NEUTRAL, color);
         this.setRegistryName(resourceLocation);
      }
   }
}
