package iskallia.vault.entity.ai.eyesore;

import iskallia.vault.entity.EyesoreEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;

public class EyesorePath {
   public Vector3d target;
   public double speed;

   public void stayInRange(Vector3d center, Entity other, double speed, double distance, double error) {
      this.speed = speed;
      if (this.target != null && !(Math.abs(this.target.func_72438_d(other.func_213303_ch()) - distance) > error)) {
         this.target = null;
      } else {
         Vector3d dir = center.func_178788_d(other.func_213303_ch());
         this.target = dir.func_72432_b().func_186678_a(distance).func_178787_e(other.func_213303_ch());
      }
   }

   public void stayInRange(EyesoreEntity entity, Entity other, double speed, double distance, double error) {
      this.speed = speed;
      if (this.target != null && !(Math.abs(this.target.func_72438_d(other.func_213303_ch()) - distance) > error)) {
         this.target = null;
      } else {
         Vector3d dir = entity.func_213303_ch().func_178788_d(other.func_213303_ch());
         this.target = dir.func_72432_b().func_186678_a(distance).func_178787_e(other.func_213303_ch());
      }
   }

   public void tick(EyesoreEntity entity) {
      if (this.target != null) {
         if (this.target.func_72438_d(entity.func_213303_ch()) <= 1.0) {
            entity.func_213317_d(entity.func_213322_ci().func_186678_a(0.1));
         } else {
            entity.func_213317_d(this.target.func_178788_d(entity.func_213303_ch()).func_72432_b().func_186678_a(this.speed));
         }
      }
   }
}
