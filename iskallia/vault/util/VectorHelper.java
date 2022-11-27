package iskallia.vault.util;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class VectorHelper {
   public static Vec3 getMovementVelocity(Vec3 current, Vec3 target, float speed) {
      return target.subtract(current).normalize().scale(speed);
   }

   public static Vec2 rotateDegrees(Vec2 v, float angleDeg) {
      float angle = (float)Math.toRadians(angleDeg);
      float cosAngle = Mth.cos(angle);
      float sinAngle = Mth.sin(angle);
      return new Vec2(v.x * cosAngle - v.y * sinAngle, v.x * sinAngle + v.y * cosAngle);
   }
}
