package iskallia.vault.entity.boss.attack;

import iskallia.vault.entity.boss.VaultBossBaseEntity;
import java.util.Optional;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;

public interface IMeleeAttack {
   TargetingConditions PLAYERS_CLOSE_TARGETING_CONDITIONS = TargetingConditions.forCombat().range(3.0);
   TargetingConditions PLAYERS_HIT_TARGETING_CONDITIONS = TargetingConditions.forCombat().range(7.0);

   boolean start(LivingEntity var1, double var2, double var4);

   void stop();

   void tick(double var1);

   int getDuration();

   Optional<BossAttackMove> getAttackMove();

   default boolean isWithinAttackableSlice(VaultBossBaseEntity boss, LivingEntity target, float closenessRatioRequired, float angleOffset) {
      if (target == null) {
         return false;
      } else {
         Vec3 bossViewVector = boss.calculateViewVector(boss.getViewYRot(1.0F) + angleOffset).normalize();
         Vec3 positionsVector = new Vec3(target.getX() - boss.getX(), target.getEyeY() - boss.getEyeY(), target.getZ() - boss.getZ());
         positionsVector = positionsVector.normalize();
         double closenessRatio = bossViewVector.dot(positionsVector);
         return closenessRatio > closenessRatioRequired;
      }
   }

   default void knockbackTarget(VaultBossBaseEntity boss, LivingEntity target, double horizontalKnockbackMultiplier, double verticalKnockbackMultiplier) {
      double strength = boss.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
      double ratioX = boss.getX() - target.getX();

      double ratioZ;
      for (ratioZ = boss.getZ() - target.getZ(); ratioX * ratioX + ratioZ * ratioZ < 1.0E-4; ratioZ = (Math.random() - Math.random()) * 0.01) {
         ratioX = (Math.random() - Math.random()) * 0.01;
      }

      LivingKnockBackEvent event = ForgeHooks.onLivingKnockBack(target, (float)strength, ratioX, ratioZ);
      if (!event.isCanceled()) {
         strength = event.getStrength();
         ratioX = event.getRatioX();
         ratioZ = event.getRatioZ();
         strength *= 1.0 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE) / 2.0;
         if (strength > 0.0) {
            target.hasImpulse = true;
            Vec3 vec3 = target.getDeltaMovement();
            Vec3 vec31 = new Vec3(ratioX, 0.0, ratioZ).normalize().scale(strength * horizontalKnockbackMultiplier);
            target.setDeltaMovement(vec3.x / 2.0 - vec31.x, vec3.y / 2.0 + strength * verticalKnockbackMultiplier, vec3.z / 2.0 - vec31.z);
         }
      }
   }
}
