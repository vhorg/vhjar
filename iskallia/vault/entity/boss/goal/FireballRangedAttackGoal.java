package iskallia.vault.entity.boss.goal;

import iskallia.vault.entity.boss.VaultBossBaseEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.SmallFireball;

public class FireballRangedAttackGoal extends RangedAttackGoalBase {
   public static final String TYPE = "fireball_ranged_attack";
   private double maxInaccuracy = 0.5;

   public FireballRangedAttackGoal(VaultBossBaseEntity boss) {
      super(boss, 1.0, 20, 25.0F);
   }

   public FireballRangedAttackGoal setAttributes(double speedModifier, int attackIntervalMin, int attackIntervalMax, float attackRadius, double maxInaccuracy) {
      this.setAttackAttributes(speedModifier, attackIntervalMin, attackIntervalMax, attackRadius);
      this.maxInaccuracy = maxInaccuracy;
      return this;
   }

   @Override
   protected void performRangedAttack(LivingEntity target, float f1) {
      double distanceToSqr = this.boss.distanceToSqr(target);
      double vectorX = target.getX() - this.boss.getX();
      double vectorY = target.getY(0.5) - this.boss.getY(0.5);
      double vectorZ = target.getZ() - this.boss.getZ();
      double inaccuracy = Math.sqrt(Math.sqrt(distanceToSqr)) * this.maxInaccuracy;
      SmallFireball smallfireball = new SmallFireball(
         this.boss.getLevel(),
         this.boss,
         vectorX + this.boss.getRandom().nextGaussian() * inaccuracy,
         vectorY,
         vectorZ + this.boss.getRandom().nextGaussian() * inaccuracy
      );
      smallfireball.setPos(smallfireball.getX(), this.boss.getY(0.5) + 0.5, smallfireball.getZ());
      this.boss.getLevel().addFreshEntity(smallfireball);
   }

   @Override
   public String getType() {
      return "fireball_ranged_attack";
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag nbt = super.serializeNBT();
      nbt.putDouble("MaxInaccuracy", this.maxInaccuracy);
      return nbt;
   }

   @Override
   public void deserializeNBT(CompoundTag nbt) {
      super.deserializeNBT(nbt);
      this.maxInaccuracy = nbt.getDouble("MaxInaccuracy");
   }
}
