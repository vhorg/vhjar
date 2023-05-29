package iskallia.vault.util;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;

public class EntityHelper {
   private static final AABB BOX = new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);

   public static void changeHealth(LivingEntity entity, int healthChange) {
      float health = entity.getHealth();
      entity.setHealth(health + healthChange);
      if (entity.isDeadOrDying()) {
         entity.die(entity.getLastDamageSource() != null ? entity.getLastDamageSource() : DamageSource.GENERIC);
      }
   }

   public static void knockback(LivingEntity target, LivingEntity source) {
      double xDiff = source.getX() - target.getX();

      double zDiff;
      for (zDiff = source.getZ() - target.getZ(); xDiff * xDiff + zDiff * zDiff < 1.0E-4; zDiff = (Math.random() - Math.random()) * 0.01) {
         xDiff = (Math.random() - Math.random()) * 0.01;
      }

      target.hurtDir = (float)(Mth.atan2(zDiff, xDiff) * (180.0 / Math.PI) - target.getYRot());
      target.knockback(1.0, xDiff, zDiff);
   }

   public static void knockbackIgnoreResist(LivingEntity target, LivingEntity source, float strength) {
      if (target != null && source != null) {
         double xDiff = source.getX() - target.getX();

         double zDiff;
         for (zDiff = source.getZ() - target.getZ(); xDiff * xDiff + zDiff * zDiff < 1.0E-4; zDiff = (Math.random() - Math.random()) * 0.01) {
            xDiff = (Math.random() - Math.random()) * 0.01;
         }

         target.hurtDir = (float)(Mth.atan2(zDiff, xDiff) * (180.0 / Math.PI) - target.getYRot());
         LivingKnockBackEvent event = ForgeHooks.onLivingKnockBack(target, strength, xDiff, zDiff);
         if (!event.isCanceled()) {
            strength = event.getStrength();
            xDiff = event.getRatioX();
            zDiff = event.getRatioZ();
            target.hasImpulse = true;
            Vec3 vec3 = target.getDeltaMovement();
            Vec3 vec31 = new Vec3(xDiff, 0.0, zDiff).normalize().scale(strength);
            target.setDeltaMovement(vec3.x / 2.0 - vec31.x, target.isOnGround() ? Math.min(0.4, vec3.y / 2.0 + strength) : vec3.y, vec3.z / 2.0 - vec31.z);
         }
      }
   }

   public static void knockbackWithStrength(LivingEntity target, LivingEntity source, float strength) {
      double xDiff = source.getX() - target.getX();

      double zDiff;
      for (zDiff = source.getZ() - target.getZ(); xDiff * xDiff + zDiff * zDiff < 1.0E-4; zDiff = (Math.random() - Math.random()) * 0.01) {
         xDiff = (Math.random() - Math.random()) * 0.01;
      }

      target.hurtDir = (float)(Mth.atan2(zDiff, xDiff) * (180.0 / Math.PI) - target.getYRot());
      target.knockback(strength, xDiff, zDiff);
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

   public static void getEntitiesInRange(LevelAccessor levelAccessor, Vec3 center, float range, Predicate<Entity> filter, List<LivingEntity> result) {
      getEntitiesInRange(levelAccessor, AABBHelper.create(center, range + 4.0F), center, range, filter, result);
   }

   public static void getEntitiesInRange(LevelAccessor levelAccessor, AABB area, Vec3 center, float range, Predicate<Entity> filter, List<LivingEntity> result) {
      if (levelAccessor != null) {
         for (Entity entity : levelAccessor.getEntities((Entity)null, area, filter)) {
            if (MathUtilities.isAABBIntersectingOrInsideSphere(entity.getBoundingBox(), center, range)) {
               result.add((LivingEntity)entity);
            }
         }
      }
   }
}
