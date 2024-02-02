package iskallia.vault.entity.boss;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;

public class AoeCloseAttack implements IMeleeAttack {
   private static final TargetingConditions TARGETING_CONDITIONS = TargetingConditions.forCombat().range(3.0);
   private static final TargetingConditions TARGETING_CONDITIONS_INSIDE = TargetingConditions.forCombat().range(1.0);
   private static final TargetingConditions TARGETING_CONDITIONS_WIDER = TargetingConditions.forCombat().range(7.0);
   private final ArtifactBossEntity boss;
   private final double damageMultiplier;
   private long attackStartTime;
   private final Set<UUID> hitPlayers = new HashSet<>();

   public AoeCloseAttack(ArtifactBossEntity boss, double damageMultiplier) {
      this.boss = boss;
      this.damageMultiplier = damageMultiplier;
   }

   @Override
   public boolean start(LivingEntity target, double distToTarget, double reach) {
      if (this.boss.getLevel().getNearbyPlayers(TARGETING_CONDITIONS, this.boss, this.boss.getBoundingBox().inflate(5.0)).isEmpty()) {
         return false;
      } else {
         this.attackStartTime = this.boss.getLevel().getGameTime();
         return true;
      }
   }

   @Override
   public void stop() {
      this.attackStartTime = 0L;
      this.hitPlayers.clear();
   }

   @Override
   public void tick(double reach) {
      long duration = this.boss.getLevel().getGameTime() - this.attackStartTime;
      int startTick = 20;
      if (duration >= startTick && duration < this.getDuration()) {
         List<Player> players = this.boss.getLevel().getNearbyPlayers(TARGETING_CONDITIONS_WIDER, this.boss, this.boss.getBoundingBox().inflate(7.0));
         players.forEach(
            player -> {
               if (!this.hitPlayers.contains(player.getUUID())
                  && (
                     TARGETING_CONDITIONS_INSIDE.test(this.boss, player)
                        || this.isWithinAttackableSlice(
                           this.boss, player, 0.6F, -0.1F + (float)(360L * (duration - startTick)) / (this.getDuration() - startTick)
                        )
                  )) {
                  player.hasImpulse = true;
                  this.knockbackTarget(this.boss, player, 5.0, 0.1F);
                  player.hurt(DamageSource.mobAttack(this.boss), (float)(this.boss.getAttributeValue(Attributes.ATTACK_DAMAGE) * this.damageMultiplier));
                  player.hurtMarked = true;
                  this.hitPlayers.add(player.getUUID());
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
   public Optional<ArtifactBossEntity.AttackMove> getAttackMove() {
      return Optional.of(ArtifactBossEntity.AttackMove.AOECLOSE);
   }
}
