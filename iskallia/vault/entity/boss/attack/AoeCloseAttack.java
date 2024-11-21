package iskallia.vault.entity.boss.attack;

import iskallia.vault.entity.boss.VaultBossBaseEntity;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class AoeCloseAttack implements IMeleeAttack {
   private static final TargetingConditions TARGETING_CONDITIONS = TargetingConditions.forCombat().range(3.0);
   private static final TargetingConditions TARGETING_CONDITIONS_INSIDE = TargetingConditions.forCombat().range(1.0);
   private static final TargetingConditions TARGETING_CONDITIONS_WIDER = TargetingConditions.forCombat().range(7.0);
   private final VaultBossBaseEntity boss;
   private final double damageMultiplier;
   private long attackStartTime;
   private final Set<UUID> hitEntities = new HashSet<>();

   public AoeCloseAttack(VaultBossBaseEntity boss, double damageMultiplier) {
      this.boss = boss;
      this.damageMultiplier = damageMultiplier;
   }

   @Override
   public boolean start(LivingEntity target, double distToTarget, double reach) {
      if (this.boss.getLevel().getNearbyEntities(LivingEntity.class, TARGETING_CONDITIONS, this.boss, this.boss.getBoundingBox().inflate(5.0)).isEmpty()) {
         return false;
      } else {
         this.attackStartTime = this.boss.getLevel().getGameTime();
         return true;
      }
   }

   @Override
   public void stop() {
      this.attackStartTime = 0L;
      this.hitEntities.clear();
   }

   @Override
   public void tick(double reach) {
      long duration = this.boss.getLevel().getGameTime() - this.attackStartTime;
      int startTick = 20;
      if (duration >= startTick && duration < this.getDuration()) {
         List<LivingEntity> entities = this.boss
            .getLevel()
            .getNearbyEntities(LivingEntity.class, TARGETING_CONDITIONS_WIDER, this.boss, this.boss.getBoundingBox().inflate(7.0));
         entities.forEach(
            entity -> {
               if (!this.hitEntities.contains(entity.getUUID())
                  && (
                     TARGETING_CONDITIONS_INSIDE.test(this.boss, entity)
                        || this.isWithinAttackableSlice(
                           this.boss, entity, 0.6F, -0.1F + (float)(360L * (duration - startTick)) / (this.getDuration() - startTick)
                        )
                  )) {
                  entity.hasImpulse = true;
                  this.knockbackTarget(this.boss, entity, 5.0, 0.1F);
                  entity.hurt(DamageSource.mobAttack(this.boss), (float)(this.boss.getAttributeValue(Attributes.ATTACK_DAMAGE) * this.damageMultiplier));
                  entity.hurtMarked = true;
                  this.hitEntities.add(entity.getUUID());
               }
            }
         );
      }
   }

   @Override
   public int getDuration() {
      return 30;
   }

   @Override
   public Optional<String> getAttackMove() {
      return Optional.of("aoeclose");
   }
}
