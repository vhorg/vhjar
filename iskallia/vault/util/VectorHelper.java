package iskallia.vault.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

public class VectorHelper {
   public static Vector3d getDirectionNormalized(Vector3d destination, Vector3d origin) {
      return new Vector3d(
            destination.field_72450_a - origin.field_72450_a,
            destination.field_72448_b - origin.field_72448_b,
            destination.field_72449_c - origin.field_72449_c
         )
         .func_72432_b();
   }

   public static Vector3d getVectorFromPos(BlockPos pos) {
      return new Vector3d(pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p());
   }

   public static Vector3d add(Vector3d a, Vector3d b) {
      return new Vector3d(a.field_72450_a + b.field_72450_a, a.field_72448_b + b.field_72448_b, a.field_72449_c + b.field_72449_c);
   }

   public static Vector3d subtract(Vector3d a, Vector3d b) {
      return new Vector3d(a.field_72450_a - b.field_72450_a, a.field_72448_b - b.field_72448_b, a.field_72449_c - b.field_72449_c);
   }

   public static Vector3d multiply(Vector3d velocity, float speed) {
      return new Vector3d(velocity.field_72450_a * speed, velocity.field_72448_b * speed, velocity.field_72449_c * speed);
   }

   public static Vector3d getMovementVelocity(Vector3d current, Vector3d target, float speed) {
      return multiply(getDirectionNormalized(target, current), speed);
   }

   public static Vector2f normalize(Vector2f v) {
      float length = (float)Math.sqrt(v.field_189982_i * v.field_189982_i + v.field_189983_j * v.field_189983_j);
      return new Vector2f(v.field_189982_i / length, v.field_189983_j / length);
   }

   public static Vector2f rotateDegrees(Vector2f v, float angleDeg) {
      float angle = (float)Math.toRadians(angleDeg);
      float cosAngle = MathHelper.func_76134_b(angle);
      float sinAngle = MathHelper.func_76126_a(angle);
      return new Vector2f(v.field_189982_i * cosAngle - v.field_189983_j * sinAngle, v.field_189982_i * sinAngle + v.field_189983_j * cosAngle);
   }
}
