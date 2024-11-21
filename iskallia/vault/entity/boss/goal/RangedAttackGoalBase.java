package iskallia.vault.entity.boss.goal;

import iskallia.vault.entity.boss.VaultBossBaseEntity;
import iskallia.vault.entity.boss.VaultBossEntity;
import iskallia.vault.entity.boss.trait.ITrait;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;

public abstract class RangedAttackGoalBase extends Goal implements ITrait {
   protected final VaultBossBaseEntity boss;
   @Nullable
   protected LivingEntity target;
   private int attackTime = -1;
   private int preAttackAnimationCooldown = -1;
   private double speedModifier;
   private int seeTime;
   private int attackIntervalMin;
   private int attackIntervalMax;
   private int preAttackAnimationDuration = 0;
   private float attackRadius;
   private boolean attackWhenInMeleeReach;
   private boolean hasRunningNavigation = false;
   private int stackSize = 1;

   public RangedAttackGoalBase(VaultBossBaseEntity boss, double speedModifier, int attackInterval, float attackRadius, boolean attackWhenInMeleeReach) {
      this(boss, speedModifier, attackInterval, attackInterval, attackRadius, attackWhenInMeleeReach);
   }

   public RangedAttackGoalBase(
      VaultBossBaseEntity boss, double speedModifier, int attackIntervalMin, int attackIntervalMax, float attackRadius, boolean attackWhenInMeleeReach
   ) {
      this.boss = boss;
      this.setAttackAttributes(speedModifier, attackIntervalMin, attackIntervalMax, attackRadius, attackWhenInMeleeReach);
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
   }

   protected void setPreAttackAnimationCooldown(int preAttackAnimationDuration) {
      this.preAttackAnimationDuration = preAttackAnimationDuration;
   }

   protected void setAttackAttributes(double speedModifier, int attackIntervalMin, int attackIntervalMax, float attackRadius, boolean attackWhenInMeleeReach) {
      this.speedModifier = speedModifier;
      this.attackIntervalMin = attackIntervalMin;
      this.attackIntervalMax = attackIntervalMax;
      this.attackRadius = attackRadius;
      this.attackWhenInMeleeReach = attackWhenInMeleeReach;
   }

   public boolean canUse() {
      LivingEntity potentialTarget = this.boss.getTarget();
      if (potentialTarget != null && potentialTarget.isAlive() && this.shouldAttackInOrIsOutOfMeleeRange(potentialTarget)) {
         this.target = potentialTarget;
         return true;
      } else {
         return false;
      }
   }

   private boolean shouldAttackInOrIsOutOfMeleeRange(LivingEntity target) {
      return this.attackWhenInMeleeReach || this.boss.distanceTo(target) > this.boss.getAttackReach();
   }

   public boolean canContinueToUse() {
      return this.canUse() || !this.boss.getNavigation().isDone();
   }

   public void stop() {
      this.target = null;
      this.seeTime = 0;
      this.attackTime = -1;
      this.preAttackAnimationCooldown = -1;
   }

   public boolean requiresUpdateEveryTick() {
      return true;
   }

   public void tick() {
      double distanceSqr = this.boss.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
      boolean hasLineOfSight = this.boss.getSensing().hasLineOfSight(this.target);
      if (hasLineOfSight) {
         this.seeTime++;
      } else {
         this.seeTime = 0;
      }

      if (this.hasRunningNavigation && !(distanceSqr > (double)this.attackRadius * this.attackRadius) && this.seeTime >= 5) {
         this.boss.getNavigation().stop();
         this.hasRunningNavigation = false;
      } else {
         this.boss.getNavigation().moveTo(this.target, this.speedModifier);
         this.hasRunningNavigation = true;
      }

      this.boss.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
      if (--this.attackTime == 0) {
         this.preAttackAnimationCooldown = this.preAttackAnimationDuration;
         this.playPreAttackAnimation();
      } else if (this.attackTime > 0) {
         return;
      }

      if (this.preAttackAnimationCooldown-- == 0) {
         if (!hasLineOfSight) {
            return;
         }

         float normalizedDistance = (float)Math.sqrt(distanceSqr) / this.attackRadius;
         this.performRangedAttack(this.target, Mth.clamp(normalizedDistance, 0.1F, 1.0F));
         this.attackTime = Mth.floor(normalizedDistance * (this.attackIntervalMax - this.attackIntervalMin) + this.attackIntervalMin);
         this.preAttackAnimationCooldown = this.preAttackAnimationDuration;
      } else if (this.preAttackAnimationCooldown < 0) {
         this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(distanceSqr) / this.attackRadius, this.attackIntervalMin, this.attackIntervalMax));
         this.preAttackAnimationCooldown = this.preAttackAnimationDuration;
      }
   }

   protected void playPreAttackAnimation() {
   }

   protected abstract void performRangedAttack(LivingEntity var1, float var2);

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putFloat("SpeedModifier", (float)this.speedModifier);
      nbt.putInt("AttackIntervalMin", this.attackIntervalMin);
      nbt.putInt("AttackIntervalMax", this.attackIntervalMax);
      nbt.putFloat("AttackRadius", this.attackRadius);
      nbt.putBoolean("AttackWhenInMeleeReach", this.attackWhenInMeleeReach);
      nbt.putInt("StackSize", this.stackSize);
      return nbt;
   }

   @Override
   public void deserializeNBT(CompoundTag nbt, VaultBossBaseEntity boss) {
      this.setAttackAttributes(
         nbt.getFloat("SpeedModifier"),
         nbt.getInt("AttackIntervalMin"),
         nbt.getInt("AttackIntervalMax"),
         nbt.getFloat("AttackRadius"),
         nbt.getBoolean("AttackWhenInMeleeReach")
      );
      this.stackSize = nbt.getInt("StackSize");
   }

   @Override
   public void apply(VaultBossEntity boss) {
      boss.addTraitGoal(this);
   }

   @Override
   public void addStack(ITrait trait) {
      if (trait instanceof RangedAttackGoalBase rangedAttackGoal) {
         this.stackSize++;
         this.attackIntervalMin = rangedAttackGoal.attackIntervalMin / this.stackSize;
         this.attackIntervalMax = rangedAttackGoal.attackIntervalMax / this.stackSize;
      }
   }
}
