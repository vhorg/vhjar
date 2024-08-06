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
   private LivingEntity target;
   private int attackTime = -1;
   private double speedModifier;
   private int seeTime;
   private int attackIntervalMin;
   private int attackIntervalMax;
   private float attackRadius;
   private float attackRadiusSqr;

   public RangedAttackGoalBase(VaultBossBaseEntity boss, double speedModifier, int attackInterval, float attackRadius) {
      this(boss, speedModifier, attackInterval, attackInterval, attackRadius);
   }

   public RangedAttackGoalBase(VaultBossBaseEntity boss, double speedModifier, int attackIntervalMin, int attackIntervalMax, float attackRadius) {
      this.boss = boss;
      this.setAttackAttributes(speedModifier, attackIntervalMin, attackIntervalMax, attackRadius);
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
   }

   protected void setAttackAttributes(double speedModifier, int attackIntervalMin, int attackIntervalMax, float attackRadius) {
      this.speedModifier = speedModifier;
      this.attackIntervalMin = attackIntervalMin;
      this.attackIntervalMax = attackIntervalMax;
      this.attackRadius = attackRadius;
      this.attackRadiusSqr = attackRadius * attackRadius;
   }

   public boolean canUse() {
      LivingEntity livingentity = this.boss.getTarget();
      if (livingentity != null && livingentity.isAlive()) {
         this.target = livingentity;
         return true;
      } else {
         return false;
      }
   }

   public boolean canContinueToUse() {
      return this.canUse() || !this.boss.getNavigation().isDone();
   }

   public void stop() {
      this.target = null;
      this.seeTime = 0;
      this.attackTime = -1;
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

      if (!(distanceSqr > this.attackRadiusSqr) && this.seeTime >= 5) {
         this.boss.getNavigation().stop();
      } else {
         this.boss.getNavigation().moveTo(this.target, this.speedModifier);
      }

      this.boss.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
      if (--this.attackTime == 0) {
         if (!hasLineOfSight) {
            return;
         }

         float normalizedDistance = (float)Math.sqrt(distanceSqr) / this.attackRadius;
         this.performRangedAttack(this.target, Mth.clamp(normalizedDistance, 0.1F, 1.0F));
         this.attackTime = Mth.floor(normalizedDistance * (this.attackIntervalMax - this.attackIntervalMin) + this.attackIntervalMin);
      } else if (this.attackTime < 0) {
         this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(distanceSqr) / this.attackRadius, this.attackIntervalMin, this.attackIntervalMax));
      }
   }

   protected abstract void performRangedAttack(LivingEntity var1, float var2);

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putFloat("SpeedModifier", (float)this.speedModifier);
      nbt.putInt("AttackIntervalMin", this.attackIntervalMin);
      nbt.putInt("AttackIntervalMax", this.attackIntervalMax);
      nbt.putFloat("AttackRadius", this.attackRadius);
      return nbt;
   }

   @Override
   public void deserializeNBT(CompoundTag nbt) {
      this.setAttackAttributes(nbt.getFloat("SpeedModifier"), nbt.getInt("AttackIntervalMin"), nbt.getInt("AttackIntervalMax"), nbt.getFloat("AttackRadius"));
   }

   @Override
   public void apply(VaultBossEntity boss) {
      boss.addTraitGoal(this);
   }

   @Override
   public void addStack(ITrait trait) {
      if (trait instanceof RangedAttackGoalBase rangedAttackGoal) {
         int stackSize = rangedAttackGoal.attackIntervalMin / this.attackIntervalMin;
         stackSize++;
         this.attackIntervalMin = rangedAttackGoal.attackIntervalMin / stackSize;
         this.attackIntervalMax = rangedAttackGoal.attackIntervalMax / stackSize;
      }
   }
}
