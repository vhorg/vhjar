package iskallia.vault.entity.boss.attack;

import iskallia.vault.core.util.WeightedList;
import iskallia.vault.entity.boss.VaultBossBaseEntity;
import java.util.EnumSet;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;

public class BossMeleeAttackGoal extends Goal {
   private final VaultBossBaseEntity boss;
   @Nullable
   private IMeleeAttack meleeAttack = null;
   @Nullable
   private IMeleeAttack rageAttack = null;
   private int remainingSwingDuration = 0;
   private Path path;
   private double pathedTargetX;
   private double pathedTargetY;
   private double pathedTargetZ;
   private int ticksUntilNextPathRecalculation;
   private int ticksUntilNextAttack;
   private int ticksUntilNextRageAttack;
   private long lastCanUseCheck;
   private static final int COOLDOWN_BETWEEN_CAN_USE_CHECKS = 20;

   public BossMeleeAttackGoal(VaultBossBaseEntity boss) {
      this.boss = boss;
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
   }

   public boolean canUse() {
      long i = this.boss.level.getGameTime();
      if (i - this.lastCanUseCheck < 20L) {
         return false;
      } else {
         this.lastCanUseCheck = i;
         LivingEntity livingentity = this.boss.getTarget();
         if (livingentity == null) {
            return false;
         } else if (!livingentity.isAlive()) {
            return false;
         } else {
            this.path = this.boss.getNavigation().createPath(livingentity, 0);
            return this.path != null
               ? true
               : this.getAttackReachSqr(livingentity) >= this.boss.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
         }
      }
   }

   public boolean canContinueToUse() {
      LivingEntity target = this.boss.getTarget();
      if (target == null) {
         return false;
      } else {
         return !target.isAlive() ? false : !(target instanceof Player player && (target.isSpectator() || player.isCreative()));
      }
   }

   public void start() {
      this.boss.getNavigation().moveTo(this.path, 1.0);
      this.ticksUntilNextPathRecalculation = 0;
      this.boss.setAggressive(true);
      this.ticksUntilNextAttack = 0;
   }

   public void stop() {
      LivingEntity livingentity = this.boss.getTarget();
      if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingentity)) {
         this.boss.setTarget(null);
      }

      this.boss.setAggressive(false);
      this.boss.getNavigation().stop();
      this.resetMeleeAttackValues();
   }

   public boolean requiresUpdateEveryTick() {
      return true;
   }

   private double getAttackReachSqr(LivingEntity target) {
      return this.boss.getAttackReach();
   }

   private double getHitReachSqr(LivingEntity target) {
      double hitReach = this.boss.getAttackReach() + 1.0;
      return hitReach * hitReach + target.getBbWidth();
   }

   public void tick() {
      if (this.remainingSwingDuration > 0) {
         if (this.boss.getTarget() != null) {
            if (this.rageAttack != null) {
               this.rageAttack.tick(this.getHitReachSqr(this.boss.getTarget()));
            } else if (this.meleeAttack != null) {
               this.meleeAttack.tick(this.getHitReachSqr(this.boss.getTarget()));
            }
         }

         this.remainingSwingDuration--;
         if (this.remainingSwingDuration <= 0) {
            this.resetMeleeAttackValues();
         }
      } else {
         this.moveAndTryStartAttack();
      }
   }

   private void resetMeleeAttackValues() {
      if (this.rageAttack != null) {
         this.rageAttack.stop();
         this.rageAttack = null;
         this.resetRageAttackCooldown();
      } else if (this.meleeAttack != null) {
         this.meleeAttack.stop();
         this.meleeAttack = null;
         this.resetAttackCooldown();
      }

      this.boss.setActiveAttackMove(null);
   }

   private void moveAndTryStartAttack() {
      LivingEntity target = this.boss.getTarget();
      if (target != null) {
         this.boss.getLookControl().setLookAt(target, 30.0F, 30.0F);
         double distToTarget = this.boss.distanceToSqr(target.getX(), target.getY(), target.getZ());
         this.moveTowardsTarget(target, distToTarget);
         this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
         this.ticksUntilNextRageAttack = Math.max(this.ticksUntilNextRageAttack - 1, 0);
         if (this.tryStartAttack(target, distToTarget, this.getAttackReachSqr(target))) {
            this.boss.getNavigation().stop();
         }
      }
   }

   private boolean tryStartAttack(LivingEntity target, double distToTarget, double reach) {
      if (this.remainingSwingDuration <= 0) {
         return this.isTimeToRageAttack() && this.getAndInitRandomAttack(this.getRageAttacks()).map(attack -> {
            this.rageAttack = attack;
            if (this.rageAttack.start(target, distToTarget, reach)) {
               this.remainingSwingDuration = this.rageAttack.getDuration();
               this.rageAttack.getAttackMove().ifPresent(this.boss::setActiveAttackMove);
               return true;
            } else {
               return false;
            }
         }).orElse(false) ? true : this.isTimeToAttack() && this.getAndInitRandomAttack(this.getMeleeAttacks()).map(attack -> {
            this.meleeAttack = attack;
            if (this.meleeAttack.start(target, distToTarget, reach)) {
               this.remainingSwingDuration = this.meleeAttack.getDuration();
               this.meleeAttack.getAttackMove().ifPresent(this.boss::setActiveAttackMove);
               return true;
            } else {
               return false;
            }
         }).orElse(false);
      } else {
         return false;
      }
   }

   protected WeightedList<MeleeAttacks.AttackData> getMeleeAttacks() {
      return this.boss.getMeleeAttacks();
   }

   protected WeightedList<MeleeAttacks.AttackData> getRageAttacks() {
      return this.boss.getRageAttacks();
   }

   private Optional<IMeleeAttack> getAndInitRandomAttack(WeightedList<MeleeAttacks.AttackData> attacks) {
      return attacks.getRandom(this.boss.getRandom())
         .flatMap(
            attackData -> MeleeAttacks.MELEE_ATTACK_FACTORIES.containsKey(attackData.name())
               ? Optional.of(MeleeAttacks.MELEE_ATTACK_FACTORIES.get(attackData.name()).apply(this.boss, attackData.multiplier()))
               : Optional.empty()
         );
   }

   private void moveTowardsTarget(LivingEntity target, double distToTarget) {
      this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
      if (this.boss.getSensing().hasLineOfSight(target)
         && this.ticksUntilNextPathRecalculation <= 0
         && (
            this.pathedTargetX == 0.0 && this.pathedTargetY == 0.0 && this.pathedTargetZ == 0.0
               || target.distanceToSqr(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= this.getAttackReachSqr(target) - 1.0
         )) {
         this.pathedTargetX = target.getX();
         this.pathedTargetY = target.getY();
         this.pathedTargetZ = target.getZ();
         this.ticksUntilNextPathRecalculation = 4 + this.boss.getRandom().nextInt(7);
         if (distToTarget > 1024.0) {
            this.ticksUntilNextPathRecalculation += 10;
         } else if (distToTarget > 256.0) {
            this.ticksUntilNextPathRecalculation = (int)(this.ticksUntilNextPathRecalculation + this.boss.getAttackReach());
         }

         if (!this.boss.getNavigation().moveTo(target, 1.0)) {
            this.ticksUntilNextPathRecalculation += 15;
         }
      }
   }

   private void resetAttackCooldown() {
      this.ticksUntilNextAttack = 10;
   }

   private void resetRageAttackCooldown() {
      this.ticksUntilNextRageAttack = (int)(20.0 * this.boss.getAttackReach());
   }

   private boolean isTimeToAttack() {
      return this.ticksUntilNextAttack <= 0;
   }

   private boolean isTimeToRageAttack() {
      return this.ticksUntilNextRageAttack <= 0
         && this.boss.getLevel().getGameTime() - 20L < this.boss.lastDamageStamp
         && !this.boss
            .getLevel()
            .getNearbyPlayers(IMeleeAttack.PLAYERS_CLOSE_TARGETING_CONDITIONS, this.boss, this.boss.getBoundingBox().inflate(this.boss.getAttackReach()))
            .isEmpty();
   }
}
