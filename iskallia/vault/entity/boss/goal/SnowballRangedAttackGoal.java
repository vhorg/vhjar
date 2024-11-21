package iskallia.vault.entity.boss.goal;

import iskallia.vault.entity.boss.VaultBossBaseEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.phys.EntityHitResult;

public class SnowballRangedAttackGoal extends RangedAttackGoalBase {
   public static final String TYPE = "snowball_ranged_attack";
   private float inaccuracy = 12.0F;
   private float damageMultiplier = 0.5F;

   public SnowballRangedAttackGoal(VaultBossBaseEntity boss) {
      super(boss, 1.0, 20, 25.0F, false);
   }

   public SnowballRangedAttackGoal setAttributes(
      double speedModifier,
      int attackIntervalMin,
      int attackIntervalMax,
      float attackRadius,
      float maxInaccuracy,
      float damageMultiplier,
      boolean attackWhenInMeleeRange
   ) {
      this.setAttackAttributes(speedModifier, attackIntervalMin, attackIntervalMax, attackRadius, attackWhenInMeleeRange);
      this.inaccuracy = maxInaccuracy;
      this.damageMultiplier = damageMultiplier;
      return this;
   }

   @Override
   protected void performRangedAttack(LivingEntity target, float f1) {
      Snowball snowball = new Snowball(this.boss.getLevel(), this.boss) {
         protected void onHitEntity(EntityHitResult pResult) {
            Entity entity = pResult.getEntity();
            entity.hurt(
               DamageSource.thrown(this, this.getOwner()),
               (float)(SnowballRangedAttackGoal.this.boss.getAttributeValue(Attributes.ATTACK_DAMAGE) * SnowballRangedAttackGoal.this.damageMultiplier)
            );
         }
      };
      snowball.setPos(snowball.getX(), this.boss.getY(0.5) + 0.5, snowball.getZ());
      double targetY = target.getEyeY() - 1.1F;
      double vectorX = target.getX() - this.boss.getX();
      double vectorY = targetY - snowball.getY();
      double vectorZ = target.getZ() - this.boss.getZ();
      double gravityAdjustment = Math.sqrt(vectorX * vectorX + vectorZ * vectorZ) * 0.2F;
      snowball.shoot(vectorX, vectorY + gravityAdjustment, vectorZ, 1.6F, this.inaccuracy);
      this.boss.getLevel().addFreshEntity(snowball);
   }

   @Override
   public String getType() {
      return "snowball_ranged_attack";
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag nbt = super.serializeNBT();
      nbt.putFloat("DamageMultiplier", this.damageMultiplier);
      nbt.putFloat("Inaccuracy", this.inaccuracy);
      return nbt;
   }

   @Override
   public void deserializeNBT(CompoundTag nbt, VaultBossBaseEntity boss) {
      super.deserializeNBT(nbt, boss);
      this.damageMultiplier = nbt.getFloat("DamageMultiplier");
      this.inaccuracy = nbt.getFloat("Inaccuracy");
   }

   public void start() {
      super.start();
      if (this.boss.getActiveAttackMove().isEmpty()) {
         this.boss.setActiveAttackMove("snowball_ranged_attack");
      }
   }

   @Override
   public void stop() {
      super.stop();
      if (this.boss.getActiveAttackMove().map(attackMove -> attackMove.equals("snowball_ranged_attack")).orElse(false)) {
         this.boss.setActiveAttackMove("");
      }
   }
}
