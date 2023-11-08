package iskallia.vault.entity.boss;

import iskallia.vault.init.ModSounds;
import java.util.Optional;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;

public class BasicMeleeAttack implements IMeleeAttack {
   private final double damageMultiplier;
   private final BasicMeleeAttack.BasicMeleeAttackAttributes attackData;
   private int damageCooldown = 0;
   private int targetId = -1;
   private final ArtifactBossEntity boss;

   public BasicMeleeAttack(ArtifactBossEntity boss, double damageMultiplier, BasicMeleeAttack.BasicMeleeAttackAttributes attackData) {
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
      return distToEnemy <= reach && this.isWithinAttackableSlice(attackData, target);
   }

   public boolean isWithinAttackableSlice(LivingEntity target) {
      return this.isWithinAttackableSlice(this.attackData, target);
   }

   public boolean isWithinAttackableSlice(BasicMeleeAttack.BasicMeleeAttackAttributes attackData, LivingEntity target) {
      if (target == null) {
         return false;
      } else {
         Vec3 bossViewVector = this.boss.calculateViewVector(this.boss.getViewYRot(1.0F) + attackData.attackableSlice().angleOffset()).normalize();
         Vec3 positionsVector = new Vec3(target.getX() - this.boss.getX(), target.getEyeY() - this.boss.getEyeY(), target.getZ() - this.boss.getZ());
         positionsVector = positionsVector.normalize();
         double closenessRatio = bossViewVector.dot(positionsVector);
         return closenessRatio > attackData.attackableSlice().closenessRatioRequired();
      }
   }

   @Override
   public void stop() {
      this.targetId = -1;
   }

   @Override
   public void tick(double reach) {
      if (this.targetId > -1 && this.boss.getLevel().getEntity(this.targetId) instanceof LivingEntity target) {
         if (--this.damageCooldown <= 0) {
            this.boss.getLevel().getNearbyPlayers(PLAYERS_HIT_TARGETING_CONDITIONS, this.boss, this.boss.getBoundingBox().inflate(reach)).forEach(player -> {
               if (this.isWithinAttackableSlice(player)) {
                  double pDistToEnemySqr = this.boss.distanceToSqr(player);
                  if (pDistToEnemySqr <= reach) {
                     this.doHurtTarget(player);
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
            float maxDelta = 1.0F;
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
      double baseKnockback = this.boss.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
      boolean flag = target.hurt(DamageSource.mobAttack(this.boss), (float)(baseDamage * this.damageMultiplier));
      if (flag) {
         if (target instanceof LivingEntity livingTarget) {
            float ratioX = Mth.sin(this.boss.getYRot() * (float) (Math.PI / 180.0));
            float ratioZ = -Mth.cos(this.boss.getYRot() * (float) (Math.PI / 180.0));
            this.knockbackTarget(
               livingTarget, baseKnockback, this.attackData.horizontalKnockbackMultiplier(), ratioX, ratioZ, this.attackData.verticalKnockbackMultiplier()
            );
         }

         this.boss.setLastHurtMob(target);
      }

      this.boss.level.playSound(null, this.boss.blockPosition(), ModSounds.ARTIFACT_BOSS_ATTACK, SoundSource.HOSTILE, 1.0F, 1.0F);
      return flag;
   }

   private void knockbackTarget(
      LivingEntity target, double strength, double horizontalKnockbackMultiplier, double ratioX, double ratioZ, double verticalKnockbackMultiplier
   ) {
      LivingKnockBackEvent event = ForgeHooks.onLivingKnockBack(target, (float)strength, ratioX, ratioZ);
      if (!event.isCanceled()) {
         strength = event.getStrength();
         ratioX = event.getRatioX() * horizontalKnockbackMultiplier;
         ratioZ = event.getRatioZ() * horizontalKnockbackMultiplier;
         strength *= 1.0 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE) / 2.0;
         if (strength > 0.0) {
            target.hasImpulse = true;
            Vec3 vec3 = target.getDeltaMovement();
            Vec3 vec31 = new Vec3(ratioX, 0.0, ratioZ).normalize().scale(strength);
            target.setDeltaMovement(vec3.x / 2.0 - vec31.x, vec3.y / 2.0 + strength * verticalKnockbackMultiplier, vec3.z / 2.0 - vec31.z);
         }
      }
   }

   @Override
   public int getDuration() {
      return this.attackData.swingDuration();
   }

   @Override
   public Optional<ArtifactBossEntity.AttackMove> getAttackMove() {
      return this.attackData == null ? Optional.empty() : Optional.of(this.attackData.attackMove());
   }

   public record BasicMeleeAttackAttributes(
      BasicMeleeAttack.BasicMeleeAttackAttributes.Slice attackableSlice,
      int swingDuration,
      int damageCooldown,
      ArtifactBossEntity.AttackMove attackMove,
      float horizontalKnockbackMultiplier,
      float verticalKnockbackMultiplier
   ) {
      public record Slice(float angleOffset, float closenessRatioRequired) {
      }
   }
}
