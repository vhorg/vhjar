package iskallia.vault.entity.boss.attack;

import iskallia.vault.entity.boss.VaultBossBaseEntity;
import java.util.Optional;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class BasicMeleeAttack implements IMeleeAttack {
   private final double damageMultiplier;
   private final BasicMeleeAttack.BasicMeleeAttackAttributes attackData;
   private int damageCooldown = 0;
   private int targetId = -1;
   private final VaultBossBaseEntity boss;

   public BasicMeleeAttack(VaultBossBaseEntity boss, double damageMultiplier, BasicMeleeAttack.BasicMeleeAttackAttributes attackData) {
      this.boss = boss;
      this.damageMultiplier = damageMultiplier;
      this.attackData = attackData;
   }

   @Override
   public boolean start(LivingEntity target, double distToTarget, double reach) {
      if (this.targetId == -1) {
         if (this.canAttack(distToTarget, reach, this.attackData, this.boss.getTarget())) {
            this.targetId = target.getId();
            this.damageCooldown = this.attackData.damageCooldown();
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean canAttack(double distToEnemy, double reach, BasicMeleeAttack.BasicMeleeAttackAttributes attackData, LivingEntity target) {
      return distToEnemy <= reach
         && this.isWithinAttackableSlice(this.boss, target, attackData.attackableSlice().closenessRatioRequired(), attackData.attackableSlice().angleOffset());
   }

   public boolean isWithinAttackableSlice(LivingEntity target) {
      return this.isWithinAttackableSlice(
         this.boss, target, this.attackData.attackableSlice().closenessRatioRequired(), this.attackData.attackableSlice().angleOffset()
      );
   }

   @Override
   public void stop() {
      this.targetId = -1;
   }

   @Override
   public void tick(double reach) {
      if (this.targetId > -1 && this.boss.getLevel().getEntity(this.targetId) instanceof LivingEntity target) {
         if (--this.damageCooldown <= 0) {
            this.boss
               .getLevel()
               .getNearbyEntities(LivingEntity.class, ENTITIES_HIT_TARGETING_CONDITIONS, this.boss, this.boss.getBoundingBox().inflate(reach))
               .forEach(entity -> {
                  if (this.isWithinAttackableSlice(entity)) {
                     double pDistToEnemySqr = this.boss.distanceToSqr(entity);
                     if (pDistToEnemySqr <= reach) {
                        this.doHurtTarget(entity);
                        this.targetId = -1;
                     }
                  }
               });
         } else if (this.damageCooldown > 0 && !this.isWithinAttackableSlice(target)) {
            this.rotateTowardTarget(target);
         }
      }
   }

   protected void rotateTowardTarget(LivingEntity target) {
      if (target != null) {
         double xDiff = target.getX() - this.boss.getX();
         double zDiff = target.getZ() - this.boss.getZ();
         if (!(Math.abs(zDiff) <= 1.0E-5F) || !(Math.abs(xDiff) <= 1.0E-5F)) {
            float targetAtAngle = (float)(Mth.atan2(zDiff, xDiff) * 180.0F / (float)Math.PI) - 90.0F;
            float maxDelta = 15.0F;
            float angleDiff = Mth.degreesDifference(this.boss.getYRot(), targetAtAngle);
            float rotateBy = Mth.clamp(angleDiff, -maxDelta, maxDelta);
            float newYRot = this.boss.getYRot() + rotateBy;
            this.boss.setYRot(newYRot);
            this.boss.setYBodyRot(newYRot);
            this.boss.setOldPosAndRot();
            this.boss.hasImpulse = true;
         }
      }
   }

   public boolean doHurtTarget(Entity target) {
      double baseDamage = this.boss.getAttributeValue(Attributes.ATTACK_DAMAGE);
      boolean flag = target.hurt(DamageSource.mobAttack(this.boss), (float)(baseDamage * this.damageMultiplier));
      if (flag) {
         if (target instanceof LivingEntity livingTarget) {
            this.knockbackTarget(this.boss, livingTarget, this.attackData.horizontalKnockbackMultiplier(), this.attackData.verticalKnockbackMultiplier());
         }

         this.boss.setLastHurtMob(target);
      }

      this.boss.playAttackSound();
      return flag;
   }

   @Override
   public int getDuration() {
      return this.attackData.swingDuration();
   }

   @Override
   public Optional<String> getAttackMove() {
      return this.attackData == null ? Optional.empty() : Optional.of(this.attackData.attackMoveName());
   }

   public record BasicMeleeAttackAttributes(
      BasicMeleeAttack.BasicMeleeAttackAttributes.Slice attackableSlice,
      int swingDuration,
      int damageCooldown,
      String attackMoveName,
      float horizontalKnockbackMultiplier,
      float verticalKnockbackMultiplier
   ) {
      public record Slice(float angleOffset, float closenessRatioRequired) {
      }
   }
}
