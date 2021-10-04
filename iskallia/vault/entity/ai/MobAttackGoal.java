package iskallia.vault.entity.ai;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;

public class MobAttackGoal extends MeleeAttackGoal {
   public MobAttackGoal(CreatureEntity creature, double speedIn, boolean useLongMemory) {
      super(creature, speedIn, useLongMemory);
   }

   protected double func_179512_a(LivingEntity attackTarget) {
      return 4.0F + attackTarget.func_213311_cf();
   }
}
