package iskallia.vault.entity.boss;

import iskallia.vault.VaultMod;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class MobSpawningUtils {
   public static final String TAG_BOSS_SUMMONED = "boss_summoned";

   @Nullable
   public static Entity spawnMob(ServerLevel serverLevel, double radius, EntityType<?> entityType, CompoundTag entityNbt, Vec3 spawnCenter) {
      return spawnMob(serverLevel, radius, 0, entityType, entityNbt, spawnCenter, false);
   }

   public static Entity spawnMob(
      ServerLevel serverLevel, double radius, int maxYOffset, EntityType<?> entityType, CompoundTag entityNbt, Vec3 spawnCenter, boolean checkCollision
   ) {
      return spawnMob(serverLevel, 0.0, radius, maxYOffset, entityType, entityNbt, spawnCenter, checkCollision);
   }

   public static Entity spawnMob(
      ServerLevel serverLevel,
      double minDistanceFromCenter,
      double radius,
      int maxYOffset,
      EntityType<?> entityType,
      CompoundTag entityNbt,
      Vec3 spawnCenter,
      boolean checkCollision
   ) {
      BlockPos spawnPos = getRandomSpawnPos(serverLevel, minDistanceFromCenter, radius, maxYOffset, entityType, spawnCenter, checkCollision);
      if (spawnPos == null) {
         return null;
      } else {
         Entity entity = entityType.spawn(serverLevel, null, null, spawnPos, MobSpawnType.SPAWNER, false, false);
         if (entity == null) {
            VaultMod.LOGGER.error("Unable to spawn entity type {} because its factory returned null", entityType.getRegistryName());
            return null;
         } else {
            if (entityNbt != null) {
               CompoundTag entityTag = entity.saveWithoutId(new CompoundTag());
               entityTag.merge(entityNbt.copy());
               entity.load(entityTag);
            }

            if (entity instanceof Mob) {
               ((Mob)entity).spawnAnim();
            }

            entity.getTags().add("boss_summoned");
            return entity;
         }
      }
   }

   private static BlockPos getRandomSpawnPos(
      ServerLevel serverLevel, double minDistanceFromCenter, double radius, int maxYOffset, EntityType<?> entityType, Vec3 spawnCenter, boolean checkCollision
   ) {
      double angle = (Math.PI * 2) * serverLevel.random.nextDouble();
      double distance = minDistanceFromCenter + (radius - minDistanceFromCenter) * serverLevel.random.nextDouble();
      double x = spawnCenter.x() + Math.cos(angle) * distance + 0.5;
      double y = spawnCenter.y() + (maxYOffset > 0 ? serverLevel.random.nextInt(2 * maxYOffset) - maxYOffset : 0);
      double z = spawnCenter.z() + Math.sin(angle) * distance + 0.5;

      int remainingTries;
      for (remainingTries = 20; checkCollision && remainingTries > 0 && !serverLevel.noCollision(entityType.getAABB(x, y, z)); remainingTries--) {
         angle = (Math.PI * 2) * serverLevel.random.nextDouble();
         distance = minDistanceFromCenter + (radius - minDistanceFromCenter) * serverLevel.random.nextDouble();
         x = spawnCenter.x() + Math.cos(angle) * distance + 0.5;
         y = spawnCenter.y() + (maxYOffset > 0 ? serverLevel.random.nextInt(2 * maxYOffset) - maxYOffset : 0);
         z = spawnCenter.z() + Math.sin(angle) * distance + 0.5;
      }

      return remainingTries == 0 ? null : new BlockPos(x, y, z);
   }

   public record EntitySpawnData(EntityType<?> entityType, @javax.annotation.Nullable CompoundTag entityNbt) {
      public static Optional<MobSpawningUtils.EntitySpawnData> from(CompoundTag tag) {
         EntityType<?> entityType = (EntityType<?>)ForgeRegistries.ENTITIES.getValue(new ResourceLocation(tag.getString("EntityType")));
         CompoundTag entityNbt = tag.contains("EntityNbt") ? tag.getCompound("EntityNbt") : null;
         return entityType == null ? Optional.empty() : Optional.of(new MobSpawningUtils.EntitySpawnData(entityType, entityNbt));
      }

      public CompoundTag serializeTo(CompoundTag tag) {
         tag.putString("EntityType", this.entityType.getRegistryName().toString());
         if (this.entityNbt != null) {
            tag.put("EntityNbt", this.entityNbt);
         }

         return tag;
      }
   }
}
