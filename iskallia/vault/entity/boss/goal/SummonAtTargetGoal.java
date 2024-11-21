package iskallia.vault.entity.boss.goal;

import iskallia.vault.entity.boss.VaultBossBaseEntity;
import net.minecraft.world.phys.Vec3;

public class SummonAtTargetGoal extends SummonGoal {
   public static final String TYPE = "summon_at_target";

   public SummonAtTargetGoal(VaultBossBaseEntity boss) {
      super(boss);
   }

   @Override
   public String getType() {
      return "summon_at_target";
   }

   @Override
   public boolean canUse() {
      return super.canUse() && this.boss.getTarget() != null;
   }

   @Override
   protected Vec3 getSummonCenter() {
      return this.boss.getTarget() != null ? this.boss.getTarget().position() : super.getSummonCenter();
   }
}
