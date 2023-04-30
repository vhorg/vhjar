package iskallia.vault.util;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class AABBHelper {
   public static AABB create(Vec3 center, float radius) {
      return create(center, radius, radius, radius);
   }

   public static AABB create(Vec3 center, float xRadius, float yRadius, float zRadius) {
      return new AABB(center.x - xRadius, center.y - yRadius, center.z - zRadius, center.x + xRadius, center.y + yRadius, center.z + zRadius);
   }

   private AABBHelper() {
   }
}
