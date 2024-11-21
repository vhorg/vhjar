package iskallia.vault.entity.entity.elite;

import iskallia.vault.init.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.NotNull;

public class EliteWitherSkeleton extends WitherSkeleton {
   protected int shootTicks = this.random.nextInt(60, 100);

   public EliteWitherSkeleton(EntityType<? extends WitherSkeleton> entityType, Level level) {
      super(entityType, level);
   }

   public void aiStep() {
      super.aiStep();
      if (this.level.isClientSide) {
         this.level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, this.getRandomX(1.0), this.getY() + 2.9F, this.getRandomZ(1.0), 0.0, 0.01, 0.0);
      }
   }

   public boolean doHurtTarget(Entity entity) {
      if (super.doHurtTarget(entity)) {
         if (entity instanceof LivingEntity livingEntity) {
            livingEntity.removeEffect(MobEffects.WITHER);
            livingEntity.addEffect(new MobEffectInstance(ModEffects.CORRUPTION, 300));
         }

         return true;
      } else {
         return false;
      }
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      LivingEntity target = this.getTarget();
      if (target != null) {
         if (this.shootTicks == 0) {
            for (int headIndex = 0; headIndex < 2; headIndex++) {
               BlockPos targetPos = target.eyeBlockPosition();
               float healthRate = this.getHealth() / this.getMaxHealth();
               this.performRangedAttack(headIndex, targetPos.getX(), targetPos.getY(), targetPos.getZ(), healthRate >= 0.5);
            }

            this.shootTicks = this.random.nextInt(60, 100);
         } else {
            this.shootTicks--;
         }
      }
   }

   private double getHeadX(int pHead) {
      if (pHead <= 0) {
         return this.getX();
      } else {
         float f = (this.yBodyRot + 180 * (pHead - 1)) * (float) (Math.PI / 180.0);
         float f1 = Mth.cos(f);
         return this.getX() + f1 * 1.3;
      }
   }

   private double getHeadY(int pHead) {
      return pHead <= 0 ? this.getY() + 3.0 : this.getY() + 2.2;
   }

   private double getHeadZ(int pHead) {
      if (pHead <= 0) {
         return this.getZ();
      } else {
         float f = (this.yBodyRot + 180 * (pHead - 1)) * (float) (Math.PI / 180.0);
         float f1 = Mth.sin(f);
         return this.getZ() + f1 * 1.3;
      }
   }

   public void performRangedAttack(@NotNull LivingEntity pTarget, float pDistanceFactor) {
      this.performRangedAttack(0, pTarget);
   }

   private void performRangedAttack(int pHead, LivingEntity pTarget) {
      this.performRangedAttack(
         pHead, pTarget.getX(), pTarget.getY() + pTarget.getEyeHeight() * 0.5, pTarget.getZ(), pHead == 0 && this.random.nextFloat() < 0.001F
      );
   }

   private void performRangedAttack(int pHead, double pX, double pY, double pZ, boolean pInvulnerable) {
      if (!this.isSilent()) {
         this.level.levelEvent((Player)null, 1024, this.blockPosition(), 0);
      }

      double d0 = this.getHeadX(pHead);
      double d1 = this.getHeadY(pHead);
      double d2 = this.getHeadZ(pHead);
      double d3 = pX - d0;
      double d4 = pY - d1;
      double d5 = pZ - d2;
      EliteWitherSkeleton.EliteWitherSkull projectile = new EliteWitherSkeleton.EliteWitherSkull(this.level, this, d3, d4, d5);
      projectile.setOwner(this);
      if (pInvulnerable) {
         projectile.setDangerous(true);
      }

      projectile.setPosRaw(d0, d1, d2);
      this.level.addFreshEntity(projectile);
   }

   public static class EliteWitherSkull extends WitherSkull {
      public EliteWitherSkull(Level world, LivingEntity p_37610_, double p_37611_, double p_37612_, double p_37613_) {
         super(world, p_37610_, p_37611_, p_37612_, p_37613_);
      }

      protected void onHit(@NotNull HitResult pResult) {
         Type hitresult$type = pResult.getType();
         if (hitresult$type == Type.ENTITY) {
            this.onHitEntity((EntityHitResult)pResult);
         } else if (hitresult$type == Type.BLOCK) {
            this.onHitBlock((BlockHitResult)pResult);
         }

         if (hitresult$type != Type.MISS) {
            this.gameEvent(GameEvent.PROJECTILE_LAND, this.getOwner());
         }

         if (!this.level.isClientSide) {
            this.level.explode(this, this.getX(), this.getY(), this.getZ(), 1.0F, false, BlockInteraction.NONE);
            this.discard();
         }
      }

      protected void onHitEntity(EntityHitResult pResult) {
         if (!this.level.isClientSide) {
            Entity entity = pResult.getEntity();
            boolean wasHurt;
            if (this.getOwner() instanceof LivingEntity livingEntity) {
               wasHurt = entity.hurt(DamageSource.witherSkull(this, livingEntity), 8.0F);
               if (wasHurt) {
                  if (entity.isAlive()) {
                     this.doEnchantDamageEffects(livingEntity, entity);
                  } else {
                     livingEntity.heal(5.0F);
                  }
               }
            } else {
               wasHurt = entity.hurt(DamageSource.MAGIC, 5.0F);
            }

            if (wasHurt && entity instanceof LivingEntity livingEntityx) {
               livingEntityx.addEffect(new MobEffectInstance(ModEffects.CORRUPTION, 300, 1), this.getEffectSource());
            }
         }
      }
   }
}
