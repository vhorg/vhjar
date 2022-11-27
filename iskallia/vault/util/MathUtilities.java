package iskallia.vault.util;

import java.util.Random;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class MathUtilities {
   private static final Random rand = new Random();

   public static float randomFloat(float min, float max) {
      return min >= max ? min : min + rand.nextFloat() * (max - min);
   }

   public static int getRandomInt(int min, int max) {
      return min >= max ? min : min + rand.nextInt(max - min);
   }

   public static double map(double value, double x0, double y0, double x1, double y1) {
      return x1 + (y1 - x1) * ((value - x0) / (y0 - x0));
   }

   public static double length(Vec2 vec) {
      return Math.sqrt(vec.x * vec.x + vec.y * vec.y);
   }

   public static double extractYaw(Vec3 vec) {
      return Math.atan2(vec.z(), vec.x());
   }

   public static double extractPitch(Vec3 vec) {
      return Math.asin(vec.y() / vec.length());
   }

   public static Vec3 rotatePitch(Vec3 vec, float pitch) {
      float f = Mth.cos(pitch);
      float f1 = Mth.sin(pitch);
      double d0 = vec.x();
      double d1 = vec.y() * f + vec.z() * f1;
      double d2 = vec.z() * f - vec.y() * f1;
      return new Vec3(d0, d1, d2);
   }

   public static Vec3 rotateYaw(Vec3 vec, float yaw) {
      float f = Mth.cos(yaw);
      float f1 = Mth.sin(yaw);
      double d0 = vec.x() * f + vec.z() * f1;
      double d1 = vec.y();
      double d2 = vec.z() * f - vec.x() * f1;
      return new Vec3(d0, d1, d2);
   }

   public static Vec3 rotateRoll(Vec3 vec, float roll) {
      float f = Mth.cos(roll);
      float f1 = Mth.sin(roll);
      double d0 = vec.x() * f + vec.y() * f1;
      double d1 = vec.y() * f - vec.x() * f1;
      double d2 = vec.z();
      return new Vec3(d0, d1, d2);
   }
}
