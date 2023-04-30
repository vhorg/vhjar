package iskallia.vault.util;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
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

   public static Vec3 getRandomPointOnSphere(double xOrigin, double yOrigin, double zOrigin, double radius, Random random) {
      float theta = (float)((Math.PI * 2) * random.nextDouble());
      double u = 2.0 * random.nextDouble() - 1.0;
      double s = Math.sqrt(1.0 - u * u) * radius;
      return new Vec3(xOrigin + s * Mth.cos(theta), yOrigin + s * Mth.sin(theta), zOrigin + u * radius);
   }

   public static Vec3 getRandomPointOnCircle(double xOrigin, double yOrigin, double zOrigin, double radius, Random random) {
      float theta = (float)((Math.PI * 2) * random.nextDouble());
      return new Vec3(xOrigin + Mth.cos(theta) * radius, yOrigin, zOrigin + Mth.sin(theta) * radius);
   }

   public static double getDistance(double x0, double y0, double x1, double y1) {
      return Math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0));
   }

   public static double getDistanceSqr(BlockPos blockPos0, BlockPos blockPos1) {
      return getDistanceSqr(blockPos0.getX(), blockPos0.getY(), blockPos0.getZ(), blockPos1.getX(), blockPos1.getY(), blockPos1.getZ());
   }

   public static double getDistanceSqr(double x0, double y0, double z0, double x1, double y1, double z1) {
      return (x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0) + (z1 - z0) * (z1 - z0);
   }

   public static boolean isAABBIntersectingOrInsideSphere(AABB aabb, Vec3 sphereCenter, double sphereRadius) {
      return isAABBIntersectingOrInsideSphere(aabb, sphereCenter.x, sphereCenter.y, sphereCenter.z, sphereRadius);
   }

   public static boolean isAABBIntersectingOrInsideSphere(AABB aabb, double xSphereCenter, double ySphereCenter, double zSphereCenter, double sphereRadius) {
      double dmin = 0.0;
      if (xSphereCenter < aabb.minX) {
         dmin += (xSphereCenter - aabb.minX) * (xSphereCenter - aabb.minX);
      } else if (xSphereCenter > aabb.maxX) {
         dmin += (xSphereCenter - aabb.maxX) * (xSphereCenter - aabb.maxX);
      }

      if (ySphereCenter < aabb.minY) {
         dmin += (ySphereCenter - aabb.minY) * (ySphereCenter - aabb.minY);
      } else if (ySphereCenter > aabb.maxY) {
         dmin += (ySphereCenter - aabb.maxY) * (ySphereCenter - aabb.maxY);
      }

      if (zSphereCenter < aabb.minZ) {
         dmin += (zSphereCenter - aabb.minZ) * (zSphereCenter - aabb.minZ);
      } else if (zSphereCenter > aabb.maxZ) {
         dmin += (zSphereCenter - aabb.maxZ) * (zSphereCenter - aabb.maxZ);
      }

      return dmin <= sphereRadius * sphereRadius;
   }
}
