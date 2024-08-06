package iskallia.vault.entity.boss.attack;

import iskallia.vault.entity.boss.VaultBossBaseEntity;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;

public class ThrowAttack implements IMeleeAttack {
   private static final TargetingConditions TARGETING_CONDITIONS = TargetingConditions.forCombat().range(2.0);
   private static final TargetingConditions TARGETING_CONDITIONS_WIDER = TargetingConditions.forCombat().range(7.0);
   private final VaultBossBaseEntity artifactBossEntity;
   private final double damageMultiplier;
   private boolean hasThrown = false;
   private int throwCooldown = 0;
   private final Set<UUID> playersThrown = new HashSet<>();

   public ThrowAttack(VaultBossBaseEntity artifactBossEntity, double damageMultiplier) {
      this.artifactBossEntity = artifactBossEntity;
      this.damageMultiplier = damageMultiplier;
   }

   @Override
   public boolean start(LivingEntity target, double distToTarget, double reach) {
      List<Player> players = this.artifactBossEntity
         .getLevel()
         .getNearbyPlayers(TARGETING_CONDITIONS, this.artifactBossEntity, this.artifactBossEntity.getBoundingBox().inflate(5.0));
      if (!players.isEmpty()) {
         this.throwCooldown = 30;
         players.forEach(player -> {
            player.hasImpulse = true;
            player.setNoGravity(true);
            player.setDeltaMovement(player.position().subtract(this.artifactBossEntity.position()).normalize().multiply(0.3, 0.0, 0.3).add(0.0, 0.1, 0.0));
            player.hurtMarked = true;
            this.playersThrown.add(player.getUUID());
         });
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void stop() {
      this.hasThrown = false;
      this.playersThrown.forEach(playerId -> {
         Player player = this.artifactBossEntity.getLevel().getPlayerByUUID(playerId);
         if (player != null) {
            player.setNoGravity(false);
         }
      });
      this.playersThrown.clear();
   }

   @Override
   public void tick(double reach) {
      if (!this.hasThrown && --this.throwCooldown <= 0) {
         List<Player> players = this.artifactBossEntity
            .getLevel()
            .getNearbyPlayers(TARGETING_CONDITIONS_WIDER, this.artifactBossEntity, this.artifactBossEntity.getBoundingBox().inflate(7.0));
         players.forEach(player -> {
            if (this.playersThrown.contains(player.getUUID())) {
               player.setNoGravity(false);
               player.hasImpulse = true;
               player.setDeltaMovement(player.position().subtract(this.artifactBossEntity.position()).normalize().multiply(10.0, 1.0, 10.0).add(0.0, 0.2, 0.0));
               player.hurtMarked = true;
               this.playersThrown.remove(player.getUUID());
            }
         });
         this.playersThrown.forEach(playerId -> {
            Player player = this.artifactBossEntity.getLevel().getPlayerByUUID(playerId);
            if (player != null) {
               player.setNoGravity(false);
            }
         });
         this.hasThrown = true;
      }
   }

   @Override
   public int getDuration() {
      return 50;
   }

   @Override
   public Optional<BossAttackMove> getAttackMove() {
      return Optional.of(BossAttackMove.SUMMON);
   }
}
