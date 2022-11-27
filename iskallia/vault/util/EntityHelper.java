package iskallia.vault.util;

import java.util.List;
import net.minecraft.core.Vec3i;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;

public class EntityHelper {
   private static final AABB BOX = new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);

   public static void changeHealth(LivingEntity entity, int healthChange) {
      float health = entity.getHealth();
      entity.setHealth(health + healthChange);
      if (entity.isDeadOrDying()) {
         entity.die(entity.getLastDamageSource() != null ? entity.getLastDamageSource() : DamageSource.GENERIC);
      }
   }

   public static <T extends Entity> T changeSize(T entity, float size, Runnable callback) {
      changeSize(entity, size);
      callback.run();
      return entity;
   }

   public static <T extends Entity> T changeSize(T entity, float size) {
      entity.dimensions = entity.getDimensions(Pose.STANDING).scale(size);
      entity.refreshDimensions();
      return entity;
   }

   public static void giveItem(Player player, ItemStack itemStack) {
      boolean added = player.getInventory().add(itemStack);
      if (!added) {
         player.drop(itemStack, false, false);
      }
   }

   public static <T extends Entity> List<T> getNearby(LevelAccessor world, Vec3i pos, float radius, Class<T> entityClass) {
      AABB selectBox = BOX.move(pos.getX(), pos.getY(), pos.getZ()).inflate(radius);
      return world.getEntitiesOfClass(entityClass, selectBox, entity -> entity.isAlive() && !entity.isSpectator());
   }
}
