package iskallia.vault.entity.boss.goal;

import iskallia.vault.entity.boss.VaultBossBaseEntity;
import iskallia.vault.entity.boss.VaultBossEntity;
import iskallia.vault.entity.boss.trait.ITrait;
import java.util.EnumSet;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.projectile.ShulkerBullet;

public class ShulkerAttackGoal extends Goal implements ITrait {
   public static final String TYPE = "shulker_bullet";
   private final VaultBossBaseEntity boss;
   private int attackIntervalMax;
   private int attackIntervalMin;
   private int attackTime;
   private int stackSize;

   public ShulkerAttackGoal(VaultBossBaseEntity boss) {
      this.boss = boss;
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
   }

   public ShulkerAttackGoal setAttributes(int attackIntervalMin, int attackIntervalMax) {
      this.attackIntervalMin = attackIntervalMin;
      this.attackIntervalMax = attackIntervalMax;
      return this;
   }

   public boolean canUse() {
      LivingEntity livingEntity = this.boss.getTarget();
      return livingEntity != null && livingEntity.isAlive();
   }

   public void start() {
      this.attackTime = this.boss.getLevel().getRandom().nextInt(this.attackIntervalMin, this.attackIntervalMax);
   }

   public boolean requiresUpdateEveryTick() {
      return true;
   }

   public void tick() {
      this.attackTime--;
      LivingEntity livingentity = this.boss.getTarget();
      if (livingentity != null) {
         this.boss.getLookControl().setLookAt(livingentity, 180.0F, 180.0F);
         double distanceSqr = this.boss.distanceToSqr(livingentity);
         if (distanceSqr < 400.0) {
            if (this.attackTime <= 0) {
               this.attackTime = 20 + this.boss.getRandom().nextInt(10) * 20 / 2;
               this.boss.level.addFreshEntity(new ShulkerBullet(this.boss.level, this.boss, livingentity, Axis.Y));
               this.boss.playSound(SoundEvents.SHULKER_SHOOT, 2.0F, (this.boss.getRandom().nextFloat() - this.boss.getRandom().nextFloat()) * 0.2F + 1.0F);
            }
         } else {
            this.boss.setTarget(null);
         }

         super.tick();
      }
   }

   @Override
   public String getType() {
      return "shulker_bullet";
   }

   @Override
   public void apply(VaultBossEntity boss) {
      boss.addTraitGoal(this);
   }

   @Override
   public void addStack(ITrait trait) {
      if (trait instanceof ShulkerAttackGoal shulkerAttackGoal) {
         this.stackSize++;
         this.attackIntervalMin = shulkerAttackGoal.attackIntervalMin * this.stackSize;
      }
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putInt("AttackIntervalMin", this.attackIntervalMin);
      nbt.putInt("AttackIntervalMax", this.attackIntervalMax);
      nbt.putInt("StackSize", this.stackSize);
      return nbt;
   }

   @Override
   public void deserializeNBT(CompoundTag nbt, VaultBossBaseEntity boss) {
      this.attackIntervalMin = nbt.getInt("AttackIntervalMin");
      this.attackIntervalMax = nbt.getInt("AttackIntervalMax");
      this.stackSize = nbt.getInt("StackSize");
   }
}
