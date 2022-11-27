package iskallia.vault.entity.ai;

import iskallia.vault.entity.entity.AggressiveCowEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class CowDashAttackGoal extends Goal {
   protected final AggressiveCowEntity entity;
   private final float dashStrength;

   public CowDashAttackGoal(AggressiveCowEntity entity, float dashStrength) {
      this.entity = entity;
      this.dashStrength = 0.4F;
   }

   public boolean canUse() {
      LivingEntity target = this.entity.getTarget();
      if (!(target instanceof Player) || !target.isAlive()) {
         return false;
      } else if (!this.entity.canDash()) {
         return false;
      } else {
         double dist = this.entity.distanceToSqr(target.getX(), target.getY(), target.getZ());
         double attackReach = this.entity.getBbWidth() * 2.0F * this.entity.getBbWidth() * 2.0F + target.getBbWidth();
         return dist >= attackReach * 4.0 && dist <= attackReach * 16.0;
      }
   }

   public boolean canContinueToUse() {
      return false;
   }

   public void tick() {
      LivingEntity target = this.entity.getTarget();
      if (target instanceof Player && target.isAlive()) {
         Vec3 dir = target.getEyePosition(1.0F).subtract(this.entity.position());
         dir = dir.multiply(this.dashStrength, this.dashStrength, this.dashStrength);
         if (dir.y() <= 0.4) {
            dir = new Vec3(dir.x(), 0.4, dir.z());
         }

         this.entity.push(dir.x, dir.y, dir.z);
         this.entity.onDash();
      }
   }
}
