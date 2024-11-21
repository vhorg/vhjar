package iskallia.vault.entity.boss.goal;

import iskallia.vault.entity.boss.GolemBossEntity;
import iskallia.vault.entity.boss.GolemHandProjectileEntity;
import iskallia.vault.entity.boss.VaultBossBaseEntity;
import iskallia.vault.init.ModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

public class GolemHandRangedAttackGoal extends RangedAttackGoalBase {
   public static final String TYPE = "golem_hand_ranged_attack";
   private float damageMultiplier = 0.5F;

   public GolemHandRangedAttackGoal(VaultBossBaseEntity boss) {
      super(boss, 1.0, 20, 25.0F, false);
      this.setPreAttackAnimationCooldown(13);
   }

   public GolemHandRangedAttackGoal setAttributes(
      double speedModifier,
      int attackIntervalMin,
      int attackIntervalMax,
      float attackRadius,
      float maxInaccuracy,
      float damageMultiplier,
      boolean attackWhenInMeleeRange
   ) {
      this.setAttackAttributes(speedModifier, attackIntervalMin, attackIntervalMax, attackRadius, attackWhenInMeleeRange);
      this.damageMultiplier = damageMultiplier;
      return this;
   }

   @Override
   protected void playPreAttackAnimation() {
      if (this.boss instanceof GolemBossEntity golem) {
         if (golem.getRandom().nextFloat() > 0.5F) {
            golem.setLaunchingLeftHand(true);
         } else {
            golem.setLaunchingRightHand(true);
         }
      }
   }

   @Override
   protected void performRangedAttack(LivingEntity target, float f1) {
      this.boss.level.playSound(null, this.boss.blockPosition(), ModSounds.ARTIFACT_BOSS_MAGIC_ATTACK, SoundSource.HOSTILE, 1.0F, 1.0F);
      float handOffset = this.boss instanceof GolemBossEntity golem ? (golem.isLaunchingRightHand() ? 1.0F : -1.0F) : 0.0F;
      float radiansHeadRot = (float)(this.boss.getYHeadRot() * Math.PI / 180.0);
      double shotPosX = this.boss.getX() - handOffset * Mth.cos(radiansHeadRot) - Mth.sin(radiansHeadRot) * 1.5;
      double shotPosY = this.boss.getY(0.8);
      double shotPosZ = this.boss.getZ() - handOffset * Mth.sin(radiansHeadRot) + Mth.cos(radiansHeadRot) * 1.5;
      double vectorX = target.getX() - shotPosX;
      double vectorY = target.getY(0.5) - shotPosY;
      double vectorZ = target.getZ() - shotPosZ;
      boolean isRightHand = this.boss instanceof GolemBossEntity golemx && golemx.isLaunchingRightHand();
      GolemHandProjectileEntity handProjectile = new GolemHandProjectileEntity(
         this.boss.getLevel(),
         this.boss,
         vectorX,
         vectorY,
         vectorZ,
         target,
         (float)(this.boss.getAttributeValue(Attributes.ATTACK_DAMAGE) * this.damageMultiplier),
         isRightHand
      );
      handProjectile.setPos(shotPosX, shotPosY, shotPosZ);
      handProjectile.setDeltaMovement(Vec3.ZERO.add(handProjectile.xPower, handProjectile.yPower, handProjectile.zPower).scale(0.95));
      this.boss.getLevel().addFreshEntity(handProjectile);
      if (this.boss instanceof GolemBossEntity golemxx) {
         if (golemxx.isLaunchingRightHand()) {
            golemxx.setLaunchingRightHand(false);
            golemxx.setShowRightHand(false);
         } else {
            golemxx.setLaunchingLeftHand(false);
            golemxx.setShowLeftHand(false);
         }
      }
   }

   @Override
   public String getType() {
      return "golem_hand_ranged_attack";
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag nbt = super.serializeNBT();
      nbt.putFloat("DamageMultiplier", this.damageMultiplier);
      return nbt;
   }

   @Override
   public void deserializeNBT(CompoundTag nbt, VaultBossBaseEntity boss) {
      super.deserializeNBT(nbt, boss);
      this.damageMultiplier = nbt.getFloat("DamageMultiplier");
   }

   public void start() {
      super.start();
      if (this.boss.getActiveAttackMove().isEmpty()) {
         this.boss.setActiveAttackMove("golem_hand_ranged_attack");
      }
   }

   @Override
   public void stop() {
      super.stop();
      if (this.boss.getActiveAttackMove().map(attackMove -> attackMove.equals("golem_hand_ranged_attack")).orElse(false)) {
         this.boss.setActiveAttackMove("");
      }
   }
}
