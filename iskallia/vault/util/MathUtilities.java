package iskallia.vault.util;

import java.util.Random;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

public class MathUtilities {
   public static float randomFloat(float min, float max) {
      return new Random().nextFloat() * (max - min) + min;
   }

   public static int getRandomInt(int min, int max) {
      return (int)(Math.random() * (max - min) + min);
   }

   public static double map(double value, double x0, double y0, double x1, double y1) {
      return x1 + (y1 - x1) * ((value - x0) / (y0 - x0));
   }

   public static double length(Vector2f vec) {
      return Math.sqrt(vec.field_189982_i * vec.field_189982_i + vec.field_189983_j * vec.field_189983_j);
   }

   public static double extractYaw(Vector3d vec) {
      return Math.atan2(vec.func_82616_c(), vec.func_82615_a());
   }

   public static double extractPitch(Vector3d vec) {
      return Math.asin(vec.func_82617_b() / vec.func_72433_c());
   }

   public static Vector3d rotatePitch(Vector3d vec, float pitch) {
      float f = MathHelper.func_76134_b(pitch);
      float f1 = MathHelper.func_76126_a(pitch);
      double d0 = vec.func_82615_a();
      double d1 = vec.func_82617_b() * f + vec.func_82616_c() * f1;
      double d2 = vec.func_82616_c() * f - vec.func_82617_b() * f1;
      return new Vector3d(d0, d1, d2);
   }

   public static Vector3d rotateYaw(Vector3d vec, float yaw) {
      float f = MathHelper.func_76134_b(yaw);
      float f1 = MathHelper.func_76126_a(yaw);
      double d0 = vec.func_82615_a() * f + vec.func_82616_c() * f1;
      double d1 = vec.func_82617_b();
      double d2 = vec.func_82616_c() * f - vec.func_82615_a() * f1;
      return new Vector3d(d0, d1, d2);
   }

   public static Vector3d rotateRoll(Vector3d vec, float roll) {
      float f = MathHelper.func_76134_b(roll);
      float f1 = MathHelper.func_76126_a(roll);
      double d0 = vec.func_82615_a() * f + vec.func_82617_b() * f1;
      double d1 = vec.func_82617_b() * f - vec.func_82615_a() * f1;
      double d2 = vec.func_82616_c();
      return new Vector3d(d0, d1, d2);
   }
}
