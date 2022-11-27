package iskallia.vault.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class MobAttackGoal extends MeleeAttackGoal {
   public MobAttackGoal(PathfinderMob creature, double speedIn, boolean useLongMemory) {
      super(creature, speedIn, useLongMemory);
   }

   protected double getAttackReachSqr(LivingEntity attackTarget) {
      return 4.0F + attackTarget.getBbWidth();
   }
}
