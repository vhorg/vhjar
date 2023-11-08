package iskallia.vault.entity.boss;

import java.util.Optional;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public interface IMeleeAttack {
   TargetingConditions PLAYERS_CLOSE_TARGETING_CONDITIONS = TargetingConditions.forCombat().range(2.0);
   TargetingConditions PLAYERS_HIT_TARGETING_CONDITIONS = TargetingConditions.forCombat().range(7.0);

   boolean start(LivingEntity var1, double var2, double var4);

   void stop();

   void tick(double var1);

   int getDuration();

   Optional<ArtifactBossEntity.AttackMove> getAttackMove();
}
