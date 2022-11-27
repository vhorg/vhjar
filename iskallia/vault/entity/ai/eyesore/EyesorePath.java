package iskallia.vault.entity.ai.eyesore;

import iskallia.vault.entity.entity.eyesore.EyesoreEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class EyesorePath {
   public Vec3 target;
   public double speed;

   public void stayInRange(EyesoreEntity entity, Entity other, double speed, double distance, double error) {
      this.speed = speed;
      if (this.target != null && !(Math.abs(this.target.distanceTo(other.position()) - distance) > error)) {
         this.target = null;
      } else {
         Vec3 dir = entity.position().subtract(other.position());
         this.target = dir.normalize().scale(distance).add(other.position());
      }
   }

   public void tick(EyesoreEntity entity) {
      if (this.target != null) {
         if (this.target.distanceTo(entity.position()) <= 1.0) {
            entity.setDeltaMovement(entity.getDeltaMovement().scale(0.1));
         } else {
            entity.setDeltaMovement(this.target.subtract(entity.position()).normalize().scale(this.speed));
         }
      }
   }
}
